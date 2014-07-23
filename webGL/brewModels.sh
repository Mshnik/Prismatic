#!/bin/bash
echo "compining and joining..."
coffee --join lib/models.js --compile src/models/util/*.coffee src/models/*.coffee src/models/*/*.coffee