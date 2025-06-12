package controller;

import com.example.demo.MailTokunService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class NewMail {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MailTokunService mailTokunService;

    @GetMapping("/newMail")
    public String newMail() {
        return "newAccountMail";
    }

    @PostMapping("/newMail")
    public String sendTokenMail(HttpSession session,@RequestParam("email") String email, Model model) {
        try {
            // トークンを生成してデータベースに保存
            String token = mailTokunService.generateAndSaveToken(email);

            // アカウント登録URL
            String registrationUrl = "http://localhost:8080/register?token=" + token;

            // メールの送信
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("アカウント登録のご案内");
            message.setText("以下のURLからアカウント登録を行ってください。\n" + registrationUrl);

            mailSender.send(message);
            
            // メールアドレスをセッションに保存
            session.setAttribute("email", email);

            // 案内文を画面に表示
            model.addAttribute("message", "メールを送信しました。メールをご確認ください。");
        } catch (Exception e) {
            // エラーハンドリング
            model.addAttribute("message", "エラーが発生しました: " + e.getMessage());
        }
        return "newAccountMail";
    }
}
