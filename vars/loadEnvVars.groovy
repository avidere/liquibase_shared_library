def call(){
    def propFileContent = libraryResource 'properties/config.properties'
    def props = readProperties text: propFileContent

    def credsFileContent = libraryResource 'properties/credentials.properties' 
    def creds = readProperties text: credsFileContent

    env.projKey = params.PROJECT_KEY
    env.gitRepo = params.REPOSITORY_NAME
    env.gitBranch = params.GIT_BRANCH
    env.baseUrl = props['baseUrl']
    env.gitUrl = "${env.baseUrl}/${env.projKey}/${env.gitRepo}.git"
   // env.gitCredentialsId = ['git-credentials']
   env.vaultcred = creds['vaultcred']
   env.VAULT_ADDR = props['VAULT_ADDR']

   //liquibase related env variables
   if(params.CHANELOG_FILE != null){
      env.changelog = params.CHANELOG_FILE
   } 

   if(params.ENVIRONMENT != null){
      env.envir = params.ENVIRONMENT
   }

   //servicenow related ENV variable
   env.HttpProxy = props['HTTP_PROXY']

   env.SNApi = props['SN_API']

   env.SNAPiupload = props['SN_API_UPLOAD']

   env.ServiceNow = creds['ServiceNow'] 
}