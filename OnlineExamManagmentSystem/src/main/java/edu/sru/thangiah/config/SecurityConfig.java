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

                                .requestMatchers("/course/add-course").hasAuthority("ADMINISTRATOR")
                              //  .requestMatchers("/profile").authenticated()
                                .requestMatchers("/add-course.html",
                                        "/associate-instructor.html",
                                        "/associate-students.html",
                                        "/classes.html",
                                        "/create-instructor.html",
                                        "/create-student.html",
                                        "/exams.html",
                                        "/history-quiz.html",
                                        "/import.html",
                                        "/import-students.html",
                                        "/index.html",
                                        "/math-quiz.html",
                                        "/navbar.html",
                                        "/register.html",
                                        "/registration_success.html",
                                        "/registration-confirmation.html",
                                        "/science-quiz.html",
                                        "/sidebar.html",
                                        "/student-list.html",
                                        "/upload-success.html",
                                        "/users.html").hasAnyAuthority("ADMINISTRATOR", "STUDENT", "INSTRUCTOR", "SCHEDULE_MANAGER")
                                .anyRequest().authenticated()
                )
                .formLogin(login -> login
                                .defaultSuccessUrl("/navbar", true) // the second parameter ensures always redirecting to "/navbar" after login
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