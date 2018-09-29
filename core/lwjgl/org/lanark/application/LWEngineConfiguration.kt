package org.lanark.application

import org.lanark.diagnostics.*
import org.lanark.events.*
import org.lanark.system.*

actual class EngineConfiguration(actual val platform: String, actual val cpus: Int, actual val version: Version) {
    internal var flags: UInt = 0u
    actual var logger: Logger = LoggerNone
    actual var events: Events? = null
    actual var executor: TaskExecutor? = null

    actual fun enableEverything() {}
    actual fun enableTimer() {}
    actual fun enableAudio() {}
    actual fun enableEvents() {}
    actual fun enableController() {}
    actual fun enableHaptic() {}
    actual fun enableJoystick() {}
    actual fun enableVideo() {}

}