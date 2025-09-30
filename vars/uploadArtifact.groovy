def artifactupload() {
    try {
        def artifactName = "${artifact}-${BUILD_NUMBER}"
        withCredentials([usernamePassword(credentialsId: NexusCreds, passwordVariable: 'PASSWORD', usernameVariable: 'USER')]) {
            sh(
                script: """
                    set -x
                    
                    RResponse=\$(curl -v -u ${USER}:${PASSWORD} --upload-file ${artifactName}.zip ${uploadUrl}/${artifactName}.zip -s -o /dev/null -w "%{http_code}")
                    echo "RResponse: \$RResponse"
                    if [ "\$RResponse" = "201" ]; then
                        echo "Success"
                    else
                        echo " Failure"
                        exit 1;
                    fi
                """

            )
        }

        echo "Artifact has been uploaded to nexus Successfully"
        comment = "Artifact has been uploaded to nexus Successfully.\\n\\n"
        sh"echo '[INFO] $comment' >> $successFile"
    } catch (e) {
        echo "Error: An exception Ocuured during the upload to nexus stage"
        comment = "$e\\n\\n"
        sh"echo '[ERROR] $comment' >> $failFile"
        error("Upload to Nexus failed")
        currentBuild.result = 'FAILURE'

    }
}

def snapshotupload(){
            withCredentials([usernamePassword(credentialsId: NexusCreds, passwordVariable: 'PASSWORD', usernameVariable: 'USER')]) {
            sh(
                script: """
                    set -x
                    RResponse=\$(curl -v -u ${USER}:${PASSWORD} --upload-file ${SCHEMA_NAME}_Snapshot_${ENVIRONMENT}.json ${nexusUrl}/${PROJECT_KEY}/${REPOSITORY_NAME}/Snapshot/${ENVIRONMENT}/${SCHEMA_NAME}_Snapshot_${ENVIRONMENT}.json -s -o /dev/null -w "%{http_code}")
                    echo "RResponse: \$RResponse"
                    if [ "\$RResponse" = "201" ]; then
                        echo "Success"
                    else
                        echo " Failed to upload artifact to Nexus"
                        exit 1;
                    fi
                """

            )
        }
}