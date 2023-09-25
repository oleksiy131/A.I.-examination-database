package edu.sru.thangiah.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import edu.sru.thangiah.model.User;
import edu.sru.thangiah.web.dto.UserRegistrationDto;

@Service
public interface UserService extends UserDetailsService{
	User save(UserRegistrationDto registrationDto);
	UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

}
