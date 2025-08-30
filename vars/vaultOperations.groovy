def generateToken(String namespace) {
    withCredentials([
        usernamePassword(credentialsId: 'vaultcred', passwordVariable: 'secretID', usernameVariable: 'roleID')]) {
        sh """
            curl -k \
            -H "X-Vault-Namespace: admin" \
            --request POST \
            --data '{
                "role_id": "${roleID}",
                "secret_id": "${secretID}"
            }' \
            https://vault-cluster-1-public-vault-399c773d.4213bc99.z1.hashicorp.cloud:8200/v1/auth/approle/login | jq -r .auth > token.json
        """
        }
        def authProps = readJSON file: 'token.json'
        def token = authProps['client_token']
        return token
}
