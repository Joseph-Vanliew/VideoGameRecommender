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
