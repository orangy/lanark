cmake_minimum_required(VERSION 3.8)

set(CMAKE_MODULE_PATH ${CMAKE_CURRENT_LIST_DIR}/KotlinCMakeModule)
set(CMAKE_Kotlin_FLAGS "-version")

project(KotlinSDL2 Kotlin)

cinterop(
        NAME sdl2
        TARGET macos
        DEF_FILE c_interop/sdl2.def
        COMPILER_OPTS "-I/usr/local/opt/sdl2/include/SDL2 -I/usr/local/opt/sdl2_image/include/SDL2 -I/usr/local/opt/sdl2_mixer/include/SDL2"
)

konanc_executable(
        NAME playground
        TARGET macos
        SOURCES playground framework/src
        TEST_SOURCES framework/test
        LIBRARIES sdl2
        LINKER_OPTS "-L/usr/local/opt/sdl2/lib -L/usr/local/opt/sdl2_image/lib -L/usr/local/opt/sdl2_mixer/lib -lsdl2 -lsdl2_image -lsdl2_mixer"
)

add_custom_command(TARGET playground.kexe PRE_BUILD
        COMMAND ${CMAKE_COMMAND} -E copy_directory
        ${CMAKE_SOURCE_DIR}/resources $<TARGET_FILE_DIR:playground.kexe>)
