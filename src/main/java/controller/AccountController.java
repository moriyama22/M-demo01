package controller;

import com.example.demo.entity.Account; 
import com.example.demo.AccountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    
    //新規アカウント作成フォームの表示
    
    @GetMapping("/newAccount")
    public String showNewAccountForm(Model model) {
        model.addAttribute("account", new Account()); // Accountオブジェクトをリクエストスコープに追加
        return "newAccount_R";
    }
    

    
    //アカウント登録処理
    @PostMapping("/newAccount")
    public String createNewAccount(@ModelAttribute Account account, BindingResult result, Model model) {
        // バリデーションエラーの処理
        if (result.hasErrors()) {
            model.addAttribute("nameError", "ユーザー名は必須です。");
            return "newAccount_R";
        }

        // IDの重複チェック
        if (accountService.existsById(account.getId())) {
            model.addAttribute("duplicateError", "このIDは既に使用されています。");
            return "newAccount_R";
        }
        
     // パスワード確認
        if (!account.getPassword().equals(account.getps2())) {
            model.addAttribute("passwordMismatchError", "パスワードが一致しません。");
            return "newAccount_R";
        }

        // 保存処理
        accountService.save(account);
        return "redirect:/account/complete";
    }

    
    //入力確認画面の表示
    @PostMapping("/confirm")
    public String confirmRegistration(@ModelAttribute("account") Account account, Model model) {
        model.addAttribute("account", account); // 入力内容を確認画面に渡す
        return "account-confirmation"; // 確認画面
    }

    
    //登録確定処理
    @PostMapping("/complete")
    public String completeRegistration(@ModelAttribute("account") Account account) {
        accountService.save(account); // データベースにアカウントを保存
        return "account-completion"; // 完了画面
    }

    
     //登録成功画面の遷移例
     
    @PostMapping("/register")
    public String registerAccount(@ModelAttribute Account account) {
        // 登録処理を実行
        System.out.println("登録されたユーザー名: " + account.getName());
        return "registrationSuccess"; // 登録成功ページへリダイレクト
    }
}
*/

