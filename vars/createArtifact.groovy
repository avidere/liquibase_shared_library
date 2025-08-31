def call() {
    try {
        sh '''
            echo changelogName=${changelog} >> artifact.properties
            echo branch=${gitBranch} >> artifact.properties
            echo artifactGroup=${groupId} >> artifact.properties
            echo validationFile=${validationFile} >> artifact.properties

            if [[ ${changelog} =~ "xml" ]]; then
                scriptPath=$(grep -o 'path="[^"]*"' "${changelog}" | cut -d'"' -f2)
            elif [[ ${changelog} =~ "yaml" ]]; then
                scriptPath=$(grep -w "path:" "${changelog}" | cut -d: -f2 | tr -d " ")
            fi

            echo $scriptPath
            cp -f liquibase.properties_from_source config/liquibase.properties
            zip -r ${WORKSPACE}/${artifact}-${BUILD_NUMBER}.zip ${changelog} config/liquibase.properties artifact.properties config/* $validationFile
        '''
    } catch (e) {
        echo "Error: An exception during zip workspace stage: ${e}"
        error('zip workspace failed')
        currentBuild.result = 'FAILURE'
    }
}
