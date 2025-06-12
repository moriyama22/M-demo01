package controller;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.MailTokunService;
import com.example.demo.entity.Account;
import com.example.demo.repository.*;
import com.example.demo.*;

@Controller
public class NewPsController {
	
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordResetService passwordResetService;
    
    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/newPsMail")
    public String newMail() {
        return "newPsMail";
    }
	
    @PostMapping("/sendResetEmail")
    public String sendResetEmail(@RequestParam("email") String email, Model model) {
        try {
        	Optional<Account> accountOpt = accountRepository.findByMail(email);
            if (accountOpt.isEmpty()) {
                model.addAttribute("isError", "そのメールアドレスは登録されていません。");
                return "newPsMail";
            }

            Account account = accountOpt.get();

            // トークンを生成
            String token = UUID.randomUUID().toString();
            passwordResetService.saveToken(account.getId(), token); // トークンをDBに保存

            // リセットURL
            String resetUrl = "http://localhost:8080/resetPassword?token=" + token;

            // メール送信
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(email);
            mailMessage.setSubject("パスワードリセットのご案内");
            mailMessage.setText("以下のリンクからパスワードをリセットしてください。\n" + resetUrl);
            mailSender.send(mailMessage);

            model.addAttribute("message", "リセット用メールを送信しました。メールをご確認ください。");
        } catch (Exception e) {
            model.addAttribute("isError", "エラーが発生しました: " + e.getMessage());
        }
        return "newPsMail";
    }
    

}
