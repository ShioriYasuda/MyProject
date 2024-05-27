package jp.eightbit.exam.service;

import jp.eightbit.exam.entity.User;
import jp.eightbit.exam.entity.UserProfile;
import jp.eightbit.exam.repository.UserProfileRepository;
import jp.eightbit.exam.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Override
    public void save(UserProfile userProfile) {
        userProfileRepository.save(userProfile);
    }

    @Transactional
    @Override
    public UserProfile updateProfile(UserProfile userProfile) {
        UserProfile existingProfile = userProfileRepository.findByUserId(userProfile.getUser().getId());
        if (existingProfile != null) {
            existingProfile.setBirthDate(userProfile.getBirthDate());
            existingProfile.setHeight(userProfile.getHeight());
            existingProfile.setGoalWeight(userProfile.getGoalWeight());
            return userProfileRepository.save(existingProfile);
        } else {
            return userProfileRepository.save(userProfile);
        }
    }

    @Override
    public UserProfile findByUserId(Long userId) {
        return userProfileRepository.findByUserId(userId);
    }

    @Override
    public UserProfile getUserProfileByUserId(Long userId) {
        return userProfileRepository.findByUserId(userId);
    }

    @Override
    public UserProfile getCurrentUserProfile() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        User user = userRepository.findByUsername(username);
        return userProfileRepository.findByUserId(user.getId());
    }

    @Override
    public boolean isUserBirthday(UserProfile userProfile) {
        if (userProfile == null || userProfile.getBirthDate() == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        LocalDate birthDate = userProfile.getBirthDate();
        return birthDate.getMonth() == today.getMonth() && birthDate.getDayOfMonth() == today.getDayOfMonth();
    }
}
