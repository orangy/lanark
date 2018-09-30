# Lanark

![Lanark](playground/resources/lanark-60x2.png)

Kotlin multiplatform framework for simple games and media applications. 
Primitive. Really it is. Not ready. Like at all. Seriously, it's a draft.  


## Roadmap

* Improve overall architecture by integrating things from experiments, and doing more experiments
* Improve desktop vs mobile experience (cursors vs touch, screen measures and rotations, windowed and full screen)
* Replace custom event loop with coroutines
* Implement UI part to the point of being usable for simple dialogs like start menu, high scores, etc
* Implement few simple 2D games
  * 2D shooter, something like Konami Knightmare :)
  * 2D platformer
  * (in 2 years) an RPG
  
…

* Profit!  

## local.properties file

Due to current state of IDE support for multiplatform projects, and also complexity of working with macOS/iOS targets,
we have some control over what to include in the build/IDE. Create a `local.properties` file in the project folder.

If you are interested only in LWJGL and/or don't have a macOS, add this to `local.properties`:
```
lanark.sdl2.enable=false
```

If you have a macOS computer, but want to work with the common code, due to current IDE limitations 
you need to exclude iOS targets from build. Add this to `local.properties`:

```
lanark.sdl2.ios=false
``` 

If you want to deploy to iOS emulator or real device, you need to add information for code signing:
```
apple_application_id=lanark.playground
apple_team_id=<YOUR APPLE TEAM ID IF YOU HAVE IT>
apple.identity=<FIND OUT IDENTITY USING "security find-identity -pcodesigning -v">
```

