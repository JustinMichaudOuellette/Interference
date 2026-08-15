#ifndef MOWAVE_SENSOR_MANAGER_H
#define MOWAVE_SENSOR_MANAGER_H

#include <android/sensor.h>

namespace mowave {

    typedef struct accelerometer_data {
        float x;
        float y;
        float z;
    } AccelerometerData;

    class SensorManager {
        private:
            ASensorManager *sensorManager = NULL;
            const ASensor *accelerometer = NULL;
            ASensorEventQueue *accelerometerEventQueue = NULL;
            ALooper *looper = NULL;
            AccelerometerData sensorFilteredData = {0};
            bool enabled = false;
        public:
            void update();
            SensorManager();
            void setEnabled(bool enabled);
            float getAccelerometerX();
            float getAccelerometerY();
            float getAccelerometerZ();
            virtual ~SensorManager();
    };

}

#endif //MOWAVE_SENSOR_MANAGER_H
