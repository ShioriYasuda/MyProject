package jp.eightbit.exam.controller;

import jp.eightbit.exam.entity.User;
import jp.eightbit.exam.entity.WeightRecord;
import jp.eightbit.exam.service.UserService;
import jp.eightbit.exam.service.WeightRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
public class WeightRecordController {

    @Autowired
    private WeightRecordService weightRecordService;

    @Autowired
    private UserService userService;

    @GetMapping("/weightHistory")
    public String showWeightHistory(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        List<WeightRecord> weightRecords = weightRecordService.getWeightRecords(user.getId(), LocalDate.now().minusMonths(1), LocalDate.now());
        model.addAttribute("weightRecords", weightRecords);
        return "weightHistory";
    }

    @GetMapping("/weight")
    public String showWeightForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("weightRecord", new WeightRecord());
        model.addAttribute("weightRecords", weightRecordService.getWeightRecords(user.getId(), LocalDate.now().minusMonths(1), LocalDate.now()));
        return "weight";
    }

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
