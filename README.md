# SomePianoApp

Some Piano App connects to a MIDI keyboard to help practice or show others what you're doing.

## Realtime Mode

In realtime mode notes you play are stretched along the timeline so that others can see what keys you're pressing and for how long.

## Playback Mode

Load up a MIDI file and play along as the notes fly across the screen.

## Spark View

The main spark view shows a vertical stripe for each note aligned with the corresponding key.

## Sheet Music View

The sheet music view attempts to draw notes on the grand staff.


# Quickstart

## Gradle

Run
```
~$ ./gradlew run
```

Deploy
```
~$ ./gradlew jlink
~$ ./build/image/bin/rtp
```

IntelliJ integration run/debug integration works with no configuration.

# Screenshots

![Playback mode playing Bach's Prelude in C major](screenshots/playback_mode.png?raw=true "Playback mode playing Bach's Prelude in C major")

![Watch mode](screenshots/watch_mode.png?raw=true "Watch mode")

