def call() {

    sh """
            curl -sk -H "X-Vault-Namespace: admin" \
            -H "X-Vault-Token: $VAULT_TOKEN" \
            -H "Content-Type: application/json" \
            --request POST \
            --data '{
                "db_name": "503027034_654654373515_aurora_demo-mysql-db_admin",
                "rotation_statements": [
                    "ALTER USER `admin`@`%` IDENTIFIED BY \"{{password}}\";"
                ],
                "username": "admin",
                "password": "8F8%?YbhSDp?uQOw"
            }' \
            $VAULT_ADDR/v1/database/static-roles/aurora_demo-mysql-db_admin

    """
}
