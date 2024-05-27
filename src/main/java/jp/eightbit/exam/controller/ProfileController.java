package jp.eightbit.exam.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import jp.eightbit.exam.entity.User;
import jp.eightbit.exam.entity.UserProfile;
import jp.eightbit.exam.service.UserProfileService;
import jp.eightbit.exam.service.UserService;

@Controller
public class ProfileController {
    
    @Autowired
    private UserProfileService userProfileService;
    
    @Autowired
    private UserService userService;
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
    
    @GetMapping("/profile")
    public ModelAndView showProfile() {
        UserProfile userProfile = userProfileService.getCurrentUserProfile();
        return new ModelAndView("profile", "userProfile", userProfile);
    }
    
    // プロフィール編集ページへのアクセス
    @GetMapping("/editProfile")
    public String editProfile() {
        return "editProfile";
    }
    
    // プロフィール更新処理
    @PostMapping("/profile")
    public ModelAndView updateProfile(@ModelAttribute UserProfile userProfile, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        userProfile.setUser(user);
        userProfileService.updateProfile(userProfile);
        return new ModelAndView("redirect:/profile");
    }
    
    @GetMapping("/dashboard")
    public ModelAndView showDashboard(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        UserProfile userProfile = userProfileService.findByUserId(user.getId());

        Map<String, Object> model = new HashMap<>();
        model.put("username", user.getUsername());
        model.put("userProfile", userProfile);

        String defaultMessage = "今日も体重を記録してほしいぞう！";
        model.put("defaultMessage", defaultMessage);

        if (userProfileService.isUserBirthday(userProfile)) {
            model.put("birthdayMessage", user.getUsername() + "さん、お誕生日おめでとう！");
        }

        return new ModelAndView("dashboard", model);
    }

}
