package edu.sru.thangiah.controller;

import edu.sru.thangiah.model.User;
import edu.sru.thangiah.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ConfirmationController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/confirm")
    public String confirmRegistration(@RequestParam("token") String token, Model model) {
        // Find the user by the verification code (token)
        User user = userRepository.findByVerificationCode(token);

        if (user != null) {
            // Mark the user as verified (update their status in the database)
            user.setVerified(true);
            userRepository.save(user);

            // Redirect to a confirmation success page
            return "confirmation_success";
        } else {
            // Redirect to a confirmation failure page
            return "confirmation_failure";
        }
    }
}
