package jp.eightbit.exam.repository;

import jp.eightbit.exam.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    UserProfile findByUserId(Long userId);
    void deleteByUserId(Long userId);  // 追加
}
