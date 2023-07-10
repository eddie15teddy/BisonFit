package funkyflamingos.bisonfit.logic;

import android.content.Context;

import funkyflamingos.bisonfit.application.Services;
import funkyflamingos.bisonfit.persistence.IUserRegistrationPersistence;
import funkyflamingos.bisonfit.persistence.UserRegistrationPersistence;
import funkyflamingos.bisonfit.application.Constants;

public class UserRegistrationHandler implements IUserRegistrationHandler {

    private IUserRegistrationPersistence persistence;

    public UserRegistrationHandler(Context context) {
        persistence = new UserRegistrationPersistence(context);
    }

    public UserRegistrationHandler(){
        persistence = Services.getUserRegistrationPersistence();
    }

    public UserRegistrationHandler(IUserRegistrationPersistence persistence) {
        this.persistence = persistence;
    }


    @Override
    public boolean userHasRegistered() {
        return (persistence.getUserName() != null);
    }

    @Override
    public boolean setUserName(String userName) {
        userName = userName.trim();
        if(userNameValid(userName)) {
            persistence.setUserName(userName);
            return  true;
        }
        return false;
    }

    @Override
    public String getUserName() {
        return persistence.getUserName();
    }

    @Override
    public boolean userNameValid(String userName) {
        userName = userName.trim();
        return (userName.length() >= Constants.MIN_USERNAME_LENGTH
                && userName.length() <= Constants.MAX_USERNAME_LENGTH);
    }
}
