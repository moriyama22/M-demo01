package controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import com.example.demo.entity.Account;
import com.example.demo.entity.Notice;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.NoticeRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController {

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

    // ログアウト処理
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // セッションを無効化
        return "index"; // ログイン画面にリダイレクト
    }

    // ダッシュボード画面 (POST)
    @PostMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // セッションからアカウント情報を取得
        Account account = (Account) session.getAttribute("loggedInAccount");
        if (account == null) {
            return "redirect:/login"; 
        }
        addNoticesToModel(model); // 共通処理の呼び出し
        return "Dashboard"; // dashboard.html をレンダリング
    }

    // ダッシュボード画面 (GET)
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        // セッションからアカウント情報を取得
        Account account = (Account) session.getAttribute("loggedInAccount");
        if (account == null) {
            return "redirect:/login"; 
        }
        addNoticesToModel(model); // 共通処理の呼び出し
        return "Dashboard"; // dashboard.html をレンダリング
    }
}