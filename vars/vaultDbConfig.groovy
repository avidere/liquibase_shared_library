def call(){
    sh '''
            curl -sk \
            -H "X-Vault-Namespace: admin" \
            -H "X-Vault-Token: $VAULT_TOKEN" \
            -H "Content-Type: application/json" \
            --request POST \
            --data '{
                "plugin_name": "mysql-database-plugin",
                "connection_url": "{{username}}:{{password}}@tcp(demo-mysql-db.cr24o06285ta.ap-south-1.rds.amazonaws.com:3306)/",
                "username": "admin",
                "password": "8F8%?YbhSDp?uQOw",
                "max_open_connections": 4,
                "max_idle_connections": 0,
                "max_connection_lifetime": "0s"
            }' \
            $VAULT_ADDR/v1/database/config/503027034_654654373515_aurora_demo-mysql-db_admin
'''
}