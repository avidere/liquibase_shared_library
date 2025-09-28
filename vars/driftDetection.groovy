def call () {
    try {
        withCredentials([usernamePassword(credentialsId: "${NexusCreds}", passwordVariable: 'PASSWORD', usernameVariable: 'USER')]) {
            def NexusToken = "${USER}:${PASSWORD}".bytes.encodeBase64().toString()
            echo "${NexusToken}"
            sh """
                set +x
                Response=\$(curl -v -u ${USER}:${PASSWORD} -X GET ${nexusUrl}/${PROJECT_KEY}/${REPOSITORY_NAME}/Snapshot/${ENVIRONMENT}/${SCHEMA_NAME}_Snapshot_${ENVIRONMENT}.json -s -o /dev/null -w "%{http_code}")

                echo "Response: \$Response"

                if [ "\$Response" == "200" ]; then
                    echo "Success - Snapshot file exists. Performing Drift Detection..."

                    liquibase --defaultsFile=config/liquibase.properties diff --diff-types=catalogs,checkconstraints,columns,data,databasepackage,databasepackagebody,foreignkeys,functions,indexes,primarykeys,sequences,storedprocedures,tables,triggers,uniqueconstraints,views --reference-url="offilne:${DBType}?snapshot=${SCHEMA_NAME}_Snapshot_${ENVIRONMENT}.json" --drift-severity=2 --default-schema-name=${SCHEMA_NAME} --output-file=drift_detect.json --reports-enabled=true --report-name=drift_report.html
                else
                    echo "Snapshot doesn't exist... skipping Drift detection stage"
                fi
                """
        }
    } catch (Exception e) {
        if (e.toString().contains("exit code 2")){
            println("\033[1;32mLiquibase has detected changes in database performed outsside of automation process. please review the drift dection report and approve or abort the deployment..\033[0m")
            def url = "${JOB_URL}${BUILD_NUMBER}/execution/node/10/ws/drift_report.html"
            def driftmessage = "<a></a href='${url}'>"
            timeout(time: 30, unit: 'MINUTES') {
                input(
                    id: 'Deploy',
                    message: "\033[1;32mplease review the drift dection report and approve or abort the deployment: ${driftmessage}\033[0m",
                    ok: 'Approve',
                    submitter: driftApprover)
                log = currentBuild.rawBuild.getLog(2)
                log.each { line ->
                    if (!line.contains('truncated')) {
                        println "Liquibase has detected changes in database performed outsside of automation process. please review the drift dection report and approve or abort the deployment."
                        comment = "Liquibase has detected changes in database performed outsside of automation process. please review the drift dection report and approve or abort the deployment\\n\\n"
                        sh"echo '[INDO] $comment' >> $successfile"
                        sh"echo '[INDO] $comment' >> $failFile"
                    }

                }
            } 
        }
    }
}
