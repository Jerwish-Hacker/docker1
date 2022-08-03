all: docker-compose-down docker-image-delete docker-build docker-compose-up

docker-build:
	docker build . -t cogniti/keycloak-custom-theme

docker-compose-up:
	docker-compose up -d

docker-run:
	docker run -d \
#	-p 8080:8080 \
	-p 9990:8080 \
  	-p 9991:8443 \
	--name keycloak-custom \
	--net keycloak-network \
	-e KEYCLOAK_USER=admin \
	-e KEYCLOAK_PASSWORD=admin \
	-e DB_USER=keycloak \~
	-e DB_PASSWORD=password  \
	-e DB_DATABASE=keycloak \
	-e DB_VENDOR=POSTGRES \
	-e DB_ADDR=postgres \
	-e PROXY_ADDRESS_FORWARDING=true \
	-it cogniti/keycloak-custom-theme:latest

docker-compose-down:
	docker-compose down

docker-stop:
	docker stop keycloak-custom

docker-delete:
	docker rm keycloak-custom

docker-image-delete:
	docker rmi cogniti/keycloak-custom-theme