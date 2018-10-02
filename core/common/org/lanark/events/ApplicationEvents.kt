package org.lanark.events

abstract class EventApp(timestamp: ULong) : Event(timestamp)

class EventAppQuit(timestamp: ULong) : EventApp(timestamp)
class EventAppTerminating(timestamp: ULong) : EventApp(timestamp)
class EventAppLowMemory(timestamp: ULong) : EventApp(timestamp)
class EventAppEnteredBackground(timestamp: ULong) : EventApp(timestamp)
class EventAppEnteredForeground(timestamp: ULong) : EventApp(timestamp)
class EventAppEnteringBackground(timestamp: ULong) : EventApp(timestamp)
class EventAppEnteringForeground(timestamp: ULong) : EventApp(timestamp)
