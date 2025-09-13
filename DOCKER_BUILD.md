# Building Nextcloud Android APK with Docker

This repository includes Docker support for building the Nextcloud Android app in a clean, reproducible environment.

## Quick Start

### Option 1: Using the build script (Recommended)

```bash
./build-docker.sh
```

This script will:
1. Build the Docker image with all required dependencies
2. Compile the Nextcloud Android app
3. Extract the built APK files to the `output/` directory

### Option 2: Manual Docker commands

```bash
# Build the Docker image
docker build -t nextcloud-android-builder .

# Run the container and extract APKs
mkdir -p output
docker run --rm -v "$(pwd)/output:/host-output" nextcloud-android-builder sh -c "cp /output/*.apk /host-output/"

# List the built APKs
ls -la output/
```

## What's Included

The Docker setup includes:

- **Ubuntu Noble (24.04)** base image
- **OpenJDK 17** for Java compilation
- **Android SDK** with required platform tools and build tools
- **Gradle** build system with optimized configuration
- All necessary dependencies for building the Nextcloud Android app

## Built APK Location

After a successful build, you'll find the APK files in the `output/` directory:

- `nextcloud-generic-debug.apk` - Debug build
- `nextcloud-generic-release.apk` - Release build (if signing keys are provided)

## Requirements

- Docker installed and running
- At least 8GB of available disk space
- At least 6GB of available RAM (recommended)

## Build Configuration

The Docker build is configured to:

- Use optimal Gradle settings for performance
- Accept Android SDK licenses automatically
- Build the "Generic" variant of the app
- Skip daemon mode for CI/container environments

## Troubleshooting

### Build fails with memory errors
Increase Docker's memory limit to at least 6GB.

### Build takes a long time
First builds download many dependencies and can take 10-30 minutes. Subsequent builds will be faster due to Docker layer caching.

### No APK files generated
Check the Docker build logs for compilation errors. The build process will show detailed error messages if compilation fails.

## Development Usage

For development purposes, you can also run an interactive container:

```bash
docker run -it --rm -v "$(pwd):/workspace" nextcloud-android-builder bash
```

This allows you to:
- Run individual Gradle tasks
- Debug build issues
- Test different build configurations
- Explore the build environment

## Build Variants

The default Dockerfile builds the "Generic" variant. To build other variants, modify the Dockerfile's build command:

```dockerfile
# For Google Play variant
RUN ./gradlew assembleGplay --no-daemon --stacktrace

# For debug builds only
RUN ./gradlew assembleGenericDebug --no-daemon --stacktrace
```