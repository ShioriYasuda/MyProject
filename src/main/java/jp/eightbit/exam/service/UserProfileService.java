package jp.eightbit.exam.service;

import jp.eightbit.exam.entity.UserProfile;

public interface UserProfileService {
    UserProfile getUserProfileByUserId(Long userId);
    void save(UserProfile userProfile);
    UserProfile updateProfile(UserProfile userProfile);
    UserProfile findByUserId(Long userId);
    UserProfile getCurrentUserProfile();
    boolean isUserBirthday(UserProfile userProfile);
}
