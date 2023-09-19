package edu.sru.thangiah.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.sru.thangiah.model.User;
import edu.sru.thangiah.repository.UserRepository;
import edu.sru.thangiah.web.dto.UserRegistrationDto;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    public User registerNewUserAccount(UserRegistrationDto userDto) {
        // Create a User object from the registration data
        User user = new User(
            userDto.getFirstName(),
            userDto.getLastName(),
            userDto.getEmail(),
            userDto.getPassword(),
            userDto.getUsername(),
            "ROLE_USER"
        );

        // Save the user to the database
        return userRepository.save(user);
    }

	@Override
	public void save(UserRegistrationDto registrationDto) {
		// TODO Auto-generated method stub
		
	}
}
