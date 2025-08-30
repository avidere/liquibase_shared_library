def call(){
    try {
        if ( (JOB_NAME.contains("Liquibase")) ){
            if ( "${REQUEST_NUMBER}".contains("CHG")){
                println "CR validation for liquibase Pipelines"
                CRValidationAPP(ServiceNow, REQUEST_NUMBER, HttpProxy, SNApi)
            } else if ( "${REQUEST_NUMBER}".contains("RITM")){
                println "RITM validation for liquibase Pipelines"
                RITMValidationAPP(ServiceNow, REQUEST_NUMBER, HttpProxy, SNApi)
            } else {
                println "Jira validation for liquibase Pipelines"
                JiraValidation(REQUEST_NUMBER)
            }
        }
        println "ServiceNow Validation for Liquibase Pipelines is completed"
        comment = "ServiceNow Validation for Liquibase Pipelines is completed\\n\\n"
        sh "echo '[INFO] $comment' >> $successFile"
    } catch (Exception e) {
        def comment = "An Exeception Occured during ServiceNow validation. \\n\\n"
        echo "[ERROR] $comment >> $failFile"
        currentBuild.result = 'FAILURE'
        error(e)
    }
}