package it.sky.rulesengine.examples.rbac;

public interface ServiceExample {

    UserProfile readUserProfile(String username);

    void createUserProfile(UserProfile profile);

    RestrictedResource readRestrictedResource();

    class UserProfile {
    }

    class RestrictedResource {
    }
}
