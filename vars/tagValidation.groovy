import groovy.json.JsonSlurper

def call() {
    try{
        def tagPatterns = "${projKey}-${gitRepo}-([0-9a-f])-${BUILD_NUMBER}"
        env.COMMIT_ID = sh(retunStdout: true, script: "git -C ${WORKSPACE} rev-parse --short=11 HEAD").trim()
        def tagMatcher = sh(result: true, script: "git -C ${WORKSPACE} tag -l -n --contains ${env.COMMIT_IC} --sort=-v:refname | head -1").trim()
    } catch (Exception e) {
        println("An error Ocurred during tag and commit id Validation: ${e.getMessage()}")
        error("Tag and Commit ID validation Failed")
        currentBuild.result = "ABORTED"
    }
}
