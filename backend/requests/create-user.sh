#!/bin/bash

curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"$1\",\"email\":\"$2\",\"password\":\"$3\"}"