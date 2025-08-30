def call() {
    currentBuild.displayName = SN_NUMBER + "-" + envir + "-" + BUILD_NUMBER
    try{
        if ("${envir}".toLowerCase().contains("prod") && !"${REQUEST_NUMBER}".startsWith("CHG")) {
            comment = "Please provide change request since you are deploying to production\\n\\n"
            sh "echo '[ERROR] $comment' >> $failFile"
            throw new Exception(comment)
        }
        SNValidation()
    }
    catch (Exception e) {
        def comment = "$env.STAGE_NAME stage failed.\\n\\n"
        echo "[ERROR] $comment >> $failFile"
        currentBuild.result = 'FAILURE'
        error(e)
    }
}