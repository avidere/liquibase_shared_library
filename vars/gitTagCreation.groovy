def call() {
    try {
        sh '''
        git -C ${WORKSPACE} tag ${projKey}=${gitRepo}-${COMMIT_ID}-${BUILD_NUMBER}
        git -C ${WORKSPACE} pish origin ${projKey}=${gitRepo}-${COMMIT_ID}-${BUILD_NUMBER}
        '''
    } catch (e) {
        echo "Error: An exception Occured during the Tag creation stage: ${e}"
        error("Tag Creation Failed")
        currentBuild.result = 'FAILURE'
    }
}    