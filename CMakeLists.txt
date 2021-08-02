cmake_minimum_required(VERSION 3.10.2)

project(govarnam_jni C)

set(BUILD_OUTPUT ${CMAKE_CURRENT_SOURCE_DIR}/build)
set(SRC ${BUILD_OUTPUT}/${CMAKE_ANDROID_ARCH_ABI})
set(JNI_MAIN ${CMAKE_CURRENT_SOURCE_DIR}/src/com/varnamproject/govarnam)

include_directories(${SRC})

find_library(
        std-lib
        android
)

message("CPU ABI: ${CMAKE_ANDROID_ARCH_ABI}")

file(COPY ${SRC}/libgovarnam.h DESTINATION ${JNI_MAIN}) # Copy header file

file(GLOB JNI_SRC ${JNI_MAIN}/*.c ${JNI_MAIN}/*.h)

add_library(govarnam_jni SHARED ${JNI_SRC})

file(GLOB OUTPUT_LIBRARY ${BUILD_OUTPUT}/${CMAKE_ANDROID_ARCH_ABI}/*.so)

set_target_properties(govarnam_jni PROPERTIES LINKER_LANGUAGE C)

target_link_libraries(
        govarnam_jni
        ${std-lib}
        log
        ${OUTPUT_LIBRARY}
)