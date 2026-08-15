#include <cassert>
#include <cmath>
#include <random>
#include <algorithm>

#include "oscillator.h"
#include "frequencies.h"

constexpr float TWO_PI = float(2.0 * M_PI);
constexpr float GEIGER_CLICK_HZ = 2000.0f;
constexpr int GEIGER_CLICK_PERIOD_DURATION = 40;
constexpr int GEIGER_CLICK_RAMP_DURATION = 5;
constexpr float GEIGER_MAX_HZ = 2000.0f;
constexpr float WARNING_LOW_HZ = 2000.0f;
constexpr float WARNING_HIGH_HZ = 2.0f * WARNING_LOW_HZ;
constexpr float WARNING_SWITCH_SECONDS = 0.25f;
constexpr float WARNING_SWITCH_TOTAL_SECONDS = 2.0f * WARNING_SWITCH_SECONDS;

namespace mowave {

Oscillator::Oscillator(mowave::SensorManager* sensorManager) {
    mSensorManager = sensorManager;
}

Oscillator::~Oscillator() = default;

void Oscillator::setWaveFunction(
        WaveFunction waveFunction,
        bool isX,
        bool invert,
        bool stairs,
        float distortion,
        float exponential) {

    mIsX = isX;
    mInvert = invert;
    mStairs = stairs;
    mDistortion = distortion;
    mExponential = exponential;

    if (this->mWaveFunction != waveFunction) {
        this->mWaveFunction = waveFunction;
        mPhase = 0.0f;
    }
}

void Oscillator::setWaveRange(int min, int max) {
    mMinFrequency = min;
    mMaxFrequency = max;
}

void Oscillator::onAudioCreated(oboe::AudioStream * audioStream) {
    mLatencyTuner = std::make_unique<oboe::LatencyTuner>(*audioStream);
    mGeigerFramesPerPeriod = (float)audioStream->getSampleRate() / GEIGER_CLICK_HZ;
    mGeigerClickFrames = int(mGeigerFramesPerPeriod * GEIGER_CLICK_PERIOD_DURATION);
    mGeigerClickRampUpFrames = int(mGeigerFramesPerPeriod * GEIGER_CLICK_RAMP_DURATION);
}

oboe::DataCallbackResult
Oscillator::onAudioReady(oboe::AudioStream *audioStream, void *audioData, int32_t numFrames) {

    if (mLatencyTuner && audioStream->getAudioApi() == oboe::AudioApi::AAudio) {
        mLatencyTuner->tune();
    }

    auto* buffer = (float*) audioData;

    if (hasWaveFunction()) {
        const float sampleRate = (float)audioStream->getSampleRate();

        switch (mWaveFunction) {
            case SQUARE:
                fillSquare(buffer, numFrames, sampleRate, mVolume);
                break;
            case SAWTOOTH:
                fillSawtooth(buffer, numFrames, sampleRate, mVolume);
                break;
            case TRIANGLE:
                fillTriangle(buffer, numFrames, sampleRate, mVolume);
                break;
            case SINE:
                fillSine(buffer, numFrames, sampleRate, mVolume);
                break;
            case NOISE:
                fillNoise(buffer, numFrames, sampleRate, mVolume);
                break;
            case RADIOACTIVE: {
                float hertz = getPhaseIncrement(sampleRate) * sampleRate;
                float previousHertz = hertz;
                if (hertz > GEIGER_MAX_HZ) {
                    if (mPreviousHertz <= GEIGER_MAX_HZ) {
                        mSwitchStateSeconds = 0.0f;
                        mPhase = 0.0f;
                    }
                    hertz = mSwitchStateSeconds > WARNING_SWITCH_SECONDS ? WARNING_HIGH_HZ : WARNING_LOW_HZ;
                    const float warningPhaseIncrement = hertz / sampleRate;
                    fillSine(buffer, numFrames, sampleRate, mVolume, warningPhaseIncrement);
                    mSwitchStateSeconds += (float)numFrames / sampleRate;
                    if (mSwitchStateSeconds > WARNING_SWITCH_TOTAL_SECONDS) {
                        mSwitchStateSeconds -= WARNING_SWITCH_TOTAL_SECONDS;
                    }
                } else {
                    fillRadioactive(buffer, numFrames, hertz, (size_t)sampleRate, mVolume);
                }
                mPreviousHertz = previousHertz;
            }
                break;
            default:
                assert(false);
        }
        return oboe::DataCallbackResult::Continue;

    } else {
        std::fill(buffer, buffer+numFrames, 0);
        return oboe::DataCallbackResult::Stop;
    }
}

bool Oscillator::hasWaveFunction() {
    return mWaveFunction != WaveFunction::NONE;
}

inline float Oscillator::transform(float value, float max) const {
    if (max <= 0.0f) return 0.0f;

    // Normalize value to [-1, 1] relative to max
    float normalized = value / max;

    if (mExponential != 1.0f) {
        bool negative = std::signbit(normalized);
        normalized = fabsf(normalized);
        normalized = std::pow(normalized, mExponential);
        if (negative) {
            normalized = -normalized;
        }
    }

    // Apply distortion: increase gain then clamp
    // mDistortion 0.0 -> gain 1.0
    // mDistortion 1.0 -> gain 20.0 (aggressive clipping)
    float gain = 1.0f + mDistortion * 19.0f;
    normalized *= gain;

    return std::clamp(normalized, -1.0f, 1.0f) * max;
}

float Oscillator::getPhaseIncrement(float sampleRate) const {
    const float accelerometerValue = mIsX ? mSensorManager->getAccelerometerX() : mSensorManager->getAccelerometerY();
    float hertzRatio = std::clamp(fabsf(accelerometerValue), 0.0f, 1.0f);

    if (mInvert) {
        hertzRatio = 1.0f - hertzRatio;
    }

    float hertz;
    if (mStairs) {
        int frequency = std::min(
                FREQUENCY_COUNT - 1,
                mMinFrequency + (int) roundf(float(mMaxFrequency - mMinFrequency) * hertzRatio));
        hertz = mowave::FREQUENCIES[std::max(0, frequency)];
    } else {
        float minHertz = mowave::FREQUENCIES[std::clamp(mMinFrequency, 0, FREQUENCY_COUNT - 1)];
        float maxHertz = mowave::FREQUENCIES[std::clamp(mMaxFrequency, 0, FREQUENCY_COUNT - 1)];
        hertz = minHertz + hertzRatio * (maxHertz - minHertz);
    }
    return hertz / sampleRate;
}

void Oscillator::fillSquare(float *buffer, size_t bufferSize, float sampleRate, float max) {
    const float negativeMax = -max;
    float phaseIncrement = getPhaseIncrement(sampleRate);
    for (size_t i = 0; i < bufferSize; ++i) {
        if (i % 32 == 0) phaseIncrement = getPhaseIncrement(sampleRate);
        buffer[i] = transform(mPhase < 0.5f ? negativeMax : max, max);
        mPhase = fmodf(mPhase + phaseIncrement, 1.0f);
    }
}

void Oscillator::fillSawtooth(float *buffer, size_t bufferSize, float sampleRate, float max) {
    float phaseIncrement = getPhaseIncrement(sampleRate);
    for (size_t i = 0; i < bufferSize; ++i) {
        if (i % 32 == 0) phaseIncrement = getPhaseIncrement(sampleRate);
        buffer[i] = transform((mPhase - 0.5f) * max * 2.0f, max);
        mPhase = fmodf(mPhase + phaseIncrement, 1.0f);
    }
}

void Oscillator::fillTriangle(float *buffer, size_t bufferSize, float sampleRate, float max) {
    float phaseIncrement = getPhaseIncrement(sampleRate);
    for (size_t i = 0; i < bufferSize; ++i) {
        if (i % 32 == 0) phaseIncrement = getPhaseIncrement(sampleRate);
        float multiplier = (mPhase - 0.5f) * 2.0f;
        float absMultiplier = fabsf(multiplier) - 0.5f;
        buffer[i] = transform(absMultiplier * max * 2.0f, max);
        mPhase = fmodf(mPhase + phaseIncrement, 1.0f);
    }
}

void Oscillator::fillSine(float *buffer, size_t bufferSize, float sampleRate, float max, float fixedPhaseIncrement) {
    float phaseIncrement = (fixedPhaseIncrement > 0.0f) ? fixedPhaseIncrement : getPhaseIncrement(sampleRate);
    for (size_t i = 0; i < bufferSize; ++i) {
        if (fixedPhaseIncrement <= 0.0f && i % 32 == 0) phaseIncrement = getPhaseIncrement(sampleRate);
        buffer[i] = transform(sinf(mPhase * TWO_PI) * max, max);
        mPhase = fmodf(mPhase + phaseIncrement, 1.0f);
    }
}

void Oscillator::fillNoise(float *buffer, size_t bufferSize, float sampleRate, float max) {
    float phaseIncrement = getPhaseIncrement(sampleRate);
    for (size_t i = 0; i < bufferSize; ++i) {
        if (i % 32 == 0) phaseIncrement = getPhaseIncrement(sampleRate);
        const float newPhase = fmodf(mPhase + phaseIncrement, 1.0f);
        if (newPhase < mPhase || (mPhase < 0.5f && newPhase >= 0.5f)) {
            mNoiseCurrentValue = mNoiseNextValue;
            mNoiseNextValue = (mDistribution(mGenerator) * 2.0f - DISTRIBUTION_MAX) / DISTRIBUTION_MAX;
        }
        buffer[i] = transform(mNoiseCurrentValue + (mNoiseNextValue - mNoiseCurrentValue) * mPhase, max);
        mPhase = newPhase;
    }
}

void Oscillator::fillRadioactive(float* buffer, size_t bufferSize, float hertz, size_t sampleRate, float max) {
    const float clickPhaseIncrement = GEIGER_CLICK_HZ / (float)sampleRate;
    for (size_t i = 0; i < bufferSize; ++i) {
        if (mGeigerHitFramesLeft == 0) {
            if ((mDistribution(mGenerator) * (float)sampleRate * GEIGER_MAX_HZ) < hertz) {
                mPhase = 0.0f;
                mGeigerHitFramesLeft = mGeigerClickFrames;
            } else {
                buffer[i] = 0.0f;
                continue;
            }
        }

        float value = sinf(mPhase * TWO_PI) * max;
        if (mGeigerHitFramesLeft < mGeigerClickRampUpFrames) {
            value *= (float)mGeigerHitFramesLeft / (float)mGeigerClickRampUpFrames;
        } else if (mGeigerHitFramesLeft > (mGeigerClickFrames - mGeigerClickRampUpFrames)) {
            value *= (float)(mGeigerClickFrames - mGeigerHitFramesLeft) / (float)mGeigerClickRampUpFrames;
        }
        buffer[i] = transform(value, max);
        mPhase = fmodf(mPhase + clickPhaseIncrement, 1.0f);
        --mGeigerHitFramesLeft;
    }
}

}
