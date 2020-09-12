Developer Instructions
======================

### Usage
**Running with gradle:**
```
./gradlew run
```

### Building Release
**Creating and executing a custom runtime image:**
```
./gradlew runtime
cd build/image/bin
./PotaLogger
```

**Creating a platform-specific installer:**
```
./gradlew jpackage
```

The above command will generate the platform-specific installers in the `build/jpackage` directory.