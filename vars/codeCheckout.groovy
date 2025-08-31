/* groovylint-disable-next-line MethodReturnTypeRequired */
def call() {
    try {
        checkout([
                $class: 'GitSCM',
                branches: [[name: '*/$BRANCH_NAME']],
                userRemoteConfigs: [[credentialsId: 'gitCredentialsId', url: gitUrl]]
            ])
        currentBuild.displayName = REQUEST_NUMBER + '-' + BUILD_NUMBER
        echo "Checkout successful: ${gitUrl}"
        } catch (hudson.plugins.git.GitException e) {
        /* groovylint-disable-next-line UnnecessaryGetter */
        echo "Checkout failed: ${e.getMessage()}"
    }
}
