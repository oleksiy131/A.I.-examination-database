package edu.sru.thangiah.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private CustomAuthenticationSuccessHandler successHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected DefaultSecurityFilterChain configure(HttpSecurity http) throws Exception {
        System.out.println("SecurityConfig is loaded");

        http
                //Disabling CSRF is not recommended for production but it is making the whole program very angry.
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((authorize) -> authorize
                				.requestMatchers("/student_homepage/**").hasAuthority("STUDENT")
                                .anyRequest().authenticated()
                )
                .formLogin(login -> login
                                .successHandler(successHandler) // the second parameter ensures always redirecting to "/navbar" after login
                                .permitAll()
                )
                .logout(logout -> logout // Configure logout
                                .logoutUrl("/logout") // Set the URL for logout
                                .logoutSuccessUrl("/login?logout") // Redirect to this URL after successful logout
                );
        return http.build();
    }


    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }
}