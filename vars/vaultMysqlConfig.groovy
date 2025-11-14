def configureMySQL(String namespace, String path, String user, String pass) {

    sh """
        curl -sk \
        -H "X-Vault-Namespace: $namespace" \
        -H "X-Vault-Token: $VAULT_TOKEN" \
        -H "Content-Type: application/json" \
        --request POST \
        --data '{
            "plugin_name": "mysql-database-plugin",
            "connection_url": "{{username}}:{{password}}@tcp(mysql-demo.ctc40uae4eiz.us-east-1.rds.amazonaws.com:3306)/",
            "username": "${user}",
            "password": "${pass}",
            "max_open_connections": 4,
            "max_idle_connections": 0,
            "max_connection_lifetime": "0s"
        }' \
        ${VAULT_ADDR}/v1/admin/database/config/${path} | jq -r . > mysql_config_out.json
    """

    return readJSON(file: "mysql_config_out.json")
}
