def dba(){
    currentBuild.displayName = SN_NUMBER + "-" + envir + "-" + Request_Type.substring(0,3) + "-" + BUILD_NUMBER
    try{
        unstash 'FILE'
        file = sh(returnStdout: true, script: "if[ -z \"\$FILE_FILENAME\" ]; then echo no; fi").trim()
        if (file == "no") {
            echo "No input provided for File parameter. please re-trigger the build with SQL script uploaded to FILE parameter.\\n\\n"
            sh"echo '[ERROR] $comment' >> $failFile"
            error(comment)
        }

        if(params.ACCES_REQUEST && params.USER_ID.isEmpty()) {
            def message = "Enter the user ID for which acces change is requested:"
            env.USER_ID = getInput.stringValue(message)
        }

        if("${DBType}" != "SQL") {
            lookupSecret()
        }

        SNValidation()
    } catch (Exception e){
        def comment = "$env.STAGE_NAME failed.\\n\\n"
        sh"echo '[ERROR] $comment' >> $failFile"
        currentBuild.result = "FAILURE"
        error(e)
    }
}

def cloud() {
    currentBuild.displayName = SN_NUMBER + "-" + envir + "-" + Request_Type.substring(0,3) + "-" + BUILD_NUMBER
    try{
        if (FILENAME == '') {
            comment = "No input provided for FILENAME parameter"
            sh"echo'[ERRO] $comment' >> $failFile"
            error(comment)
        }

        if (params.ACCES_REQUEST && params.USER_ID.isEmpty()) {
            def message = "Enter the user ID for which acces change is requested:"
            env.USER_ID = getInput.stringValue(message)
        }
    } catch(Exception e) {
        def comment = "$env.STAGE_NAME failed.\\n\\n"
        sh"echo '[ERROR] $comment' >> $failFile"
        currentBuild.result = "FAILURE"
        error(e)
    }
}

def app() {
    currentBuild.displayName = SN_NUMBER + '-' + envir + '-' + BUILD_NUMBER
    try {
        if ("${envir}".toLowerCase().contains('prod') && !"${REQUEST_NUMBER}".startsWith('CHG')) {
            comment = 'Please provide change request since you are deploying to production\\n\\n'
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
