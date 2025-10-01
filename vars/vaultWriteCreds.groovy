def dbUrl(String namespace, String path, String url) {
    try {
        sh """
        
         curl -k \
            -H "X-Vault-Token: $VAULT_TOKEN" \
            -H "X-Vault-namespace: $namespace" \
            -H "Content-Type: application/json" \
            -X POST \
            -d '{ "url": "\'$url\'" }' \
            $VAULT_ADDR/v1/${path}
        """
    } catch (Exception e) {
        def comment = "Failed to write vault secret due to exception $e"
        sh"echo '$comment' >> $failFile"
        currentBuild.result = "FAILURE"
    }
}
