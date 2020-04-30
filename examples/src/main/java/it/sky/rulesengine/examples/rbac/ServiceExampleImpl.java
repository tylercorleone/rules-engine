package it.sky.rulesengine.examples.rbac;

public class ServiceExampleImpl implements ServiceExample {

    @Override
    public UserProfile readUserProfile(String username) {
        return new UserProfile();
    }

    @Override
    public void createUserProfile(UserProfile profile) {
    }

    @Override
    public RestrictedResource readRestrictedResource() {
        return new RestrictedResource();
    }

}
