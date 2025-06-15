package controller;

import com.example.demo.repository.*;
import com.example.demo.MailTokunService;
import com.example.demo.entity.*;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class NewAccountController {

    @Autowired
    private MailTokunService mailTokunService;

    @Autowired
    private AccountRepository accountRepository;

    // 登録画面を表示
    @GetMapping("/register")
    public String showRegistrationForm(
        @RequestParam(value = "token", required = false) String token,
        Model model,
        HttpSession session
    ) {
    	
    	System.out.println("ーーーーーーーーーー登録画面表示開始デバック");
        // トークンの検証
        if (token == null || !mailTokunService.validateToken(token)) {
            model.addAttribute("error", "無効なトークンです。");
            return "errorPage";
        }
        
        // トークンからメールアドレスを取得
        //String emailFromToken = mailTokunService.validateToken(token);
        String email = (String) session.getAttribute("email");
        String emailFromToken = email;
        
        // トークンをセッションに保存
        session.setAttribute("token", token);
        model.addAttribute("token", token);
        
        // モデルに初期値としてメールアドレスを設定
        model.addAttribute("mail", emailFromToken);
        
        System.out.println("ーーーーーーーーーー登録画面表示終了前デバック");
        
        return "newAccount_R"; // 登録画面
    }
    
    @PostMapping("/newAccount_R")
    public String processAccountCreation(
        @RequestParam("name") String name,
        @RequestParam("id") String id,
        @RequestParam("password") String password,
        @RequestParam("password2") String password2,
        @RequestParam(value = "mail", required = false) String mail,
        HttpSession session,
        Model model
    ) {
    	System.out.println("ーーーーーーーーーー登録画面内処理開始デバック");
    	
    	System.out.println("name: " + name);
        System.out.println("id: " + id);
        System.out.println("password: " + password);
        System.out.println("password2: " + password2);
        System.out.println("mail: " + mail); // 確認ポイント
    	
        // 入力内容のバリデーション
        boolean hasError = false;
        if (name.isEmpty() || name.length() > 10) {
            model.addAttribute("nameError", "名前は10文字以内で入力してください");
            hasError = true;
        }
        if (id.isEmpty() || id.length() > 10 || !id.matches("^[A-Za-z0-9]+$")) {
            model.addAttribute("idError", "IDは10文字以内の半角英数字で入力してください");
            hasError = true;
        }
        if (password.isEmpty() || password.length() < 6 || password.length() > 20 
                || !password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,20}$")) {
            model.addAttribute("passwordError", "パスワードは6〜20文字の半角英数字を含めて入力してください");
            hasError = true;
        }
        if (!password.equals(password2)) {
            model.addAttribute("password2Error", "入力されたパスワードが一致しません");
            hasError = true;
        }

        // 入力にエラーがあれば登録画面に戻す
        if (hasError) {
            return "newAccount_R";
        }

        // 重複チェック（Optional を適切に判定）
        if (accountRepository.findById(id).isPresent() || accountRepository.findByMail(mail).isPresent()) {
            model.addAttribute("duplicateError", "IDまたはメールアドレスが既に登録されています");
            return "newAccount_R";
        }
        
        System.out.println("ーーーーーーーーーー登録画面内処理終了デバック");

        // セッションにデータを保存
        session.setAttribute("name", name);
        session.setAttribute("id", id);
        session.setAttribute("password", password);
        session.setAttribute("mail", mail);

        // 確認画面へリダイレクト
        return "redirect:/register/confirm";
    }
    

    // 確認画面を表示
    @GetMapping("/register/confirm")
    public String confirmAccount(Model model, HttpSession session) {
        // セッションからデータを取得
        String name = (String) session.getAttribute("name");
        String id = (String) session.getAttribute("id");
        String password = (String) session.getAttribute("password");
        String mail = (String) session.getAttribute("mail");
        
        System.out.println("ーーーーーーーーーー確認画面表示デバック");
        
        if (name == null || id == null || password == null || mail == null ) {
            model.addAttribute("error", "セッションが切れています。再度登録してください。");
            return "errorPage";
        }
        
        String maskedPassword = "*".repeat(password.length());


        // 確認画面用のモデルにデータを渡す
        model.addAttribute("name", name);
        model.addAttribute("id", id);
        model.addAttribute("password", maskedPassword);
        model.addAttribute("mail", mail);

        return "confirm"; // 確認画面
    }

    // 確認画面からの送信を処理
    @RequestMapping(value = "/confirm", method = RequestMethod.POST)
    public String confirmSubmission(
        @RequestParam("name") String name,
        @RequestParam("id") String id,
        @RequestParam("mail") String mail,
        HttpSession session,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
    	
    	// トークンを再検証
    	String sessionToken = (String) session.getAttribute("token");
    	if (sessionToken == null || !mailTokunService.validateToken(sessionToken)) {
    	    model.addAttribute("error", "無効なトークンです。");
    	    return "errorPage";
    	}
    	
    	// セッションから元のパスワードを取得
        String originalPassword = (String) session.getAttribute("password");

        if (originalPassword == null) {
            model.addAttribute("error", "セッションが切れています。再度登録してください。");
            return "errorPage";
        }
        
        String password = originalPassword;
    	
    	Account account = new Account(id, password, name, mail);
    	account.setRegistrationUser("system"); // またはセッションから取得するユーザー名など
        account.setUpdatedUser("system");
        
        System.out.println("Mail address received: " + mail);
        
    	accountRepository.save(account);
    	
    	// リダイレクト用データ設定
        redirectAttributes.addFlashAttribute("name", name);
        redirectAttributes.addFlashAttribute("id", id);
        redirectAttributes.addFlashAttribute("mail", mail);
        
        

        // 完了画面へリダイレクト
        return "redirect:/complete";
    }

 // 完了画面
    @GetMapping("/complete")
    public String showCompletePage(@ModelAttribute("name") String name, @ModelAttribute("id") String id, Model model) {
        model.addAttribute("name", name);
        model.addAttribute("id", id);
        return "complete";
    }
    
}
