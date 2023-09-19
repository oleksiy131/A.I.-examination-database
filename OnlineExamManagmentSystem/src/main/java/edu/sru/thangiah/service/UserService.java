package edu.sru.thangiah.service;

import edu.sru.thangiah.model.User;
import edu.sru.thangiah.web.dto.UserRegistrationDto;

public interface UserService {
    static User registerNewUserAccount(UserRegistrationDto userDto) {
		// TODO Auto-generated method stub
		return null;
	}

	void save(UserRegistrationDto registrationDto);
}
