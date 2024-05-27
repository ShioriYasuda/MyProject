package jp.eightbit.exam.controller;

import jp.eightbit.exam.entity.User;
import jp.eightbit.exam.repository.UserRepository;
import jp.eightbit.exam.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            logger.error("Validation errors occurred: " + result.getAllErrors());
            return "register";
        }
        String rawPassword = user.getPassword();
        String encodedPassword = new BCryptPasswordEncoder().encode(rawPassword);
        user.setPassword(encodedPassword);
        user.setRole("ROLE_USER");

        logger.info("Registering user: " + user.getUsername());

        try {
            userRepository.save(user);
            logger.info("User registered successfully: " + user.getUsername());
        } catch (Exception e) {
            logger.error("Error occurred while registering user: " + e.getMessage(), e);
            return "register";
        }

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
    // 既存の/dashboardマッピングを削除
    // @GetMapping("/dashboard")
    // public String showDashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
    //     model.addAttribute("username", userDetails.getUsername());
    //     return "dashboard";
    // }

    @PostMapping("/deleteAccount")
    public ResponseEntity<String> deleteAccount(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        if (user != null) {
            userService.deleteUser(user);
            return ResponseEntity.ok("Account deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }
    
}
