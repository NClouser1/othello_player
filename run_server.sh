#!/bin/bash

# Start the Othello server
echo "Starting Othello server..."
javac -cp AI/lib/gson-2.8.5.jar -d AI/bin AI/src/com/atomicobject/othello/*.java
java -jar othello.jar --p1-type remote --p2-type random --wait-for-ui
