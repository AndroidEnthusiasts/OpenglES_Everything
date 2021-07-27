# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.10.2)
include_directories(
        ${CMAKE_SOURCE_DIR}/src/main/cpp/camera
        ${CMAKE_SOURCE_DIR}/src/main/cpp/utils
        ${CMAKE_SOURCE_DIR}/src/main/cpp/es
)

file(GLOB CAMERA_FILES
        src/main/cpp/*.cpp
        src/main/cpp/utils/*.cpp
        src/main/cpp/camera/*.cpp
        src/main/cpp/es/*.cpp
        )

add_library(CAMERA_LIB
        SHARED
        ${CAMERA_FILES}
        )

target_link_libraries(CAMERA_LIB
        EGL
        GLESv3
        log
        android)