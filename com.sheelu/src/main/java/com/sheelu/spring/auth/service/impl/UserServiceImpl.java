package com.sheelu.spring.auth.service.impl;

import com.sheelu.spring.auth.dao.RoleRepository;
import com.sheelu.spring.auth.dao.UserRepository;
import com.sheelu.spring.auth.models.Role;
import com.sheelu.spring.auth.models.User;
import com.sheelu.spring.auth.models.UserRole;
import com.sheelu.spring.auth.util.UUIDGeneratorUtils;
import com.sheelu.spring.auth.controllers.dtos.request.UserSignupRequest;
import com.sheelu.spring.auth.controllers.dtos.request.UserUpdateRequestDTO;
import com.sheelu.spring.auth.controllers.dtos.response.UserProfile;
import com.sheelu.spring.auth.exceptions.EntityAlreadyExistException;
import com.sheelu.spring.auth.exceptions.EntityNotFoundException;
import com.sheelu.spring.auth.mappers.UserMapper;
import com.sheelu.spring.auth.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UUIDGeneratorUtils uuidGeneratorUtils;

    @Autowired
    public UserServiceImpl(UserMapper userMapper,
                           BCryptPasswordEncoder bCryptPasswordEncoder,
                           UserRepository userRepository,
                           RoleRepository roleRepository,
                           UUIDGeneratorUtils uuidGeneratorUtils) {

        this.userMapper = userMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.uuidGeneratorUtils = uuidGeneratorUtils;
    }

    @Transactional
    public void createStandardUser(@RequestBody @Valid UserSignupRequest userSignupRequest) throws EntityAlreadyExistException {
        User user = createUserWithRole(userSignupRequest, UserRole.STANDARD);
    }

    @Override
    public void createAdminUser(UserSignupRequest userSignupRequest) throws EntityAlreadyExistException {
        createUserWithRole(userSignupRequest, UserRole.ADMIN);
    }

    @Override
    public UserProfile getUser(String userName) throws EntityNotFoundException {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        UserProfile userProfile = new UserProfile();
        userMapper.UserEntityToUserProfile(user, userProfile);
        return userProfile;
    }

    @Override
    public void updateUser(String userName, UserUpdateRequestDTO userUpdateRequestDTO) throws EntityNotFoundException {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (StringUtils.isNotBlank(userUpdateRequestDTO.getFirstName())) {
            user.setFirstName(userUpdateRequestDTO.getFirstName());
        }
        if (StringUtils.isNotBlank(userUpdateRequestDTO.getLastName())) {
            user.setLastName(userUpdateRequestDTO.getLastName());
        }
        userRepository.save(user);
    }

    @Override
    public void deleteUser(String userName) throws EntityNotFoundException {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        userRepository.delete(user);
    }

    @Override
    public List<UserProfile> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserProfile userProfile = new UserProfile();
                    userMapper.UserEntityToUserProfile(user, userProfile);
                    return userProfile;
                })
                .collect(Collectors.toList());
    }

    private User createUserWithRole(UserSignupRequest userSignupRequest, UserRole userRole) throws EntityAlreadyExistException {
        validateDuplicateUser(userSignupRequest.getUserName());
        User user = new User();
        userMapper.userSignupRequestToUserEntity(userSignupRequest, user);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setUserName(userSignupRequest.getUserName());
        Role role = roleRepository
                .findByName(userRole)
                .orElseThrow(() -> new RuntimeException(userRole + " does not exist"));
        user.setRoles(new HashSet<>(Arrays.asList(role)));
        userRepository.save(user);
        return user;
    }

    private void validateDuplicateUser(String emailId) throws EntityAlreadyExistException {
        Optional<User> user = userRepository.findByUserName(emailId);
        if (user.isPresent()) {
            throw new EntityAlreadyExistException("User already exist with given username");
        }
    }
}
