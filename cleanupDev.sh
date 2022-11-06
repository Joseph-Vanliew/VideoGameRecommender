#!/bin/bash
set -eo pipefail

echo "Deleting Application $UNIT_FIVE_SERVICE_STACK_DEV"
echo "This may take 2-3 minutes...  But if takes more than 5 minutes then it may have failed. Check your CloudFormation Stack on the AWS UI for errors."
aws cloudformation delete-stack --stack-name $UNIT_FIVE_SERVICE_STACK_DEV
aws cloudformation wait stack-delete-complete --stack-name $UNIT_FIVE_SERVICE_STACK_DEV
