def call() {
    try{
        env.SUBSTR_COMPONENT = sh(script: "echo ${params.component_URL} | sed 's|${nexusUrl}/||'",returnStdout: true).trim()

        withCredentials([usernamePassword(credentialsId: "${NexusCreds}", passwordVariable: 'PASSWORD', usernameVariable: 'USER')]) {
            def NexusToken = "${USER}:${PASSWORD}".bytes.encodeBase64().toString()
            echo "${NexusToken}"
            sh """
                set +x
                Response=\$(curl -v -u ${USER}:${PASSWORD} -X GET ${nexusUrl}/${PROJECT_KEY}/${REPOSITORY_NAME}/Snapshot/${ENVIRONMENT}/${SCHEMA_NAME}_Snapshot_${ENVIRONMENT}.json -s -o /dev/null -w "%{http_code}")

                echo "Response: \$Response"

                if [ "\$Response" == "200" ]; then 
                    echo "Success - Snapshot file exists. Downloading..."
                    wget --header 'Authorization: Basic ${NexusToken}' --progress=bar:force \\
                    "${nexusUrl}/${PROJECT_KEY}/${REPOSITORY_NAME}/Snapshot/${ENVIRONMENT}/${SCHEMA_NAME}_Snapshot_${ENVIRONMENT}.json"
                else
                    echo "Snapshot doesn't exist."
                fi

                echo "Downloading component artifact..."
                wget --header 'Authorization: Basic ${NexusToken}' --progress=bar:force "${params.component_URL}"
            """

            env.COMPONENT_FILE = sh(returnStdout: true,script: "echo \"${params.component_URL}\" | rev | cut -d/ -f1 | rev").trim()

            sh "unzip -q \"${env.COMPONENT_FILE}\""
        }
        def propFileContent = 'artifact.properties'
        def artifactProps = readProperties file: propFileContent

        env. validationFilePath = artifactProps['validationFile']

        env.changelog = artifactProps[changelogName]
        sh """
            set +XV
            envsubst < $changelog > ${changelog}_updated
            rm $changelog
            mv ${changelog}_updated $changelog
        """
        echo "Artifact has been downloaded Successully"
        comment = "Artifact has been downloaded Successully.\\n\\n"
        sh"echo '[INFO] $comment' >> $successFile"
    } catch (Exception e) {
        comment = "Failed to download and unzip component : $e"
        sh"echo '[ERROR] $comment' >> $failFile"
        echo " Error: An exception occured in downloadArtifact stage: ${e}"
        currentBuild.result = "FAILURE"
        error(e)
    }
}
