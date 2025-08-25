def call(){
    def propFileContent = libraryResource 'properties/config.properties'
    def props = readProperties text: propFileContent

    def credsFileContent = libraryResource 'properties/credentials.properties' 
    def creds = readProperties text: credsFileContent

    env.projectKey = params.PROJECT_KEY
    env.repositoryName = params.REPOSITORY_NAME
    env.gitbranch = params.GIT_BRANCH
    env.baseUrl = "https://github.com"
    env.gitUrl = "${env.baseUrl}/${env.projectKey}/${env.repositoryName}.git"
   // env.gitCredentialsId = ['git-credentials']
   env.vaultcred = creds['vaultcred']
   env.VAULT_ADDR = props['VAULT_ADDR']

   //liquibase related env variables
   if(params.CHANGE_LOG != null){
      env.changelog = params.CHANGE_LOG
   } 

   if(params.ENVIRONMENT != null){
      env.envir = params.ENVIRONMENT
   }

}