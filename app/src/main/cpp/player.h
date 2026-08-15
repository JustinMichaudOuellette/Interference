#ifndef MOWAVE_PLAYER_H
#define MOWAVE_PLAYER_H

#include <oboe/Oboe.h>
#include "oscillator.h"

namespace mowave {

class Player {

public:
    Player(int32_t sampleRate, int32_t bufferSize, mowave::Oscillator* oscillator);

    virtual ~Player();

    void setPlaying(bool playing);

    void openStream();

    void configureStream(oboe::AudioStreamBuilder *builder);

    void closeStream();

private:
    mowave::Oscillator* mOscillator;
    bool mPlaying = false;
    oboe::AudioStream *mPlayStream = nullptr;

};

}

#endif //MOWAVE_PLAYER_H
