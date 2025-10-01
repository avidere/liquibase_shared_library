def call(String namespace, String path) {
    try {
        sh """
         
         (curl -k \
            -H "X-Vault-Token: \$VAULT_TOKEN" \
            -H "X-Vault-namespace: $namespace" \
            -H "Content-Type: application/json" \
            -X LIST \
            $VAULT_ADDR/v1/kv/Oracle/dev | jq -r .data.keys[]) > secrets.txt
        """
    } catch (Exception e) {
        def comment = "Failed to serach vault due to exception $e"
        sh"echo '$comment' >> $failFile"
        currentBuild.result = "FAILURE"
    }
}

