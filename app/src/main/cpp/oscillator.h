#ifndef MOWAVE_OSCILLATOR_H
#define MOWAVE_OSCILLATOR_H

#include <oboe/Oboe.h>
#include <random>
#include "sensor_manager.h"

namespace mowave {

typedef enum wave_function {
    NONE = 0,
    SQUARE = 1,
    SAWTOOTH = 2,
    TRIANGLE = 3,
    SINE = 4,
    NOISE = 5,
    RADIOACTIVE = 6,
} WaveFunction;

constexpr float DISTRIBUTION_MAX = 0.05f;

class Oscillator : public oboe::AudioStreamCallback {

public:

    explicit Oscillator(mowave::SensorManager* sensorManager);

    ~Oscillator() override;

    void
    setWaveFunction(
            WaveFunction waveFunction,
            bool isX,
            bool invert,
            bool stairs,
            float distortion,
            float exponential);

    void setWaveRange(int min, int max);

    bool hasWaveFunction();

    void onAudioCreated(oboe::AudioStream * audioStream);

    oboe::DataCallbackResult
    onAudioReady(oboe::AudioStream *audioStream, void *audioData, int32_t numFrames) override;

private:
    mowave::SensorManager* mSensorManager;
    float mVolume = 1.0f;
    bool mIsX{};
    bool mInvert{};
    bool mStairs{};
    float mDistortion{};
    float mExponential{};
    int mMinFrequency{};
    int mMaxFrequency{};
    float mPhase = 0.0f;
    WaveFunction mWaveFunction = NONE;
    int mGeigerHitFramesLeft = 0;
    float mGeigerFramesPerPeriod{};
    int mGeigerClickRampUpFrames{};
    int mGeigerClickFrames{};
    float mSwitchStateSeconds = 0.0f;
    float mNoiseCurrentValue = 0.0f;
    float mNoiseNextValue = 0.0f;
    float mPreviousHertz = 0.0f;
    std::mt19937 mGenerator = std::mt19937(std::random_device{}());
    std::uniform_real_distribution<float> mDistribution = std::uniform_real_distribution<float>(0.0f, DISTRIBUTION_MAX);
    std::unique_ptr<oboe::LatencyTuner> mLatencyTuner;

    void fillSquare(float *buffer, size_t bufferSize, float sampleRate, float max);
    void fillSawtooth(float *buffer, size_t bufferSize, float sampleRate, float max);
    void fillTriangle(float *buffer, size_t bufferSize, float sampleRate, float max);
    void fillSine(float *buffer, size_t bufferSize, float sampleRate, float max, float fixedPhaseIncrement = -1.0f);
    void fillNoise(float *buffer, size_t bufferSize, float sampleRate, float max);

    void fillRadioactive(float* buffer, size_t bufferSize, float hertz, size_t sampleRate, float max);

    float transform(float value, float max) const;

    float getPhaseIncrement(float sampleRate) const;
};

}

#endif //MOWAVE_OSCILLATOR_H
