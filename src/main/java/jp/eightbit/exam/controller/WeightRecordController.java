package jp.eightbit.exam.controller;

import jp.eightbit.exam.entity.User;
import jp.eightbit.exam.entity.UserProfile;
import jp.eightbit.exam.entity.WeightRecord;
import jp.eightbit.exam.service.UserService;
import jp.eightbit.exam.service.WeightRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jp.eightbit.exam.repository.UserProfileRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
public class WeightRecordController {

    @Autowired
    private WeightRecordService weightRecordService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserProfileRepository userProfileRepository;

    //    目的: 直近1ヶ月の体重記録を表示するためのページを提供します。
  //  詳細:
    //    現在の認証済みユーザーのユーザー名を取得し、userService を使用して User エンティティを取得します。
      //  weightRecordService を使用して、過去1ヶ月の体重記録を取得し、model に追加します。
        //weightHistory テンプレートを返します。
    
    @GetMapping("/weightHistory")
    public String showWeightHistory(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        UserProfile userProfile = userProfileRepository.findByUserId(user.getId());
        LocalDate today = LocalDate.now();

        // 今日の体重記録を取得
        WeightRecord todayRecord = weightRecordService.findByUserAndDate(user, today);
        if (todayRecord != null) {
            double todayWeight = todayRecord.getWeight();
            double goalWeight = userProfile.getGoalWeight();
            double height = userProfile.getHeight();

            // 目標体重までの差を計算
            double weightDifference = Math.round((goalWeight - todayWeight) * 10) / 10.0;
            String goalMessage = "目標体重まであと" + weightDifference + "kg！";

            // BMIの計算
            double bmiValue = Math.round((todayWeight / ((height / 100) * (height / 100))) * 10) / 10.0;
            String bmiMessage = "あなたのBMIは" + bmiValue + "です。";
            if (bmiValue < 18.5) {
                bmiMessage += "あなたは低体重です。";
            } else if (bmiValue < 24.9) {
                bmiMessage += "あなたは標準体重です。";
            } else if (bmiValue < 29.9) {
                bmiMessage += "あなたは肥満1度です。";
            } else if (bmiValue < 34.9) {
                bmiMessage += "あなたは肥満2度です。";
            } else if (bmiValue < 39.9) {
                bmiMessage += "あなたは肥満3度です。";
            } else {
                bmiMessage += "あなたは肥満4度です。";
            }

            model.addAttribute("goalMessage", goalMessage);
            model.addAttribute("bmiMessage", bmiMessage);
        } else {
            model.addAttribute("goalMessage", "今日の体重記録がありません。");
            model.addAttribute("bmiMessage", "");
        }

        List<WeightRecord> weightRecords = weightRecordService.getWeightRecords(user.getId(), LocalDate.now().minusMonths(1), LocalDate.now());
        model.addAttribute("weightRecords", weightRecords);
        return "weightHistory";
    }
    
    
   // 目的: 体重を記録するためのフォームを表示します。
   // 詳細:
     //   現在の認証済みユーザーのユーザー名を取得し、userService を使用して User エンティティを取得します。
       // 新しい WeightRecord オブジェクトを作成し、モデルに追加します。
      //  過去1ヶ月の体重記録を取得し、モデルに追加します。
        //weight テンプレートを返します。
    
