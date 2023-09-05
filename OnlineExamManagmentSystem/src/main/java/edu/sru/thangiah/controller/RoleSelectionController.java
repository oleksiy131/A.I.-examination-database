
package edu.sru.thangiah.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RoleSelectionController {

    @PostMapping("/select-role")
    public String selectRole(@RequestParam("role") String selectedRole, RedirectAttributes redirectAttributes) {
        // Redirect users based on their selected role
        if ("student".equals(selectedRole)) {
            return "redirect:/studentlogin"; // Redirect Student to login page
        } else if ("instructor".equals(selectedRole)) {
            return "redirect:/instructorlogin"; // Redirect Instructor to login page
        } else if ("admin".equals(selectedRole)) {
            return "redirect:/administratorlogin"; // Redirect Administrator to login page
        } else {
            // Handle invalid or unrecognized roles here (e.g., show an error message)
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid role selected");
            return "redirect:/"; // Redirect back to the index page
        }
    }
}
