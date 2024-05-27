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

//  目的: 現在の認証済みユーザーのIDを取得します。
  //詳細:
    //  @AuthenticationPrincipal アノテーションを使用して、現在のユーザーの詳細情報を取得します。
      //ユーザー名を使用して UserRepository から User エンティティを取得し、そのIDを返します。
    
    @GetMapping("/api/currentUserId")
    @ResponseBody
    public Long getCurrentUserId(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername());
        return user.getId();
    }

//  目的: 指定されたユーザーIDと期間の体重記録を取得します。
 // 詳細:
   //   @RequestParam アノテーションを使用して、HTTPリクエストからパラメータを取得します。
     // startDate と endDate を LocalDate に変換し、weightRecordService を使用して指定期間の体重記録を取得します。
    
    @GetMapping("/api/weightHistory")
    @ResponseBody
    public List<WeightRecord> getWeightHistory(@RequestParam("userId") Long userId, 
                                               @RequestParam("startDate") String startDate, 
                                               @RequestParam("endDate") String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return weightRecordService.getWeightRecords(userId, start, end);
    }
    
    //    目的: 指定されたユーザーIDの目標体重を取得します。
    // 詳細:
      //   @RequestParam アノテーションを使用して、HTTPリクエストからユーザーIDを取得します。
        // userProfileService を使用してユーザープロファイルを取得し、その目標体重を返します。
     

    @GetMapping("/api/userGoalWeight")
    @ResponseBody
    public Map<String, Float> getUserGoalWeight(@RequestParam("userId") Long userId) {
        UserProfile userProfile = userProfileService.getUserProfileByUserId(userId);
        Map<String, Float> response = new HashMap<>();
        response.put("goalWeight", (float) userProfile.getGoalWeight());
        return response;
    }
}
