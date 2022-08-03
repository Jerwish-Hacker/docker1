package com.identity.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.identity.domain.IdentityEvent;
import com.identity.domain.IdentityEvent.ChangeLog;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.events.Event;
import org.keycloak.events.admin.AdminEvent;

import java.sql.Date;
import java.util.Map;
import java.util.UUID;

import static com.identity.domain.IdentityEvent.*;
import static com.identity.domain.IdentityEvent.Action.*;

@Slf4j
public class UserResourceService {

    private static final String USER_ID = "uuid";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String EMAIL ="email";
    private static final String REPRESENTATION ="representation";

    private ObjectMapper objectMapper = new ObjectMapper();

    public IdentityEvent register (Event event){
        logEvent(event);
        return new IdentityEvent();
    }

    public IdentityEvent register (AdminEvent event) throws MalformedUserException {
        logEvent(event);
        IdentityEvent iEvent = new IdentityEvent();
        iEvent.setType(Resource.USER);
        iEvent.setPath(event.getResourcePath());
        setActionType(iEvent, event);

        //Change Log
        ChangeLog changeLog = new ChangeLog();
        changeLog.setOriginUserId(UUID.fromString(
                event.getAuthDetails().getUserId()
        ));
        changeLog.setOriginDateTime(new Date(event.getTime()));
        changeLog.setOriginIpAddress(event.getAuthDetails().getIpAddress());
        changeLog.setOriginSourceRealm(event.getRealmId());

        //Data
        setDataBasedOnActionType(iEvent, event);
        log.info("Inside Resource service {}", iEvent);
        return iEvent;
    }

    private void setDataBasedOnActionType(IdentityEvent iEvent, AdminEvent event) throws MalformedUserException {

        switch (iEvent.getAction()){
            case CREATE:
                setAdminData4CreateOrUpdate(iEvent, event); break;
            case UPDATE:
                setAdminData4CreateOrUpdate(iEvent, event); break;
            case DELETE:
                setAdminData4Delete(iEvent, event); break;
            default:
                log.error("Un Supported type, Needs further examine {}", iEvent.getAction());
        }
    }

    private void setAdminData4Delete(IdentityEvent iEvent, AdminEvent event) throws MalformedUserException {
        Map<String, String> data = iEvent.getData();
        data.put(USER_ID, extractUUID(event.getResourcePath()));
    }
    private void setAdminData4CreateOrUpdate(IdentityEvent iEvent, AdminEvent event) throws MalformedUserException {
        Map<String, String> data = iEvent.getData();
        data.put(USER_ID, extractUUID(event.getResourcePath()));
        data.put(REPRESENTATION, event.getRepresentation());
    }

    private void setActionType(IdentityEvent iEvent, AdminEvent event) {
        switch (event.getOperationType().toString()){
            case "CREATE":
                iEvent.setAction(CREATE);
                break;
            case "UPDATE":
                iEvent.setAction(UPDATE);
                break;
            case "DELETE":
                iEvent.setAction(DELETE);
                break;
            default:
                log.error("Un Supported Event {}", event.getRepresentation());
        }
    }

    private void logEvent(Event event) {
        try {
            log.info("Incoming Event :: {}", objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.error("Suppressing the above error and moving forward");
        }
    }
    private void logEvent(AdminEvent event) {
        try {
            log.info("Incoming Event :: {}", objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.error("Suppressing the above ADMIN error and moving forward");
        }
    }

    public String extractUUID(String resourcePath) throws MalformedUserException {
        if(null == resourcePath || resourcePath.isEmpty()) throw new MalformedUserException("No Incoming ResourcePath");
        String[] delimitedPath = resourcePath.split("/");
        String uuidAsString = delimitedPath[delimitedPath.length - 1];
        if(uuidAsString.isEmpty()) throw new MalformedUserException(resourcePath);
        return uuidAsString;
    }
}
