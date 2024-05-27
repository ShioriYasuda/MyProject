package jp.eightbit.exam.controller;

import jp.eightbit.exam.entity.User;
import jp.eightbit.exam.entity.UserProfile;
import jp.eightbit.exam.entity.WeightRecord;
import jp.eightbit.exam.service.UserProfileService;
import jp.eightbit.exam.service.WeightRecordService;
import jp.eightbit.exam.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class WeightHistoryController {

    @Autowired
    private WeightRecordService weightRecordService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/api/currentUserId")
    @ResponseBody
    public Long getCurrentUserId(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername());
        return user.getId();
    }

    @GetMapping("/api/weightHistory")
    @ResponseBody
    public List<WeightRecord> getWeightHistory(@RequestParam("userId") Long userId, 
                                               @RequestParam("startDate") String startDate, 
                                               @RequestParam("endDate") String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return weightRecordService.getWeightRecords(userId, start, end);
    }

    @GetMapping("/api/userGoalWeight")
    @ResponseBody
    public Map<String, Float> getUserGoalWeight(@RequestParam("userId") Long userId) {
        UserProfile userProfile = userProfileService.getUserProfileByUserId(userId);
        Map<String, Float> response = new HashMap<>();
        response.put("goalWeight", (float) userProfile.getGoalWeight());
        return response;
    }
}
