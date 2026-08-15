#include <jni.h>
#include <memory>

#include "sensor_manager.h"
#include "player.h"
#include "oscillator.h"

static std::unique_ptr<mowave::SensorManager> sensorManager;
static std::unique_ptr<mowave::Player> player;
static std::unique_ptr<mowave::Oscillator> oscillator;

extern "C" {

    JNIEXPORT void JNICALL
    Java_ca_justinmo_interference_InterferenceJNI_create(
            JNIEnv *env, jobject /* this */, jint sampleRate, jint bufferSize) {

        sensorManager = std::make_unique<mowave::SensorManager>();
        oscillator = std::make_unique<mowave::Oscillator>(sensorManager.get());
        player = std::make_unique<mowave::Player>(sampleRate, bufferSize, oscillator.get());
    }

    JNIEXPORT void JNICALL
    Java_ca_justinmo_interference_InterferenceJNI_selectWave(
            JNIEnv *env,
            jobject /* this */,
            jint waveFunction,
            jboolean isX,
            jboolean invert,
            jboolean stairs,
            jfloat distortion,
            jfloat exponential) {

        if (oscillator) {
            oscillator->setWaveFunction((mowave::WaveFunction)waveFunction, isX, invert, stairs, distortion, exponential);

            bool hasWaveFunction = oscillator->hasWaveFunction();

            if (sensorManager) sensorManager->setEnabled(hasWaveFunction);
            if (player) player->setPlaying(hasWaveFunction);
        }
    }

    JNIEXPORT void JNICALL
    Java_ca_justinmo_interference_InterferenceJNI_setWaveRange(
            JNIEnv *env, jobject /* this */, jint min, jint max) {
        if (oscillator) oscillator->setWaveRange(min, max);
    }

    JNIEXPORT void JNICALL
    Java_ca_justinmo_interference_InterferenceJNI_destroy(JNIEnv *env, jobject /* this */) {
        player.reset();
        oscillator.reset();
        sensorManager.reset();
    }

}
