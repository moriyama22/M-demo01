package controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.entity.Account;
import com.example.demo.entity.Notice;
import com.example.demo.repository.NoticeRepository;


import jakarta.servlet.http.HttpSession;


@Controller
public class AdminController {
	private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
	
	@Autowired
    private NoticeRepository noticeRepository;
	

	@Autowired
	HttpSession session;
	
	// 共通処理メソッド
    private void addNoticesToModel(Model model) {
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
        
    }

	
	@GetMapping("/admin")
    public String showAdminPage(Model model) {
		Account account = (Account) session.getAttribute("loggedInAccount");
	    if (account == null) {
	        return "redirect:/login"; // セッションがない場合、ログイン画面にリダイレクト
	    }

	    // 権限チェック
	    if (account.getLevelflag() != 9) {
	        return "redirect:/dashboard"; // 権限がない場合、ダッシュボード画面にリダイレクト
	    }

	    model.addAttribute("account", account);
    	return "admin";
    }
	
	@GetMapping("/Dashboard")
    public String goToDashboard(Model model) {
		addNoticesToModel(model); // 共通処理の呼び出し
        return "Dashboard"; // ダッシュボード画面に直接戻る
    }
	
	
}


