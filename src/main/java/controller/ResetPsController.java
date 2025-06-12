package controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.PasswordResetService;
import com.example.demo.entity.Account;
import com.example.demo.repository.AccountRepository;

@Controller
public class ResetPsController {
    
    @Autowired
    private PasswordResetService passwordResetService;
    
    @Autowired
    private AccountRepository accountRepository;
    
    // パスワードリセットフォームを表示
    @GetMapping("/resetPassword")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        // トークンの有効性をチェック
        if (!passwordResetService.validateToken(token)) {
            System.out.println("Invalid or expired token: " + token);
            model.addAttribute("error", "無効なまたは期限切れのトークンです。");
            return "errorPage";
        }

        // トークンからアカウントを取得
        Optional<Account> accountOpt = passwordResetService.findAccountByToken(token);
        if (accountOpt.isEmpty()) {
            System.out.println("No account found for token: " + token);
            model.addAttribute("error", "アカウントが見つかりません。");
            return "errorPage";
        }

        Account account = accountOpt.get();
        System.out.println("Valid token for accountId: " + account.getId());
        
        // モデルにトークンを追加
        model.addAttribute("token", token);
        return "newPs"; // newPs.html をレンダリング
    }
    
    // パスワードリセット処理
    @PostMapping("/resetPassword")
    public String resetPassword(
            @RequestParam("token") String token,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {
        // パスワード一致チェック
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "パスワードが一致しません。");
            System.out.println("パスワードが一致しません。");
            return "newPs";
        }

        // トークンの有効性を再チェック
        if (!passwordResetService.validateToken(token)) {
            model.addAttribute("error", "無効なまたは期限切れのトークンです。");
            return "errorPage";
        }

        // トークンからアカウントを取得
        Optional<Account> accountOpt = passwordResetService.findAccountByToken(token);
        if (accountOpt.isEmpty()) {
            model.addAttribute("error", "アカウントが見つかりません。");
            return "errorPage";
        }

        // パスワードの更新
        Account account = accountOpt.get();
        account.setPassword(newPassword); // ハッシュ化を行う場合はここで適用
        account.setUpdatedDate(LocalDateTime.now()); // 更新日時を設定
        accountRepository.save(account);
        System.out.println("Password updated for accountId: " + account.getId());

        // トークンを無効化
        passwordResetService.invalidateToken(token);
        System.out.println("Token invalidated: " + token);

        return "psComplete"; // 完了画面にリダイレクト
    }
    
    // 完了画面を表示
    @GetMapping("/psComplete")
    public String showCompletePage() {
        return "psComplete";
    }
}
