package jp.eightbit.exam.service;

import jp.eightbit.exam.entity.User;
import jp.eightbit.exam.entity.WeightRecord;
import jp.eightbit.exam.repository.UserRepository;
import jp.eightbit.exam.repository.WeightRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class WeightRecordService {

    @Autowired
    private WeightRecordRepository weightRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void save(WeightRecord weightRecord) {
        weightRecordRepository.save(weightRecord);
    }

    public WeightRecord findById(Long id) {
        return weightRecordRepository.findById(id).orElse(null);
    }

    public WeightRecord findByUserAndDate(User user, LocalDate date) {
        return weightRecordRepository.findByUserAndDate(user, date);
    }

    public List<WeightRecord> getWeightRecords(Long userId, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ArrayList<>();
        }

        List<WeightRecord> records = weightRecordRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
        List<WeightRecord> completeRecords = new ArrayList<>();

        LocalDate[] currentDate = {startDate};
        Float lastWeight = null;

        while (!currentDate[0].isAfter(endDate)) {
            WeightRecord record = records.stream()
                    .filter(r -> r.getDate().equals(currentDate[0]))
                    .findFirst()
                    .orElse(null);

            if (record != null) {
                lastWeight = record.getWeight();
                completeRecords.add(record);
            } else if (lastWeight != null) {
                completeRecords.add(new WeightRecord(user, currentDate[0], lastWeight));
            }

            currentDate[0] = currentDate[0].plusDays(1);
        }

        return completeRecords;
    }
    
    
}
