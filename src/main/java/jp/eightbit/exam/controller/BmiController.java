package jp.eightbit.exam.controller;

import jp.eightbit.exam.service.BMI;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BmiController {

    @GetMapping("/bmi")
    public String showBmiForm() {
        return "bmi";
    }

    @PostMapping("/bmi")
    public String calculateBmi(@RequestParam("weight") double weight,
                               @RequestParam("height") double height,
                               Model model) {
        double bmiValue = BMI.calculateBMI(weight, height);
        String formattedBmi = String.format("%.1f", bmiValue); // 小数点以下一桁にフォーマット
        String message;
        if (bmiValue < 18.5) {
            message = "あなたは低体重です";
        } else if (bmiValue < 24.9) {
            message = "あなたは標準体重です";
        } else if (bmiValue < 29.9) {
            message = "あなたは肥満1度です";
        } else if (bmiValue < 34.9) {
            message = "あなたは肥満2度です";
        } else if (bmiValue < 39.9) {
            message = "あなたは肥満3度です";
        } else {
            message = "あなたは肥満4度です";
        }

        model.addAttribute("bmi", formattedBmi);
        model.addAttribute("message", message);

        return "bmiResult";
    }
}
