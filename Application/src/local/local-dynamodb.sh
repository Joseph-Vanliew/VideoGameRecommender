#!/usr/bin/env sh
set -e

REFERRAL_STORAGE_DYNAMODB_CONTAINER_NAME="referral-storage-dynamodb"

REFERRAL_STORAGE_CONTAINER=$(docker ps -a -f "name=${REFERRAL_STORAGE_DYNAMODB_CONTAINER_NAME}" -q)
echo "REFERRAL_STORAGE_CONTAINER"

if [ ! -z "$REFERRAL_STORAGE_CONTAINER" ]; then
    echo "Removing container: ${REFERRAL_STORAGE_DYNAMODB_CONTAINER_NAME}"
    docker rm -f ${REFERRAL_STORAGE_CONTAINER}
fi

echo "Running container: ${REFERRAL_STORAGE_DYNAMODB_CONTAINER_NAME}"
docker run --name ${REFERRAL_STORAGE_DYNAMODB_CONTAINER_NAME} -p 8000:8000 -d amazon/dynamodb-local

echo "Waiting 10 seconds for dynamodb to boot..."
sleep 10