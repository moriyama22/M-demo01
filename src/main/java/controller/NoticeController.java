package controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.demo.entity.Account;
import com.example.demo.entity.Notice;
import com.example.demo.repository.*;

import jakarta.servlet.http.HttpSession;

@Controller
public class NoticeController {
	
	@Autowired
	HttpSession session;
	
	@Autowired
    private NoticeRepository noticeRepository;
	
	
	// 共通処理メソッド
    private void addNoticesToModel(Model model) {
    	final Logger logger = LoggerFactory.getLogger(DashboardController.class);
        try {
        	//List<Notice> findAllByOrderByPostsDateDesc();
            List<Notice> notices = noticeRepository.findAllByOrderByPostsDateDesc();

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
	
    @GetMapping("/admin/postNotice")
    public String showPostNoticePage(Model model) {
        // セッションからアカウント情報を取得
        Account account = (Account) session.getAttribute("loggedInAccount");
        if (account == null) {
            return "redirect:/login"; 
        }
        
        addNoticesToModel(model); // 共通メソッドでデータ取得
        return "postNotice";
    }


    @PostMapping("/admin/postNotice")
    public String postNotice(@RequestParam("title") String title,
                             @RequestParam("content") String content,
                             RedirectAttributes redirectAttributes) {
        try {
            Account account = (Account) session.getAttribute("loggedInAccount");
            if (account == null) {
                return "redirect:/login"; 
            }

            String loggedInUser = account.getName();

            Notice notice = new Notice();
            notice.setTitle(title);
            notice.setContent(content);
            notice.setPosts_usre(loggedInUser);
            notice.setUpdated_usre(loggedInUser);
            notice.setPosts_date(LocalDateTime.now());

            noticeRepository.save(notice);

            redirectAttributes.addFlashAttribute("message", "お知らせを投稿しました！");
            return "redirect:/admin/postNotice"; // リダイレクトでページを更新
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "お知らせの投稿に失敗しました。");
            return "redirect:/admin/postNotice";
        }
    }
	
    
    @GetMapping("/postNotice")
    public String returnPostNotice( Model model) {
    	// セッションからアカウント情報を取得
        Account account = (Account) session.getAttribute("loggedInAccount");
        if (account == null) {
            return "redirect:/login"; 
        }
        
        addNoticesToModel(model); // 共通メソッドでデータ取得
        return "postNotice";
    }
    
    
    // お知らせ詳細画面
    @GetMapping("/postNotice/{seq}")
    public String showNoticeDetail(@PathVariable("seq") Long seq, Model model) {
        Notice notice = noticeRepository.findById(seq).orElse(null);
        if (notice == null) {
        	addNoticesToModel(model); // 共通メソッドでデータ取得
            return "redirect:/postNotice"; // 存在しない場合は一覧画面へリダイレクト
        }
        model.addAttribute("notice", notice);
        addNoticesToModel(model); // 共通メソッドでデータ取得
        return "noticeDetail"; // 詳細画面のHTML
    }
    
    // 更新処理
    @PostMapping("/notice/update")
    public String updateNotice(@RequestParam(name = "seq") Long seq, @RequestParam(name = "adflag") String adflag, Model model) {
        Notice notice = noticeRepository.findById(seq).orElse(null);
        if (notice != null) {
            notice.setAdflag(adflag); // 表示/非表示を変更
            notice.setUpdated_date(LocalDateTime.now()); // 更新日時を変更
            noticeRepository.save(notice);
        }
        addNoticesToModel(model); // 共通メソッドでデータ取得
        return "/postNotice"; // 一覧画面へ戻る
    }

}
