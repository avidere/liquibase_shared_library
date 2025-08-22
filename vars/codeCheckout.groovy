/* groovylint-disable-next-line MethodReturnTypeRequired */
def call(){
         try {
            checkout([
                $class: 'GitSCM',
                branches: [[name: '*/$BRANCH_NAME']],
                userRemoteConfigs: [[credentialsId: 'gitCredentialsId', url: gitUrl]]
            ])
            currentBuild.displayName = repositoryName + "-" + BUILD_NUMBER
            echo "Checkout successful: ${gitUrl}"
        } catch (Exception e) {
            echo "Checkout failed: ${e.getMessage()}"
    }
}
