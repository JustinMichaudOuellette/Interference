#include <cassert>

#include "sensor_manager.h"

#define LOOPER_ID_USER 113
#define ALPHA 0.6f
#define MAX_NORMAL_VAL 10.0f

namespace mowave {

    int callback(int fd, int events, void* data) {
        static_cast<mowave::SensorManager*>(data)->update();
        return 1;
    }

    SensorManager::SensorManager() {
        sensorManager = ASensorManager_getInstance();
        assert(sensorManager != nullptr);

        accelerometer = ASensorManager_getDefaultSensor(
                sensorManager,
                ASENSOR_TYPE_ACCELEROMETER);
        assert(accelerometer != nullptr);

        looper = ALooper_prepare(0);
        assert(looper != nullptr);

        accelerometerEventQueue = ASensorManager_createEventQueue(
                sensorManager, looper,
                LOOPER_ID_USER, &callback, this);
        assert(accelerometerEventQueue != nullptr);
    }

    void SensorManager::setEnabled(bool enabled) {
        if (this->enabled != enabled) {
            this->enabled = enabled;
            if (enabled) {
                ASensorEventQueue_enableSensor(accelerometerEventQueue,
                                               accelerometer);
                auto status = ASensorEventQueue_setEventRate(accelerometerEventQueue,
                                                             accelerometer,
                                                             ASensor_getMinDelay(accelerometer));
                assert(status >= 0);
            } else {
                ASensorEventQueue_disableSensor(accelerometerEventQueue,
                                                accelerometer);
            }
        }
    }

    void SensorManager::update() {
        ASensorEvent event;
        while (ASensorEventQueue_getEvents(accelerometerEventQueue, &event, 1) > 0) {
            sensorFilteredData.x = ALPHA * event.acceleration.x + (1.0f - ALPHA) * sensorFilteredData.x;
            sensorFilteredData.y = ALPHA * event.acceleration.y + (1.0f - ALPHA) * sensorFilteredData.y;
            sensorFilteredData.z = ALPHA * event.acceleration.z + (1.0f - ALPHA) * sensorFilteredData.z;
        }
    }

    float SensorManager::getAccelerometerX() {
        return sensorFilteredData.x / MAX_NORMAL_VAL;
    }

    float SensorManager::getAccelerometerY() {
        return sensorFilteredData.y / MAX_NORMAL_VAL;
    }

    float SensorManager::getAccelerometerZ() {
        return sensorFilteredData.z / MAX_NORMAL_VAL;
    }

    SensorManager::~SensorManager() {
        setEnabled(false);
        if (looper != nullptr) {
            ALooper_release(looper);
            looper = nullptr;
        }
    }

}
