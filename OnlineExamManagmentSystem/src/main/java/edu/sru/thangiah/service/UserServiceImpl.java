package edu.sru.thangiah.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import edu.sru.thangiah.model.User;
import edu.sru.thangiah.repository.UserRepository;
import edu.sru.thangiah.web.dto.UserRegistrationDto;

@Service
public class UserServiceImpl implements UserService{
	
	@Autowired
	private UserRepository userRepository;
	
	
	public UserServiceImpl(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}
	
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
        	    user.getUsername(), 
        	    user.getPassword(), 
        	    Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name())));

    }

	@Override
    public User save(UserRegistrationDto registrationDto) {
        User user = new User(registrationDto.getId(), registrationDto.getFirstName(), registrationDto.getLastName(), 
        						registrationDto.getEmail(), registrationDto.getPassword(), registrationDto.getPassword(), registrationDto.getRole(), registrationDto.getVerificationCode());
		
		return userRepository.save(user);
	}

}
