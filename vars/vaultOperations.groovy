def call(){
    sh """
    curl -s --header \
    "X-Vault-Namespace: $VAULT_NAMESPACE" \
    --request POST --data '{
      "role_id": "78158080-4e46-35f6-e57f-4156a5384daa",
      "secret_id": "bee25694-600c-dc2b-16ca-fa9353d2e30d"
    }' \
     $VAULT_ADDR/v1/auth/approle/login | jq -r '.auth.client_token'

     """
    return
}
