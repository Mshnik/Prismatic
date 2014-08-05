#!/bin/bash

echo "brewing models and joining"
coffee --join lib/models.js --compile src/models/util/*.coffee src/models/*.coffee src/models/*/*.coffee
echo "brewing scripts"
coffee --compile --output lib/ src/scripts/