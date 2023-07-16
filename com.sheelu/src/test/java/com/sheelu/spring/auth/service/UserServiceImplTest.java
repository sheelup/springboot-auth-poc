package com.sheelu.spring.auth.service;

import com.sheelu.spring.auth.dao.UserRepository;
import com.sheelu.spring.auth.mappers.UserMapper;
import com.sheelu.spring.auth.models.Role;
import com.sheelu.spring.auth.models.User;
import com.sheelu.spring.auth.models.UserRole;
import com.sheelu.spring.auth.service.impl.UserServiceImpl;
import com.sheelu.spring.auth.util.UUIDGeneratorUtils;
import com.sheelu.spring.auth.controllers.dtos.request.UserSignupRequest;
import com.sheelu.spring.auth.controllers.dtos.request.UserUpdateRequestDTO;
import com.sheelu.spring.auth.controllers.dtos.response.UserProfile;
import com.sheelu.spring.auth.dao.RoleRepository;
import com.sheelu.spring.auth.exceptions.EntityAlreadyExistException;
import com.sheelu.spring.auth.exceptions.EntityNotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class UserServiceImplTest {
    @InjectMocks
    UserServiceImpl userServiceImpl;

    @Spy
    private UserMapper userMapper;

    @Spy
    private UUIDGeneratorUtils uuidGeneratorUtils;

    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void TestCreateStandardUser() {
        //Case1: Add Duplicate User
        UserSignupRequest userSignupRequest = UserSignupRequest.builder()
                .firstName("Sheela")
                .lastName("Purbiya")
                .userName("sheela@gmail.com")
                .password("somepassword")
                .build();
        Mockito.when(userRepository.findByUserName(Mockito.eq("sheela@gmail.com"))).thenReturn(Optional.of(new User()));
        Assertions.assertThrows(EntityAlreadyExistException.class, () -> {
            userServiceImpl.createStandardUser(userSignupRequest);
        });

        //Case2: Add User With Valid Role but same does not exist in DB
        Mockito.when(roleRepository.findByName(UserRole.STANDARD)).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByUserName(Mockito.eq("sheela@gmail.com"))).thenReturn(Optional.empty());
        Assertions.assertThrows(RuntimeException.class, () -> {
            userServiceImpl.createStandardUser(userSignupRequest);
        });
    }

    @Test
    public void TestCreateAdminUser() throws EntityAlreadyExistException {
        //Case1: Add Duplicate User
        UserSignupRequest userSignupRequest = UserSignupRequest.builder()
                .firstName("Admin")
                .lastName("Sir")
                .userName("admin@gmail.com")
                .password("somepassword")
                .build();
        Mockito.when(userRepository.findByUserName(Mockito.eq("admin@gmail.com"))).thenReturn(Optional.of(new User()));
        Assertions.assertThrows(EntityAlreadyExistException.class, () -> {
            userServiceImpl.createAdminUser(userSignupRequest);
        });

        //Case2: Admin Use should get created successfully
        Mockito.when(roleRepository.findByName(UserRole.ADMIN)).thenReturn(Optional.of(new Role(1, UserRole.ADMIN)));
        Mockito.when(userRepository.findByUserName(Mockito.eq("admin@gmail.com"))).thenReturn(Optional.empty());
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(new User());
        userServiceImpl.createAdminUser(userSignupRequest);
    }

    @Test
    public void TestGetUser() throws EntityNotFoundException {
        //Case1: No User found for given userName
        String userName = "hello123";
        Mockito.when(userRepository.findByUserName(Mockito.eq(userName))).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            userServiceImpl.getUser(userName);
        });

        //Case2: Get User successfully
        Mockito.when(userRepository.findByUserName(Mockito.eq(userName))).thenReturn(Optional.of(
                User.builder()
                        .id(100L).userName("qwerty").firstName("user").lastName("lastName").password("password").build()
        ));

        UserProfile expectedUser = UserProfile.builder()
                .userName("qwerty").firstName("user").lastName("lastName")
                .build();
        UserProfile actualUser = userServiceImpl.getUser(userName);
        Assert.assertEquals(expectedUser, actualUser);
    }

    @Test
    public void TestUpdateUser() throws EntityNotFoundException {
        String userName = "userName";
        User expectFromDao = User.builder().id(100L).firstName("f1").lastName("f2").userName(userName).build();

        //Case1: Update First Name and Last Name both
        UserUpdateRequestDTO updateRequestDTO = UserUpdateRequestDTO.builder().firstName("User").lastName("Sir").build();
        Mockito.when(userRepository.findByUserName(userName)).thenReturn(Optional.of(expectFromDao));
        User daoShouldExpectAfterModification =
                User.builder().id(100L).firstName("User").lastName("Sir").userName(userName).build();
        Mockito.when(userRepository.save(daoShouldExpectAfterModification)).thenReturn(new User());
        userServiceImpl.updateUser(userName, updateRequestDTO);

        //Case2: Update First Name only
        updateRequestDTO = UserUpdateRequestDTO.builder().firstName("User").build();
        Mockito.when(userRepository.findByUserName(userName)).thenReturn(Optional.of(expectFromDao));
        daoShouldExpectAfterModification =
                User.builder().id(100L).firstName("User").lastName("f2").userName(userName).build();
        Mockito.when(userRepository.save(daoShouldExpectAfterModification)).thenReturn(new User());
        userServiceImpl.updateUser(userName, updateRequestDTO);

        //Case3: Update lastName only
        updateRequestDTO = UserUpdateRequestDTO.builder().lastName("Sir").build();
        Mockito.when(userRepository.findByUserName(userName)).thenReturn(Optional.of(expectFromDao));
        daoShouldExpectAfterModification =
                User.builder().id(100L).firstName("f1").lastName("Sir").userName(userName).build();
        Mockito.when(userRepository.save(daoShouldExpectAfterModification)).thenReturn(new User());
        userServiceImpl.updateUser(userName, updateRequestDTO);

        //Case4: User does not exist
        Mockito.when(userRepository.findByUserName(userName)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            userServiceImpl.updateUser(userName, UserUpdateRequestDTO.builder().firstName("User").lastName("Sir").build());
        });
    }

    @Test
    public void testDeleteUser() {
        String userName = "userName";
        User expectFromDao = User.builder().id(100L).firstName("f1").lastName("f2").userName(userName).build();
        //Case1: Delete successfully
        Mockito.when(userRepository.findByUserName(userName)).thenReturn(Optional.of(expectFromDao));
        Mockito.doNothing().when(userRepository).delete(expectFromDao);
        Assertions.assertDoesNotThrow(() -> userServiceImpl.deleteUser(userName));

        //Case2: Delete user which doesn't exist
        Mockito.when(userRepository.findByUserName(userName)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            userServiceImpl.deleteUser(userName);
        });
    }

    @Test
    public void testGetAllUsers() {
        List<User> allUsersInDB = Arrays.asList(User.builder().id(100L).firstName("f1").lastName("l1").userName("u1").build(),
                User.builder().id(200L).firstName("f2").lastName("l2").userName("u2").build(),
                User.builder().id(300L).firstName("f3").lastName("l3").userName("u3").build(),
                User.builder().id(400L).firstName("f4").lastName("l4").userName("u4").build());

        Mockito.when(userRepository.findAll()).thenReturn(allUsersInDB);
        List<UserProfile>  users = userServiceImpl.getAllUsers();
        Assertions.assertEquals(4, users.size());
        List<UserProfile> sortedUserList = users.stream().sorted(Comparator.comparing(u -> u.getUserName())).collect(Collectors.toList());
        for (int i = 0; i<4; i++){
            Assertions.assertEquals(allUsersInDB.get(i).getUserName(), sortedUserList.get(i).getUserName());
            Assertions.assertEquals(allUsersInDB.get(i).getFirstName(), sortedUserList.get(i).getFirstName());
            Assertions.assertEquals(allUsersInDB.get(i).getLastName(), sortedUserList.get(i).getLastName());
        }
    }
}
