package edu.sru.thangiah.service;

import edu.sru.thangiah.model.User;
import edu.sru.thangiah.web.dto.UserRegistrationDto;

public interface UserService {
	User save(UserRegistrationDto registrationDto);

}
