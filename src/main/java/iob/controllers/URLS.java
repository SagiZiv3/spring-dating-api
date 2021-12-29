package iob.controllers;

public interface URLS {
    interface INSTANCES {
        String ROOT = "/iob/instances";
        String GET = "/{userDomain}/{userEmail}/{instanceDomain}/{instanceId}";
        String GET_ALL = "/{userDomain}/{userEmail}";
        String GET_CHILDREN = "/{userDomain}/{userEmail}/{instanceDomain}/{instanceId}/children";
        String GET_PARENTS = "/{userDomain}/{userEmail}/{instanceDomain}/{instanceId}/parents";
        String CREATE = "/{userDomain}/{userEmail}";
        String UPDATE = "/{userDomain}/{userEmail}/{instanceDomain}/{instanceId}";
        String BIND = "/{userDomain}/{userEmail}/{instanceDomain}/{instanceId}/children";
        String FIND_BY_NAME = "/{userDomain}/{userEmail}/search/byName/{name}";
        String FIND_BY_TYPE = "/{userDomain}/{userEmail}/search/byType/{type}";
        String FIND_BY_LOCATION = "/{userDomain}/{userEmail}/search/near/{lat}/{lng}/{distance}";
        String FIND_BY_DATE = "/{userDomain}/{userEmail}/search/created/{creationWindow}";
    }

    interface USERS {
        String ROOT = "/iob/users";
        String CREATE = "";
        String UPDATE = "/{userDomain}/{userEmail}";
        String LOGIN = "/login/{userDomain}/{userEmail}";
    }

    interface ACTIVITIES {
        String ROOT = "/iob/activities";
        String INVOKE = "";
    }

    interface ADMIN {
        String ROOT = "/iob/admin";
        String EXPORT_USERS = "/users/{userDomain}/{userEmail}";
        String EXPORT_ACTIVITIES = "/activities/{userDomain}/{userEmail}";
        String DELETE_USERS = "/users/{userDomain}/{userEmail}";
        String DELETE_INSTANCES = "/instances/{userDomain}/{userEmail}";
        String DELETE_ACTIVITIES = "/activities/{userDomain}/{userEmail}";
    }
}