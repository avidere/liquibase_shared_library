def call(){
    try {
        sh """
            liquibase --defaultsFile=config/liquibase.properties update-sql --changelogFile="${changelog}" --output-file=updatesql.txt
        """
        def url = "${JOB_URL}${BUILD_NUMBER}/execution/node/10/ws/updatesql.txt"
        def linkedmessage = "<a></a href='${url}'>"
        timeout(time: 30, unit: 'MINUTES') {
            input(
                    id: 'Deploy',
                    message: "\033[1;32mPlease review the script before proceeding with the deployment: ${linkedmessage}\033[0m",
                    ok: 'Approve',
                    submitter: sqlApprover)
            log = currentBuild.rawBuild.getLog(2)
            log.each { line ->
                if (!line.contains('truncated')) {
                    println "Script is ${line} ... and SQL Review stage is executed Successfully"
                    comment = "Script is ${line} ... and SQL Review stage is executed Successfully\\n\\n"
                    sh"echo '[INDO] $comment' >> $successfile"
                    sh"echo '[INDO] $comment' >> $failFile"
                }

            }
        }
    } catch (Exception e){
        comment = "SQL Review stage failed with exception"
        sh"echo '[ERROR] $comment' >> $successfile"
        echo"Error: An Exception occured in SQL review stage: ${e}"
        currentBuild.result = 'FAILURE'
        error(e)
    }
}
