def createStaticRole(String namespace, String path, String user, String pass) {

    sh """
        curl -sk \
        -H "X-Vault-Namespace: $namespace" \
        -H "X-Vault-Token: $VAULT_TOKEN"" \
        -H "Content-Type: application/json" \
        --request POST \
        --data '{
            "db_name": "mysqldev-db",
            "username": "${username}",
            "rotation_statements": "ALTER USER \`${username}\`@\"%\" IDENTIFIED BY '{{password}}';"
        }' \
        ${VAULT_ADDR}/v1/admin/database/static-roles/mysqldev-static | jq -r . > static_role_out.json
    """

    return readJSON(file: "static_role_out.json")
}
