#!/bin/bash

# Start the Othello server
echo "Starting Othello server..."
java -jar othello.jar --p1-type remote --p2-type random --wait-for-ui
