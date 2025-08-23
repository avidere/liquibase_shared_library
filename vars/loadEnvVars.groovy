def call(){
    env.projectKey = params.PROJECT_KEY
    env.repositoryName = params.REPOSITORY_NAME
    env.gitbranch = params.GIT_BRANCH
    env.baseUrl = "https://github.com"
    env.gitUrl = "${env.baseUrl}/${env.projectKey}/${env.repositoryName}.git"
    env.gitCredentialsId = "git-credentials"
    env.vaultcred = creds['vaultcred']

}