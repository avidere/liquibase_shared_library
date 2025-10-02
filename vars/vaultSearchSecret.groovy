def call(String namespace, String path) {
    try {
        sh """
        (curl -s -k \
            -H "X-Vault-Token: $VAULT_TOKEN" \
            -H "X-Vault-Namespace: $namespace" \
            -X LIST \
            $VAULT_ADDR/v1/kv/metadata/${path}\
            | jq -r '.data.keys[]') > secrets.txt
            """
    } catch (Exception e) {
        def comment = "Failed to serach vault due to exception $e"
        sh"echo '$comment' >> $failFile"
        currentBuild.result = "FAILURE"
    }
}

