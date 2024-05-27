package jp.eightbit.exam.repository;

import jp.eightbit.exam.entity.User;
import jp.eightbit.exam.entity.WeightRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WeightRecordRepository extends JpaRepository<WeightRecord, Long> {
    List<WeightRecord> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    WeightRecord findByUserAndDate(User user, LocalDate date);


}
