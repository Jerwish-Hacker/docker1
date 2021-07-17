docker-build:
	docker build . -t sam/keycloak-custom-theme

docker-run:
	docker run \
	-p 8080:8080 \
	--name keycloak-custom \
	-e KEYCLOAK_USER=admin \
	-e KEYCLOAK_PASSWORD=admin \
	--net keycloak-network \
	-e DB_USER=keycloak \
	-e DB_PASSWORD=welcome1  \
	-e DB_VENDOR=POSTGRES \
	-e DB_ADDR=postgres \
	-it sam/keycloak-custom-theme:latest

docker-delete:
	docker rm keycloak-custom
