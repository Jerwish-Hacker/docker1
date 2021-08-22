FROM jboss/keycloak:14.0.0

USER root

WORKDIR /

ADD /config/standalone.xml /opt/jboss/keycloak/standalone/configuration/standalone.xml
COPY themes/assessment/ /opt/jboss/keycloak/themes/assessment
ENTRYPOINT ["/opt/jboss/tools/docker-entrypoint.sh"]

