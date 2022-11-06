# ATA-Unit-Five-Project-Solution

Follow the instructions on lms.kenzie.academy

### To create the Referral table in DynamoDB:

You must do this for the ReferralServiceLambda to work!

```
aws cloudformation create-stack --stack-name referral-table --template-body file://ReferralTable.yaml --capabilities CAPABILITY_IAM
```

### To deploy the CI/CD Pipeline

Fill out `setupEnvironment.sh` with your Github Repo name.

Run `./createPipeline.sh`

To teardown the pipeline, run `./cleanupPipeline.sh`


### To run local Redis:

First, run
```
./runLocalRedis.sh
```

### To create your development deployment:

Run `deployDev.sh`.  This might take 20 minutes...

To teardown the deployment, run `./cleanupDev.sh`.

Do not leave your development deployment running for long periods of time when you are not using it!  That will quickly eat up your AWS budget.

Stop your session on Vocareum to pause the dev deployment without having to tear it down. 