    @GetMapping("/weight")
    public String showWeightForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("weightRecord", new WeightRecord());
        model.addAttribute("weightRecords", weightRecordService.getWeightRecords(user.getId(), LocalDate.now().minusMonths(1), LocalDate.now()));
        return "weight";
    }

   //    目的: 体重を記録し、既存の記録がある場合は更新します。
  //  詳細:
     //   現在の認証済みユーザーを取得し、WeightRecord オブジェクトに設定します。
       // 重複する日付の記録をチェックし、存在する場合は更新し、存在しない場合は新規作成します。
        //処理が完了したら、weightHistory ページにリダイレクトします。
    
    @PostMapping("/weight")
    public String recordWeight(@ModelAttribute WeightRecord weightRecord, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        weightRecord.setUser(user);

        // 重複日付チェックと更新
        WeightRecord existingRecord = weightRecordService.findByUserAndDate(user, weightRecord.getDate());
        if (existingRecord != null) {
            existingRecord.setWeight(weightRecord.getWeight());
            weightRecordService.save(existingRecord);
        } else {
            weightRecordService.save(weightRecord);
        }

        return "redirect:/weightHistory";
    }
    
    //    目的: 体重記録を編集するためのフォームを表示します。
   // 詳細:
    //    パス変数から記録IDを取得し、weightRecordService を使用して WeightRecord を取得します。
     //   記録が存在しないか、ユーザーが一致しない場合は weightHistory ページにリダイレクトします。
       // 記録が有効な場合は、モデルに追加し、editWeight テンプレートを返します。

    @GetMapping("/weight/edit/{id}")
    public String showEditWeightForm(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        WeightRecord weightRecord = weightRecordService.findById(id);
        User user = userService.findByUsername(userDetails.getUsername());
        if (weightRecord == null || !weightRecord.getUser().getId().equals(user.getId())) {
            return "redirect:/weightHistory"; // If the record does not exist or does not belong to the user, redirect
        }
        model.addAttribute("weightRecord", weightRecord);
        return "editWeight";
    }
    
    //    目的: 体重記録を更新します。
   // 詳細:
     //   フォームから送信された WeightRecord オブジェクトを使用し、既存の記録を検索します。
       // 記録が存在しないか、ユーザーが一致しない場合は weightHistory ページにリダイレクトします。
       // 記録が有効な場合は、日付と体重を更新し、保存します。

    @PostMapping("/weight/edit")
    public String editWeight(@ModelAttribute WeightRecord weightRecord, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        WeightRecord existingRecord = weightRecordService.findById(weightRecord.getId());
        if (existingRecord == null || !existingRecord.getUser().getId().equals(user.getId())) {
            return "redirect:/weightHistory"; // If the record does not exist or does not belong to the user, redirect
        }
        existingRecord.setDate(weightRecord.getDate());
        existingRecord.setWeight(weightRecord.getWeight());
        weightRecordService.save(existingRecord);
        return "redirect:/weightHistory";
    }

    //    目的: 体重を記録し、API経由でのリクエストに応答します。
  //  詳細:
     //   リクエストボディから日付と体重を取得し、ユーザーの体重記録を更新または新規作成します。
       // 成功した場合は "success" を返します。
    
    @PostMapping("/api/addWeight")
    @ResponseBody
    public String addWeight(@RequestBody Map<String, Object> payload, @AuthenticationPrincipal UserDetails userDetails) {
        String date = (String) payload.get("date");
        float weight = Float.parseFloat(payload.get("weight").toString());

        User user = userService.findByUsername(userDetails.getUsername());
        WeightRecord existingRecord = weightRecordService.findByUserAndDate(user, LocalDate.parse(date));
        if (existingRecord != null) {
            existingRecord.setWeight(weight);
            weightRecordService.save(existingRecord);
        } else {
            WeightRecord weightRecord = new WeightRecord();
            weightRecord.setDate(LocalDate.parse(date));
            weightRecord.setWeight(weight);
            weightRecord.setUser(user);
            weightRecordService.save(weightRecord);
        }

        return "success";
    }
    
    //    目的: 指定された年と月の体重記録を検索し、表示します。
    //詳細:
      //  リクエストパラメータから年と月を取得し、指定された期間の開始日と終了日を計算します。
        //weightRecordService を使用して指定期間の体重記録を取得し、モデルに追加します。
        //weight テンプレートを返します。
    
    @GetMapping("/weight/search")
    public String searchWeightRecords(@RequestParam("year") int year, @RequestParam("month") int month, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        List<WeightRecord> weightRecords = weightRecordService.getWeightRecords(user.getId(), startDate, endDate);
        model.addAttribute("weightRecords", weightRecords);
        return "weight";
    }
    
    
}
