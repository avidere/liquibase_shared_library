def artifactupload() {
    try {
        def artifactName = "${artifact}-${BUILD_NUMBER}"
        withCredentials([usernamePassword(credentialsId: NexusCreds, passwordVariable: 'PASSWORD', usernameVariable: 'USER')]) {
            sh(
                script: """
                    set -x
                    echo ${COMMIT_ID}
                    RResponse=\$(curl -v -u ${USER}:${PASSWORD} --upload-file ${artifactName}.zip ${uploadUrl}/${artifactName}.zip -s -o /de/null -w "%{http_code}")
                    echo "RResponse: \$RResponse"
                    if [ "\$RResponse" == "201" ]; then
                        echo "Success"
                    else
                        echo " Failure"
                        exit 1;
                    fi
                """

            )
        }

        echo "Artifact has been uploaded to nexus Successfully"
        commet = "Artifact has been uploaded to nexus Successfully.\\n\\n"
        sh"echo '[INFO] $comment' >> $successFile"
    } catch (e) {
        echo "Error: An exception Ocuured during the upload to nexus stage"
        comment = "$e\\n\\n"
        sh"echo '[ERROR] $comment' >> $failFile"
        error("Upload to Nexus failed")
        currentBuild.result = 'FAILURE'

    }
}