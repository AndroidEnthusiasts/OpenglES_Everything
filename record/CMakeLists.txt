# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.10.2)
include_directories(
        src/main/cpp/
)

file(GLOB record-files
        src/main/cpp/RecordBuffer.cpp
        src/main/cpp/AudioRecorder.cpp
        )

add_library(record-lib
        SHARED
        ${record-files}
        )

target_link_libraries(record-lib
        mediandk
        OpenSLES
        EGL
        GLESv3
        log
        android)