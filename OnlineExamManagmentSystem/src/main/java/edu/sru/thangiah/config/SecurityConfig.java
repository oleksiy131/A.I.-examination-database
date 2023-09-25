package edu.sru.thangiah.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired 	
	private UserDetailsService userDetailsService;
	
	private PasswordEncoder passwordEncoder;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}
	
	protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/index").permitAll()
                        .anyRequest().authenticated())
                .formLogin(login -> login
                        .loginPage("/")
                        .defaultSuccessUrl("/navbar")
                        .permitAll());
    }
	
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
	    auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
	}



}
