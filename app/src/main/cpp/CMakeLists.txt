cmake_minimum_required(VERSION 3.4.1)

project(mowave)

find_package(oboe REQUIRED CONFIG)

add_library(mowave SHARED
            mowave.cpp
            sensor_manager.cpp
            player.cpp
            oscillator.cpp
        )

target_link_libraries(mowave
                      oboe::oboe
                      android
                      log
                      OpenSLES)
