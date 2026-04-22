def getAddr(String namespace) {
    if (namespace.contains(vaultdev) || namespace.contains(smdev)) {
        print 'DEV VAULT'
        env.VAULT_ADDR = 'https://vault-cluster-public-vault-55c72033.42256dc0.z1.hashicorp.cloud:8200'
    } else if (namespace.contains(vaultqa) || namespace.contains(smqa)) {
        print 'QA VAULT'
        env.VAULT_ADDR = 'https://vault-cluster-public-vault-55c72033.42256dc0.z1.hashicorp.cloud:8200'
    } else if (namespace.contains(vaultuat) || namespace.contains(smuat)) {
        print 'UAT VAULT'
        env.VAULT_ADDR = 'https://vault-cluster-public-vault-55c72033.42256dc0.z1.hashicorp.cloud:8200'
    } else if (namespace.contains(vaultprod) || namespace.contains(smprod)) {
        print 'PROD VAULT'
        env.VAULT_ADDR = 'https://vault-cluster-public-vault-55c72033.42256dc0.z1.hashicorp.cloud:8200'
    }

    if (namespace.count("/") == 2) {
        prefix = namespace.split('/')[1]+'_'+namespace.split('/')[2]
    } else {
        prefix = namespace.split('/')[1]
    }

    if (namespace.contains('token')){
        env.cred = prefix+'_token_'+(namespace.split('/')[0]).split(':')[1]
    } else {
        env.cred = prefix+'_approle_'+(namespace.split('/')[0])
    }
}
def generateToken(String vaultNS) {
    getAddr(namespace)
    withCredentials([usernamePassword(credentialsId: 'cred', passwordVariable: 'secretID', usernameVariable: 'roleID')]) {
       
        sh """
            curl -k \
            -H "X-Vault-Namespace: ${vaultNS}" \
            --request POST \
            --data '{
                "role_id": "${roleID}",
                "secret_id": "${secretID}"
            }' \
            ${VAULT_ADDR}/v1/auth/approle/login | jq -r .auth > token.json
        """
        
    }
    def authProps = readJSON file: 'token.json'
    def token = authProps['client_token']
    return token
}
