package org.lanark.events

actual abstract class EventApp : Event()
actual class EventAppQuit : EventApp()
actual class EventAppTerminating : EventApp()
actual class EventAppLowMemory : EventApp()
actual class EventAppEnteredBackground : EventApp()
actual class EventAppEnteredForeground : EventApp()
actual class EventAppEnteringBackground : EventApp()
actual class EventAppEnteringForeground : EventApp()