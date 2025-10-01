def secret (String namespace, String path) {
    try {
        sh """
         
         (curl -k \
            -H "X-Vault-Token: \$VAULT_TOKEN" \
            -H "X-Vault-namespace: $namespace" \
            -H "Content-Type: application/json" \
            -X LIST \
            $VAULT_ADDR/v1/${path} | jq -r .data.keys[]) > secrets.txt
        """
    } catch (Exception e) {
        def comment = "Failed to serach vault due to exception $e"
        sh"echo '$comment' >> $failFile"
        currentBuild.result = "FAILURE"
    }
}

def call(String namespace, String path) {
    try {
        // Query Vault for the secret
        def response = sh(
            script: """
                curl -s -k \
                  -H "X-Vault-Token: ${VAULT_TOKEN}" \
                  -H "X-Vault-Namespace: ${namespace}" \
                  ${VAULT_ADDR}/v1/${path}
            """,
            returnStdout: true
        ).trim()

        // Parse JSON safely
        def json = readJSON text: response

        if (json?.data?.data) {
            // KV v2 -> secrets inside .data.data
            def secrets = json.data.data

            // Write to file only if needed
            writeFile file: "secrets.txt", text: secrets.toString()

            echo "✅ Secrets retrieved successfully for path: ${path}"
            return secrets
        } else {
            error "❌ No secrets found at Vault path: ${path}"
        }
    } catch (Exception e) {
        echo "❌ Failed to fetch Vault secrets: ${e.message}"
        currentBuild.result = "FAILURE"
        throw e
    }
}
