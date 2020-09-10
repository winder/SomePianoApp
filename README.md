# RealTimePiano

Configured according to
https://openjfx.io/openjfx-docs/#maven


# Quickstart
Download SDK and jmods:
https://gluonhq.com/products/javafx/

Compile
```
~$ export PATH_TO_FX=/opt/java/javafx-sdk-14.0.2.1/lib
~$ mvn compile
```

Run
```
~$ mvn javafx:run
```

Debug
(saved as a run configuration with IntelliJ)
```
~$ mvn javafx:run@debug
```

Runtime image
```
~$ export PATH_TO_FX_MODS=path/opt/java/javafx-jmods-14.0.2.1/
~$ mvn javafx:jlink
~$ ./target/rtp/bin/launcher
```
