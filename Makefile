docker-build:
	docker build . -t sam/keycloak-custom

docker-run:
	docker run -d \
	-p 8080:8080 \
	--name keycloak-custom \
	--net keycloak-network \
	-e KEYCLOAK_USER=admin \
	-e KEYCLOAK_PASSWORD=admin \
	-e DB_USER=keycloak \
	-e DB_PASSWORD=password  \
	-e DB_DATABASE=keycloak \
	-e DB_VENDOR=POSTGRES \
	-e DB_ADDR=postgres \
	-e PROXY_ADDRESS_FORWARDING=true \
	-it sam/keycloak-custom:latest

docker-stop:
	docker stop keycloak-custom

docker-delete:
	docker rm keycloak-custom

docker-image-delete:
	docker rmi sam/keycloak-custom