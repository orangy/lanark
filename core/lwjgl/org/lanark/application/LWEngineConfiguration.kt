package org.lanark.application

import org.lanark.diagnostics.*
import org.lanark.system.*

actual class EngineConfiguration {
    actual val platform: String
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    actual val cpus: Int
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    actual val version: Version
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    actual var logger: Logger
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        set(value) {}

    actual fun enableEverything() {}
    actual fun enableTimer() {}
    actual fun enableAudio() {}
    actual fun enableEvents() {}
    actual fun enableController() {}
    actual fun enableHaptic() {}
    actual fun enableJoystick() {}
    actual fun enableVideo() {}

}