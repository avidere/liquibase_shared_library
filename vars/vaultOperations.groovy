def getAddr(String namespace) {
    echo "Determining Vault address for namespace: ${namespace}"
    if (namespace.contains('vaultdev') || namespace.contains('smdev')) {
        print 'DEV VAULT'
        env.VAULT_ADDR = 'https://vault-cluster-public-vault-55c72033.42256dc0.z1.hashicorp.cloud:8200'
    } else if (namespace.contains('vaultqa') || namespace.contains('smqa')) {
        print 'QA VAULT'
        env.VAULT_ADDR = 'https://vault-cluster-public-vault-55c72033.42256dc0.z1.hashicorp.cloud:8200'
    } else if (namespace.contains('vaultuat') || namespace.contains('smuat')) {
        print 'UAT VAULT'
        env.VAULT_ADDR = 'https://vault-cluster-public-vault-55c72033.42256dc0.z1.hashicorp.cloud:8200'
    } else if (namespace.contains('vaultprod') || namespace.contains('smprod')) {
        print 'PROD VAULT'
        env.VAULT_ADDR = 'https://vault-cluster-public-vault-55c72033.42256dc0.z1.hashicorp.cloud:8200'
    }

    def parts = namespace.trim().replaceAll('/+$', '').tokenize('/')

    if (parts.size() >= 3) {
        prefix = parts[2]              
    } else {
        prefix = parts[1]
    }

    if (namespace.contains('token')){
        def envType = parts[0].contains(':') ? parts[0].split(':')[1] : parts[0]
        env.cred = prefix + '_token_' + envType
    } else {
        env.cred = prefix + '_approle_' + parts[1]   
    }
}

def generateToken(String namespace) {
    getAddr(namespace)
    withCredentials([usernamePassword(credentialsId: env.cred, passwordVariable: 'secretID', usernameVariable: 'roleID')]) {
       
        sh """
            curl -k \
            -H "X-Vault-Namespace: $namespace" \
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