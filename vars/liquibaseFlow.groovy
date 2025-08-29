def appci(String flowfile) {
    try {
        def flowfiles = libraryResource("config/flowfiles/liquibase-ci.flowfile.yaml")
        writeFile file: 'flowfile.yaml', text: flowfiles
        comment = 'Starting Liquibase Execution'
        sh "echo '[INFO] $timestamp ${comment}' >> $successFile"
        sh "echo '[INFO] $timestamp ${comment}' >> $failFile"

        sh """
                liquibase --defaultsFile=liquibase.properties \
                        flow --flowfile=${flowfiles} --output-file=output.txt --log-file-liquibase.log
                """
        } catch (Exception e) {
        failed_stage = sh(
                    returnStdout: true,
                    script: "jq -r '(select(.flowFileFailedStage != null) | .floFileFailedStage)' " +
                            'liquibase.log | head -1 || true'
                ).trim()
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
        def flowfiles = libraryResource("config/flowfiles/${flowfile}")
        writeFile file: 'liquibase-flowfile.yaml', text: flowfiles

        comment = 'Starting Liquibase Execution'
        sh "echo '[INFO] $timestamp ${comment}' >> $successFile"
        sh "echo '[INFO] $timestamp ${comment}' >> $failFile"

        sh '''
                liquibase --defaultsFile=liquibase.properties \
                        flow --flowfile=liquibase-cd.flowfile.yaml --output-file=output.txt --log-file-liquibase.log
                '''
        } catch (Exception e) {
        failed_stage = sh(
                    returnStdout: true,
                    script: "jq -r '(select(.flowFileFailedStage != null) | .floFileFailedStage)' " +
                            'liquibase.log | head -1 || true'
                ).trim()
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
