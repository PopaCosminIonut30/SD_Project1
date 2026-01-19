#!/bin/bash

# Listă cu serviciile tale (calea către folderele care conțin pom.xml)
SERVICES=(
  "authentication-service/demo"
  "device-service/demo"
  "monitoring-service/demo"
  "user-service/demo"
  "websocket-service/demo"
  "device-simulator"
)

echo "--- START BUILD BACKEND SERVICES ---"

for SERVICE in "${SERVICES[@]}"; do
  echo "Building $SERVICE..."
  if [ -d "$SERVICE" ]; then
    cd "$SERVICE" && mvn clean package -DskipTests
    cd - > /dev/null
  else
    echo "Warning: Directory $SERVICE not found!"
  fi
done

echo "--- START BUILD FRONTEND ---"
if [ -d "frontend" ]; then
  cd frontend && npm install
  cd - > /dev/null
else
  echo "Warning: Frontend directory not found!"
fi

echo "--- ALL BUILDS COMPLETED ---"