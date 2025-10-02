def dba(String flowfile) {
    try {
        def flowfiles = libraryResource "config/flowfiles/${flowfile}"
        writeFile file: 'flowfile.yaml', text: flowfiles

        def excludedata = libraryResource "config/logging-excludedata.yaml"
        writeFile file : 'logging-excludedata.yaml', text: excludedata

        comment = "Starting Liquibase Execution for ${REQUEST_TYPE} workflow\\n\\n"
        sh "echo '[INFO]  ${comment}' >> $successFile"
        sh "echo '[INFO]  ${comment}' >> $failFile"

        if ("${ACCESS_REQUEST}" == "true") {
            env.LIQUIBASE_CUSTOM_LOG_DATA_FILE = "logging-excludedata.yaml"
            env.LIQUIBASE_REPORTS_ENABLED = "false"
        } else {
            env.ACCESS_REQUEST = "false"
            env.LIQUIBASE_REPORTS_ENABLED = "true"
        }

        sh """
                liquibase --defaultsFile=${liquibasePropFile} flow --flowfile=flowfile.yaml --output-file=output.txt --log-file=liquibase.log
            """

        def timestamp = new Date().format("yyyy-MM-dd'T'HH:mm:ss.SSSZ", TimeZone.getTimeZone('UTC'))
        comment = "Liquibase Execution is Successful for ${REQUEST_TYPE} workflow\\n\\n"
        sh "echo '[INFO] $timestamp ${comment}' >> $successFile"

        comment "$sql_file is deployed successfully on $DB_NAME\\n\\n"
        sh "echo '[INFO] $timestamp ${comment}' >> $successFile"

        if (ACCESS_REQUEST) {
            comment "$ACCESS_TYPE access for $USER_ID on $DB_NAME\\n\\n"
            sh "echo '[INFO] $timestamp ${comment}' >> $successFile"
        }

        } catch (Exception e) {
        def failed_stage = sh(returnStdout: true, script: "jq -r '(select(.flowFileFailedStage != null) | .flowFileFailedStage)' " + 'liquibase.log | head -1 || true').trim()
        echo "Failed Stage: ${failed_stage}"
        comment = "Liquibase Execution Failed at Stage: ${failed_stage} \\n"
        sh "echo '[ERROR] $timestamp ${comment}' >> $failFile"

        comment = "For failure reason chack file errorLog_${BUILD_NUMBER}.txt attached to ticket\\n"
        sh "echo '[INFO] $timestamp ${comment}' >> $failFile"

        sh """
                jq -r '(select(.flowFileFailedStage != null) | .flowFileFailedMessage)' \
                    liquibase.log > errorLog_${BUILD_NUMBER}.txt "
                """
        liquibaseReportUpload.errorLog()
        currentBuild.result = 'FAILURE'
        error(e)
    }
}

def appci(String flowfile) {
    try {
        def flowfiles = libraryResource "config/flowfiles/${flowfile}"
        writeFile file: 'flowfile.yaml', text: flowfiles

        def timestamp = new Date().format("yyyy-MM-dd'T'HH:mm:ss.SSSZ", TimeZone.getTimeZone('UTC'))
        comment = 'Starting Liquibase Execution'
        sh "echo '[INFO] $timestamp ${comment}' >> $successFile"
        sh "echo '[INFO] $timestamp ${comment}' >> $failFile"

        sh '''
                liquibase --defaultsFile=config/liquibase.properties flow --flowfile=flowfile.yaml --output-file=output.txt --log-file=liquibase.log
                '''
        } catch (Exception e) {
        def failed_stage = sh(returnStdout: true, script: "jq -r '(select(.flowFileFailedStage != null) | .flowFileFailedStage)' " + 'liquibase.log | head -1 || true').trim()
        echo "Failed Stage: ${failed_stage}"
        comment = "Liquibase Execution Failed at Stage: ${failed_stage} \\n"
        sh "echo '[ERROR] $timestamp ${comment}' >> $failFile"

        comment = "For failure reason chack file errorLog_${BUILD_NUMBER}.txt attached to ticket\\n"
        sh "echo '[INFO] $timestamp ${comment}' >> $failFile"

        sh """
                jq -r '(select(.flowFileFailedStage != null) | .flowFileFailedMessage)' \
                    liquibase.log > errorLog_${BUILD_NUMBER}.txt "
                """
        liquibaseReportUpload.errorLog()
        currentBuild.result = 'FAILURE'
        error(e)
    }
}

def appcd(String flowfile) {
    try {
        def flowfiles = libraryResource "config/flowfiles/${flowfile}"
        writeFile file: 'flowfile.yaml', text: flowfiles

        def timestamp = new Date().format("yyyy-MM-dd'T'HH:mm:ss.SSSZ", TimeZone.getTimeZone('UTC'))
        comment = 'Starting Liquibase Execution'
        sh "echo '[INFO] $timestamp ${comment}' >> $successFile"
        sh "echo '[INFO] $timestamp ${comment}' >> $failFile"

        sh '''
                liquibase --defaultsFile=config/liquibase.properties flow --flowfile=flowfile.yaml --output-file=output.txt --log-file=liquibase.log
                '''
        } catch (Exception e) {
        def failed_stage = sh(returnStdout: true, script: "jq -r '(select(.flowFileFailedStage != null) | .flowFileFailedStage)' " + 'liquibase.log | head -1 || true').trim()
        echo "Failed Stage: ${failed_stage}"
        comment = "Liquibase Execution Failed at Stage: ${failed_stage} \\n"
        sh "echo '[ERROR] $timestamp ${comment}' >> $failFile"

        comment = "For failure reason chack file errorLog_${BUILD_NUMBER}.txt attached to ticket\\n"
        sh "echo '[INFO] $timestamp ${comment}' >> $failFile"

        sh """
                jq -r '(select(.flowFileFailedStage != null) | .flowFileFailedMessage)' \
                    liquibase.log > errorLog_${BUILD_NUMBER}.txt "
                """
        liquibaseReportUpload.errorLog()
        currentBuild.result = 'FAILURE'
        error(e)
    }
}
