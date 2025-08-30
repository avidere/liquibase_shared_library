def generateToken(String namespace) {
    withCredentials([
        usernamePassword(credentialsId: 'vaultcred', passwordVariable: 'secretID', usernameVariable: 'roleID')]) {
        sh """
            curl -k \
            -H "X-Vault-Namespace: admin" \
            --request POST \
            --data '{
                "role_id": "bb5323d8-51a1-3cae-a732-f1325e3ad66d",
                "secret_id": "01d0613a-53f0-cfdc-f621-102f321f7061"
            }' \
            https://vault-cluster-1-public-vault-399c773d.4213bc99.z1.hashicorp.cloud:8200/v1/auth/approle/login | jq -r .auth > token.json
        """
        }
        def authProps = readJSON file: 'token.json'
        def token = authProps['client_token']
        return token
}
