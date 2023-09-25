package edu.sru.thangiah.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import edu.sru.thangiah.model.User;
import edu.sru.thangiah.repository.UserRepository;
import edu.sru.thangiah.web.dto.UserRegistrationDto;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

	/*
	 * public User registerNewUserAccount(UserRegistrationDto userDto) { // Create a
	 * User object from the registration data User user = new User( null,
	 * userDto.getFirstName(), userDto.getLastName(), userDto.getEmail(),
	 * userDto.getPassword(), userDto.getUsername(), "ROLE_USER", false, null );
	 * 
	 * // Save the user to the database return userRepository.save(user); }
	 */

	
	@Override
	public User save(UserRegistrationDto registrationDto) {
		User user = new User(null, registrationDto.getFirstName(), registrationDto.getLastName(),
				registrationDto.getEmail(), registrationDto.getPassword(), registrationDto.getUsername(),
				registrationDto.getVerificationCode(), false, null);

		return userRepository.save(user);

	}
	


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	    User user = userRepository.findByUsername(username).orElse(null);

	    if (user == null) {
	        throw new UsernameNotFoundException("User not found with username: " + username);
	    }

	    return user;
	}


}
