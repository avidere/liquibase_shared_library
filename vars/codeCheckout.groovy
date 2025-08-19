/* groovylint-disable-next-line MethodReturnTypeRequired */
def call(){
         try {
            checkout([
                $class: 'GitSCM',
                branches: [[name: '*/main']],
                userRemoteConfigs: [[credentialsId: 'gitCredentialsId', url: gitUrl]]
            ])
            currentBuild.displayName = ${REPOSITORY_NAME} + env.BUILD_NUMBER
            echo "Checkout successful: ${gitUrl}"
        } catch (Exception e) {
            echo "Checkout failed: ${e.getMessage()}"
    }
}
