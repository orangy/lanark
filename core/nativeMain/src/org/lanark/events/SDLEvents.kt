package org.lanark.events

import sdl2.*

val sdlEventNames = mapOf(
    SDL_QUIT to "SDL_QUIT",
    SDL_APP_TERMINATING to "SDL_APP_TERMINATING",
    SDL_APP_LOWMEMORY to "SDL_APP_LOWMEMORY",
    SDL_APP_WILLENTERBACKGROUND to "SDL_APP_WILLENTERBACKGROUND",
    SDL_APP_DIDENTERBACKGROUND to "SDL_APP_DIDENTERBACKGROUND",
    SDL_APP_WILLENTERFOREGROUND to "SDL_APP_WILLENTERFOREGROUND",
    SDL_APP_DIDENTERFOREGROUND to "SDL_APP_DIDENTERFOREGROUND",
    SDL_WINDOWEVENT to "SDL_WINDOWEVENT",
    SDL_SYSWMEVENT to "SDL_SYSWMEVENT",
    SDL_KEYDOWN to "SDL_KEYDOWN",
    SDL_KEYUP to "SDL_KEYUP",
    SDL_TEXTEDITING to "SDL_TEXTEDITING",
    SDL_TEXTINPUT to "SDL_TEXTINPUT",
    SDL_KEYMAPCHANGED to "SDL_KEYMAPCHANGED",
    SDL_MOUSEMOTION to "SDL_MOUSEMOTION",
    SDL_MOUSEBUTTONDOWN to "SDL_MOUSEBUTTONDOWN",
    SDL_MOUSEBUTTONUP to "SDL_MOUSEBUTTONUP",
    SDL_MOUSEWHEEL to "SDL_MOUSEWHEEL",
    SDL_JOYAXISMOTION to "SDL_JOYAXISMOTION",
    SDL_JOYBALLMOTION to "SDL_JOYBALLMOTION",
    SDL_JOYHATMOTION to "SDL_JOYHATMOTION",
    SDL_JOYBUTTONDOWN to "SDL_JOYBUTTONDOWN",
    SDL_JOYBUTTONUP to "SDL_JOYBUTTONUP",
    SDL_JOYDEVICEADDED to "SDL_JOYDEVICEADDED",
    SDL_JOYDEVICEREMOVED to "SDL_JOYDEVICEREMOVED",
    SDL_CONTROLLERAXISMOTION to "SDL_CONTROLLERAXISMOTION",
    SDL_CONTROLLERBUTTONDOWN to "SDL_CONTROLLERBUTTONDOWN",
    SDL_CONTROLLERBUTTONUP to "SDL_CONTROLLERBUTTONUP",
    SDL_CONTROLLERDEVICEADDED to "SDL_CONTROLLERDEVICEADDED",
    SDL_CONTROLLERDEVICEREMOVED to "SDL_CONTROLLERDEVICEREMOVED",
    SDL_CONTROLLERDEVICEREMAPPED to "SDL_CONTROLLERDEVICEREMAPPED",
    SDL_FINGERDOWN to "SDL_FINGERDOWN",
    SDL_FINGERUP to "SDL_FINGERUP",
    SDL_FINGERMOTION to "SDL_FINGERMOTION",
    SDL_DOLLARGESTURE to "SDL_DOLLARGESTURE",
    SDL_DOLLARRECORD to "SDL_DOLLARRECORD",
    SDL_MULTIGESTURE to "SDL_MULTIGESTURE",
    SDL_CLIPBOARDUPDATE to "SDL_CLIPBOARDUPDATE",
    SDL_DROPFILE to "SDL_DROPFILE",
    SDL_DROPTEXT to "SDL_DROPTEXT",
    SDL_DROPBEGIN to "SDL_DROPBEGIN",
    SDL_DROPCOMPLETE to "SDL_DROPCOMPLETE",
    SDL_AUDIODEVICEADDED to "SDL_AUDIODEVICEADDED",
    SDL_AUDIODEVICEREMOVED to "SDL_AUDIODEVICEREMOVED",
    SDL_RENDER_TARGETS_RESET to "SDL_RENDER_TARGETS_RESET",
    SDL_RENDER_DEVICE_RESET to "SDL_RENDER_DEVICE_RESET",
    SDL_USEREVENT to "SDL_USEREVENT"
)


