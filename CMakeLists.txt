cmake_minimum_required(VERSION 3.8)

set(CMAKE_MODULE_PATH ${CMAKE_CURRENT_LIST_DIR}/KotlinCMakeModule)
set(CMAKE_Kotlin_FLAGS "-nodefaultlibs -version")

project(KotlinSDL2)

include_directories(
        "/usr/local/opt/sdl2/include"
        "/usr/local/opt/sdl2_image/include"
        "/usr/local/opt/sdl2_mixer/include"
)
link_directories(
        "/usr/local/opt/sdl2/lib"
        "/usr/local/opt/sdl2_image/lib"
        "/usr/local/opt/sdl2_mixer/lib"
)

enable_language(Kotlin)
cinterop(
        NAME sdl2
        DEF_FILE c_interop/sdl2.def
        COMPILER_OPTS "-I/usr/local/opt/sdl2/include/SDL2 -I/usr/local/opt/sdl2_image/include/SDL2 -I/usr/local/opt/sdl2_mixer/include/SDL2"
)

konanc_executable(
        NAME KotlinSDL2
        SOURCES src framework
        LIBRARIES sdl2
        LINKER_OPTS "-L/usr/local/opt/sdl2/lib -L/usr/local/opt/sdl2_image/lib -L/usr/local/opt/sdl2_mixer/lib -lsdl2 -lsdl2_image -lsdl2_mixer"
)

add_custom_command(TARGET KotlinSDL2.kexe PRE_BUILD
        COMMAND ${CMAKE_COMMAND} -E copy_directory
        ${CMAKE_SOURCE_DIR}/resources $<TARGET_FILE_DIR:KotlinSDL2.kexe>)
