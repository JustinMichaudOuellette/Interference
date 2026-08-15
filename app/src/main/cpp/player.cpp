#include "player.h"
#include "oscillator.h"

namespace mowave {

Player::Player(int32_t sampleRate, int32_t bufferSize, mowave::Oscillator* oscillator) {
    oboe::DefaultStreamValues::SampleRate = sampleRate;
    oboe::DefaultStreamValues::FramesPerBurst = bufferSize;
    mOscillator = oscillator;
}

Player::~Player() {
    closeStream();
}

void Player::setPlaying(bool playing) {
    if (mPlaying != playing) {
        mPlaying = playing;
        if (playing) {
            openStream();
        } else {
            closeStream();
        }
    }
}

void Player::openStream() {
    oboe::AudioStreamBuilder builder;

    configureStream(&builder);

    oboe::Result result = builder.openStream(&mPlayStream);

    if (result == oboe::Result::OK && mPlayStream != nullptr) {
        mPlayStream->setBufferSizeInFrames(mPlayStream->getFramesPerBurst());
        mOscillator->onAudioCreated(mPlayStream);
        mPlayStream->requestStart();
    }
}

void Player::configureStream(oboe::AudioStreamBuilder *builder) {
    builder->setAudioApi(oboe::AudioApi::AAudio)
           ->setDeviceId(0)
           ->setChannelCount(1)
           ->setSharingMode(oboe::SharingMode::Exclusive)
           ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
           ->setCallback(mOscillator)
           ->setFormat(oboe::AudioFormat::Float)
           ->setBufferCapacityInFrames(2 * oboe::DefaultStreamValues::FramesPerBurst)
           ;
}

void Player::closeStream() {
    if (mPlayStream != nullptr) {
        mPlayStream->requestStop();
        mPlayStream->close();
    }
}

}
