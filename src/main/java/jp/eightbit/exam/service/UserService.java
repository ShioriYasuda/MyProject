package jp.eightbit.exam.service;

import jp.eightbit.exam.entity.User;
import jp.eightbit.exam.repository.UserProfileRepository;
import jp.eightbit.exam.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserProfileRepository userProfileRepository;

    public void save(User user) {
        String rawPassword = user.getPassword();
        String encodedPassword = new BCryptPasswordEncoder().encode(rawPassword);
        user.setPassword(encodedPassword);
        user.setRole("ROLE_USER");
        userRepository.save(user);
    }
    
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Transactional
    public void deleteUser(User user) {
        // 関連データの削除
        userProfileRepository.deleteByUserId(user.getId());
        userRepository.delete(user);
    }
}
