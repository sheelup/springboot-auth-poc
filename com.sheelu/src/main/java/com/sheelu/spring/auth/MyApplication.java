package com.sheelu.spring.auth;

import com.sheelu.spring.auth.service.RoleService;
import com.sheelu.spring.auth.service.UserService;
import com.sheelu.spring.auth.controllers.dtos.request.RoleDTO;
import com.sheelu.spring.auth.controllers.dtos.request.UserSignupRequest;
import com.sheelu.spring.auth.exceptions.EntityAlreadyExistException;
import com.sheelu.spring.auth.models.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Arrays;

@SpringBootApplication
@EnableJpaAuditing
public class MyApplication implements ApplicationRunner {

	private static final Logger logger = LoggerFactory.getLogger(MyApplication.class);

	private final RoleService roleService;
	private final UserService userService;

	@Value("${app.roles}")
	private String roles;

	@Value("${app.adminUserName}")
	private String adminUserName;

	@Value("${app.adminPassword}")
	private String adminPassword;

	@Autowired
	public MyApplication(RoleService roleService, UserService userService) {
		this.roleService = roleService;
		this.userService = userService;
	}

	public static void main(String[] args) {
		SpringApplication.run(MyApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		this.createRoleIfNotExist();
		this.createAdminUserIfNotExist();
	}

	private void createAdminUserIfNotExist() {
		try {
			userService.createAdminUser(
					UserSignupRequest.builder()
							.userName(adminUserName)
							.password(adminPassword)
							.firstName("xxxx")
							.lastName("xxxx").build());
		} catch (EntityAlreadyExistException e) {
			logger.info("Admin user already created");
		}
		logger.info("admin user info, email: {}, password:{}", adminUserName, adminPassword);
	}

	private void createRoleIfNotExist() {
		Arrays.stream(roles.split(","))
				.map(String::trim)
				.map(role -> new RoleDTO(UserRole.valueOf(role)))
				.forEach(roleDTO -> {
					try {
						roleService.createNewRole(roleDTO);
					} catch (EntityAlreadyExistException e) {
						logger.info("Roles already created in database");
					} catch (Exception exception) {
						throw new RuntimeException(exception);
					}
				});
	}
}
