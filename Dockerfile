FROM jboss/keycloak:14.0.0

USER root

WORKDIR /

ADD /config/standalone.xml /opt/jboss/keycloak/standalone/configuration/standalone.xml
ADD /config/standalone-ha.xml /opt/jboss/keycloak/standalone/configuration/standalone-ha.xml
COPY themes/assessment/ /opt/jboss/keycloak/themes/assessment
COPY themes/platform/ /opt/jboss/keycloak/themes/platform
COPY themes/les/ /opt/jboss/keycloak/themes/les
#COPY plugins/keycloak-spi-kafka/target/keycloak-spi-kafka.jar /opt/jboss/keycloak/standalone/deployments/
ENTRYPOINT ["/opt/jboss/tools/docker-entrypoint.sh"]

