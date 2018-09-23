package org.lanark.events

expect abstract class EventApp : Event

expect class EventAppQuit : EventApp
expect class EventAppTerminating : EventApp
expect class EventAppLowMemory : EventApp
expect class EventAppEnteredBackground : EventApp
expect class EventAppEnteredForeground : EventApp
expect class EventAppEnteringBackground : EventApp
expect class EventAppEnteringForeground : EventApp
