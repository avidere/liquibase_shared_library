def call() {
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
    if (params.CHANELOG_FILE != null) {
        env.changelog = params.CHANGE_LOG
    }

    if (params.ENVIRONMENT != null) {
        env.envir = params.ENVIRONMENT
    }

    //servicenow related ENV variable
    env.HttpProxy = props['HTTP_PROXY']

    env.SNApi = props['SN_API']

    env.SNAPiupload = props['SN_API_UPLOAD']

    env.ServiceNow = creds['ServiceNow']

    env.REQUEST_NUMBER = params.REQUEST_NUMBER
    env.SN_NUMBER = params.REQUEST_NUMBER

    //Nexus related ENV varaibles
    env.NexusCreds = creds['NexusCreds']

    env.nexusHost = props['NEXUS_PROD']

    env.nexusTagApiUrl = "${nexusHost}/service/rest/v1/tags"

    env.Nexus_Liquibase_repo = props['NEXUS_REPO']

    env. nexusUrl = "${nexusHost}/repository/${Nexus_Liquibase_repo}"

    env.groupId = params.ARTIFACT_GROUP

    env.uploadUrl = "${nexusUrl}/${projKey}/${gitRepo}/${groupId}"

    env.groupName = "${projKey}/${gitRepo}/${groupId}"

    env.artifact = "${projKey}-${gitRepo}-${groupId}"

    env.CurrentDate = "${new Date()}"

    env.successFile = 'Success_PipelineSummary.txt'

    env.failFile = 'Failure_PipelineSummary.txt'

    env.BUILD_TRIGGER_BY = currentBuild.getBuildCauses()[0].shortDescription + ' / ' + currentBuild.getBuildCauses()[0].userId
}
