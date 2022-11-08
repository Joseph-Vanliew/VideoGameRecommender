# Step One- Fill out the UNIT_FIVE_REPO_NAME and GITHUB_USERNAME

# Step Two - configure your shell to always have these variables.
# For OSX / Linux
# Copy and paste ALL of the properties below into your .bash_profile in your home directly

# For Windows
# Copy and paste ALL of the properties below into your .bashrc file in your home directory

# Fill out the following values
# The path of your repo on github.  Don't but the whole URL, just the part after github.com/
export UNIT_FIVE_REPO_NAME=ata-unit-five-project-Joseph-Vanliew

# Do not modify the rest of these unless you have been instructed to do so.
export UNIT_FIVE_PROJECT_NAME=unitproject5
export UNIT_FIVE_PIPELINE_STACK=$UNIT_FIVE_PROJECT_NAME-$GITHUB_USERNAME
export UNIT_FIVE_ARTIFACT_BUCKET=$UNIT_FIVE_PROJECT_NAME-$GITHUB_USERNAME-artifacts
export UNIT_FIVE_APPLICATION_STACK=$UNIT_FIVE_PROJECT_NAME-$GITHUB_USERNAME-application
export UNIT_FIVE_SERVICE_STACK=$UNIT_FIVE_PROJECT_NAME-$GITHUB_USERNAME-service
export UNIT_FIVE_SERVICE_STACK_DEV=$UNIT_FIVE_PROJECT_NAME-$GITHUB_USERNAME-service-dev