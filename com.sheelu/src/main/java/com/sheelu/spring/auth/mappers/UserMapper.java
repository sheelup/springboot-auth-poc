package com.sheelu.spring.auth.mappers;

import com.sheelu.spring.auth.models.User;
import com.sheelu.spring.auth.controllers.dtos.request.UserSignupRequest;
import com.sheelu.spring.auth.controllers.dtos.response.UserProfile;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public void userSignupRequestToUserEntity(UserSignupRequest userSignupRequest, User userEntity) {
        userEntity.setUserName(userSignupRequest.getUserName());
        userEntity.setFirstName(userSignupRequest.getFirstName());
        userEntity.setLastName(userSignupRequest.getLastName());
        userEntity.setPassword(userSignupRequest.getPassword());
    }

    public void UserEntityToUserProfile(User user, UserProfile userProfile) {
        userProfile.setFirstName(user.getFirstName());
        userProfile.setLastName(user.getLastName());
        userProfile.setUserName(user.getUserName());
    }
}
