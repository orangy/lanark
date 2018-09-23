package org.lanark.application

import org.lanark.diagnostics.*
import org.lanark.system.*
import sdl2.*

actual class EngineConfiguration(actual val platform: String, actual val cpus: Int, actual val version: Version) {
    internal var flags: UInt = 0u
    actual var logger: Logger = LoggerNone

    actual fun enableEverything() {
        flags = flags or SDL_INIT_EVERYTHING
    }

    actual fun enableTimer() {
        flags = flags or SDL_INIT_TIMER
    }

    actual fun enableAudio() {
        flags = flags or SDL_INIT_AUDIO
    }

    actual fun enableEvents() {
        flags = flags or SDL_INIT_EVENTS
    }

    actual fun enableController() {
        flags = flags or SDL_INIT_GAMECONTROLLER
    }

    actual fun enableHaptic() {
        flags = flags or SDL_INIT_HAPTIC
    }

    actual fun enableJoystick() {
        flags = flags or SDL_INIT_JOYSTICK
    }

    actual fun enableVideo() {
        flags = flags or SDL_INIT_VIDEO
    }
}