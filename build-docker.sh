#!/bin/bash

# Script to build Nextcloud Android app using Docker

set -e

echo "Building Nextcloud Android app using Docker..."

# Build the Docker image
echo "Building Docker image..."
docker build -t nextcloud-android-builder .

# Create output directory
mkdir -p output

# Run the container and copy the APKs
echo "Extracting built APKs..."
docker run --rm -v "$(pwd)/output:/host-output" nextcloud-android-builder sh -c "cp /output/*.apk /host-output/ 2>/dev/null || echo 'No APKs found in /output/'"

# List the built APKs
echo "Built APKs:"
ls -la output/*.apk 2>/dev/null || echo "No APKs found in output directory"

echo "Build completed!"