def call () {
    try {

        sh """
            liquibase checks run --defaultsFile="${liquibasePropFile}" --changeLogFile="${changelog}" --output-file=policychecksoutput.txt --reports-enabled=true --report-name=checks_run_report.html

            """
    } catch (Exception e) {
        if (e.toString().contains("exit code 3")){
            println("\033[1;32m\033[0m")
            def url = "${JOB_URL}${BUILD_NUMBER}/execution/node/10/ws/policychecksoutput.txt"
            def checksmessage = "<a></a href='${url}'>"
            timeout(time: 30, unit: 'MINUTES') {
                input(
                    id: 'Deploy',
                    message: "\033[1;32mDrop/Delete Checks are detected against the SQL. Please click on console-output to review report and approve or abort the deployment: ${checksmessage}\033[0m",
                    ok: 'Approve',
                    submitter: driftApprover)
                }
            } else {
            println "Policychecks validation stage failed with exception."
            comment = "Policychecks validation stage failed with exception.\\n\\n"
            sh"echo '[INDO] $comment' >> $successfile"
            sh"echo '[INDO] $comment' >> $failFile"
            currentBuild.result = "FAILURE"
            error(e)
        }
    }
}
