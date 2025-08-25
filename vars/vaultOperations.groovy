def generateToken(String namespace) {
    withCredentials([
        usernamePassword(credentialsId: 'vaultcred', passwordVariable: 'secretID', usernameVariable: 'roleID')]) {
        sh """
            curl -k \
            -H "X-Vault-Namespace: ${namespace}" \
            --request POST \
            --data '{"role_id": "${roleID}", "secret_id": "${secretID}"}' \
            ${env.VAULT_ADDR}/v1/auth/approle/login | jq -r .auth > token.json
        """
        }
        def authProps = readJSON file: 'token.json'
        def token = authProps['client_token']
        return token
}
