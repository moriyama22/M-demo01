package controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.demo.*;
import com.example.demo.entity.Account;
import com.example.demo.entity.Notice;
import com.example.demo.repository.*;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class indexController {
	
	@Autowired
    private NoticeRepository noticeRepository;
	
	@Autowired
	HttpSession session;
	
	@Autowired
    private AccountRepository accountRepository;
	
	@GetMapping("/login")
    public String newMail() {
    	return "index";
    }
	
	//ログイン処理
	@PostMapping("/login")
	public String login(@RequestParam("id") String id,
	                    @RequestParam("password") String password,
	                    Model model) {
	    try {
	        Account account = accountRepository.findByIdAndPassword(id, password);

	        if (account != null) {
	        	//ログイン成功
	            session.setAttribute("loggedInAccount", account);
	            session.setAttribute("loggedInUser", account.getName()); // 名前だけを保存
	            model.addAttribute("account", account);
	            
	            final Logger logger = LoggerFactory.getLogger(DashboardController.class);
	            try {
	                List<Notice> notices = noticeRepository.findByAdflagOrderByPostsDateDesc("0");

	                // 日時フォーマットの調整
	                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	                for (Notice notice : notices) {
	                    LocalDateTime postDate = notice.getPosts_date();
	                    notice.setFormattedDate(postDate.format(formatter)); // フォーマット済みの日付を設定
	                }

	                logger.info("Number of notices fetched: {}", notices.size());
	                model.addAttribute("notices", notices);
	    	    } catch (Exception e) {
	    	        logger.error("Error fetching notices", e);
	    	    }
	            
	            //権限をセッションに保存
	            session.setAttribute("userRole", account.getLevelflag());
	            return "Dashboard";
	        } else {
	            if (id.isEmpty() || password.isEmpty()) {
	                if (id.isEmpty()) {
	                    model.addAttribute("idError", "IDを入力してください");
	                }
	                if (password.isEmpty()) {
	                    model.addAttribute("psError", "パスワードを入力してください");
	                }
	            } else {
	                model.addAttribute("idError", "ID、またはパスワードが違います。");
	                //model.addAttribute("psError", "パスワードが間違っています");
	            }
	            return "index"; // 再度ログイン画面に戻る
	        }
	    } catch (Exception e) {
	        // エラーログを出力
	        System.err.println("エラーが発生しました: " + e.getMessage());
	        e.printStackTrace();
	        model.addAttribute("error", "予期しないエラーが発生しました");
	        return "errorPage"; // エラー画面を表示
	    }
		
	}
	
	

}
