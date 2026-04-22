def getAddr(String vaultNS) {

    if (!vaultNS?.trim()) {
        error "❌ vaultNS is empty"
    }

    echo "Namespace received: ${vaultNS}"

    // -------- Clean namespace --------
    def cleanNS = vaultNS.trim().replaceAll('/+$', '')
    def parts = cleanNS.tokenize('/')

    // 👉 FIX: ensure at least 2 parts exist
    if (parts.size() < 2) {
        error "❌ Invalid namespace format: ${vaultNS}"
    }

    // -------- Set VAULT ADDR --------
    if (vaultNS.contains('vaultdev') || vaultNS.contains('smdev')) {
        echo 'DEV VAULT'
        env.VAULT_ADDR = 'https://vault-cluster-public-vault-55c72033.42256dc0.z1.hashicorp.cloud:8200'
    } else if (vaultNS.contains('vaultqa') || vaultNS.contains('smqa')) {
        echo 'QA VAULT'
        env.VAULT_ADDR = 'https://vault-cluster-public-vault-55c72033.42256dc0.z1.hashicorp.cloud:8200'
    } else if (vaultNS.contains('vaultuat') || vaultNS.contains('smuat')) {
        echo 'UAT VAULT'
        env.VAULT_ADDR = 'https://vault-cluster-public-vault-55c72033.42256dc0.z1.hashicorp.cloud:8200'
    } else if (vaultNS.contains('vaultprod') || vaultNS.contains('smprod')) {
        echo 'PROD VAULT'
        env.VAULT_ADDR = 'https://vault-cluster-public-vault-55c72033.42256dc0.z1.hashicorp.cloud:8200'
    }

    // -------- FIX: safe prefix logic --------
    def prefix
    if (parts.size() >= 3) {
        prefix = parts[1] + '_' + parts[2]
    } else {
        prefix = parts[1]
    }

    def parent = parts[0]

    if (vaultNS.contains('token')) {
        def envType = parent.contains(':') ? parent.split(':')[1] : parent
        env.cred = prefix + '_token_' + envType
    } else {
        env.cred = prefix + '_approle_' + parent
    }

    echo "Using credentialsId: ${env.cred}"
}
def generateToken(String vaultNS) {

    getAddr(vaultNS)

    withCredentials([
        usernamePassword(
            credentialsId: env.cred, 
            usernameVariable: 'roleID',
            passwordVariable: 'secretID'
        )
    ]) {

        sh """
            curl -k \
            -H "X-Vault-Namespace: ${vaultNS}" \
            --request POST \
            --data '{
                "role_id": "${roleID}",
                "secret_id": "${secretID}"
            }' \
            ${env.VAULT_ADDR}/v1/auth/approle/login | jq -r .auth > token.json
        """
    }

    def authProps = readJSON file: 'token.json'
    return authProps['client_token']
}