package org.lanark.application

import org.lanark.diagnostics.*
import org.lanark.events.*
import org.lanark.system.*

expect class EngineConfiguration {
    val platform: String
    val cpus: Int
    val version: Version
    
    var logger: Logger?
    
    fun enableEverything()
    fun enableTimer()
    fun enableAudio()
    fun enableEvents()
    fun enableController()
    fun enableHaptic()
    fun enableJoystick()
    fun enableVideo()
}