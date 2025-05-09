#!/bin/bash

# Exit on any error
set -e

echo "Compiling player code..."
mkdir -p AI/bin
javac -cp AI/lib/gson-2.8.5.jar -d AI/bin "AI/src/com/atomicobject/othello/*.java"

echo "Running player..."
java -cp "AI\bin;AI\lib\gson-2.8.5.jar" com.atomicobject.othello.Main
