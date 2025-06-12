package controller;

import com.example.demo.InquiryService;
import com.example.demo.entity.Account;
import com.example.demo.entity.Inquiry;
import com.example.demo.entity.InquiryReply;
import com.example.demo.entity.Notice;
import com.example.demo.repository.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/contact")
public class InquiryController {
	
	private static final Logger logger = LoggerFactory.getLogger(InquiryController.class);

    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private InquiryReplyRepository inquiryReplyRepository;

    @Autowired
    private JavaMailSender mailSender;
    
	@Autowired
    private NoticeRepository noticeRepository;
	
	@Autowired
	private InquiryService inquiryService;

    @Autowired
    private HttpSession session;
    
    @Autowired
    private AccountRepository accountRepository;

    private static final String UPLOAD_DIR = "uploads/";
    
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
	
    @GetMapping("/admin/inquiry_list")
    public String viewInquiries(Model model) {
        // 初期表示時は空のリストを渡す
        model.addAttribute("inquiries", List.of());
        model.addAttribute("currentPage", 0);
        model.addAttribute("totalPages", 0);
        return "inquiry_list";
    }
    
    /**
     * お問い合わせ検索処理
     */
    @GetMapping("/admin/inquiries/search")
    public String searchInquiries(
            @RequestParam(name = "id", required = false) Long id,
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "status", required = false, defaultValue = "-1") Integer status,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model) {

        logger.info("検索条件: id={}, title={}, category={}, status={}, page={}", id, title, category, status, page);

        Pageable pageable = PageRequest.of(page, 5);

        // 検索条件なし → 全件取得（5件ずつ、作成日の新しい順）
        if ((id == null) && (title == null || title.isEmpty()) && (category == null || category.isEmpty()) && (status == -1)) {
            Page<Inquiry> inquiryPage = inquiryRepository.findAllByOrderByCreatedAtDesc(pageable);
            model.addAttribute("inquiries", inquiryPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", inquiryPage.getTotalPages());
            return "inquiry_list";
        }

        // 検索条件あり
        Page<Inquiry> inquiryPage = inquiryService.searchInquiries(id, title, category, status, pageable);
        logger.info("検索結果: {} 件 (総ページ数: {})", inquiryPage.getTotalElements(), inquiryPage.getTotalPages());

        model.addAttribute("inquiries", inquiryPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", inquiryPage.getTotalPages());

        // 検索条件を Thymeleaf に渡す（検索条件の保持）
        model.addAttribute("param", new HashMap<String, String>() {{
            put("id", id != null ? id.toString() : "");
            put("title", title != null ? title : "");
            put("category", category != null ? category : "");  // デフォルト値を "" にする
            put("status", status != null ? status.toString() : "-1");
            
            System.out.println("検索条件: id=" + id + ", title=" + title + ", category=" + category + ", status=" + status);
        }});

        return "inquiry_list";
    }


    // お問合せフォーム送信処理
    @PostMapping("/submit")
    public String submitInquiry(@RequestParam("category") String category,
                                @RequestParam("title") String title,
                                @RequestParam("details") String details,
                                @RequestParam(value = "image", required = false) MultipartFile image,
                                RedirectAttributes redirectAttributes) {
        
    	Account account = (Account) session.getAttribute("loggedInAccount");
    	if (account == null) {
            return "redirect:/login"; // 未ログインならログインページへ
        }

    	String userId = account.getId();

        Inquiry inquiry = new Inquiry();
        inquiry.setUserId(userId);
        inquiry.setCategory(category);
        inquiry.setTitle(title);
        inquiry.setDetails(details);

     // 画像アップロード処理
        if (image != null && !image.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/uploads/";
                File uploadFolder = new File(uploadDir);

                // フォルダがなければ作成
                if (!uploadFolder.exists()) {
                    boolean created = uploadFolder.mkdirs();
                    if (created) {
                        System.out.println("フォルダを作成しました: " + uploadDir);
                    } else {
                        System.out.println("フォルダの作成に失敗しました");
                    }
                }

                // 画像のファイル名をユニークにする（重複防止）
                String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                String imagePath = "/uploads/" + fileName;  // DBには相対パスで保存
                File destFile = new File(uploadFolder, fileName); // 絶対パスで保存

                // 画像をファイルに書き込み
                image.transferTo(destFile);
                inquiry.setImagePath(imagePath);
                System.out.println("Image Path in DB: " + imagePath);
                System.out.println("Image Path in Folder: " + destFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("error", "画像のアップロードに失敗しました");
                return "redirect:/otoiawase";
            }
        }
        
        inquiryRepository.save(inquiry);

        // 管理者にメール通知
        sendAdminNotification(inquiry);

        redirectAttributes.addFlashAttribute("message", "お問合せが送信されました！ お問合せ番号: " + inquiry.getId());
        return "redirect:/otoiawase";
    }
    
    

    // 管理者用: お問合せ一覧
    @GetMapping("/admin/inquiries")
    public String viewInquiries1(Model model) {
        // 初期表示時は空のリストを渡す
        model.addAttribute("inquiries", List.of());
        model.addAttribute("currentPage", 0);
        model.addAttribute("totalPages", 0);
        return "admin/inquiry_list";
    }

    // 管理者用: お問合せ詳細＆返信画面
    @GetMapping("/admin/inquiries/{id}")
    public String viewInquiryDetail(@PathVariable("id") Long id, Model model) {
        Optional<Inquiry> inquiry = inquiryRepository.findById(id);
        if (inquiry.isPresent()) {
        	Inquiry inquiryData = inquiry.get(); // Optionalからデータを取り出す
            model.addAttribute("inquiry", inquiry.get());
         // 画像パスのデバッグ出力
            if (inquiryData.getImagePath() != null) {
                System.out.println("Image Path?: " + inquiryData.getImagePath());
            } else {
                System.out.println("Image Path is null");
            }
            return "inquiry_detail";
        }
        return "redirect:inquiry_detail";
    }

    // 管理者用: 返信処理
    @PostMapping("/admin/reply")
    public String replyToInquiry(@RequestParam("inquiryId") Long inquiryId,
                                 @RequestParam("message") String message,
                                 RedirectAttributes redirectAttributes) {
        Optional<Inquiry> inquiryOpt = inquiryRepository.findById(inquiryId);
        if (inquiryOpt.isPresent()) {
            Inquiry inquiry = inquiryOpt.get();
            
            Account account = (Account) session.getAttribute("loggedInAccount");

            // 返信データを保存
            InquiryReply reply = new InquiryReply();
            reply.setInquiry(inquiry);
            reply.setAdminId(account.getId()); // ここで管理者IDをセット
            reply.setMessage(message);
            inquiryReplyRepository.save(reply);

            // ユーザーへメール返信
            sendUserReply(inquiry, message);

            // ステータス更新
            inquiry.setStatus(1);
            inquiry.setUpdatedAt(LocalDateTime.now());
            inquiryRepository.save(inquiry);

            redirectAttributes.addFlashAttribute("message", "返信を送信しました");
        }
        return "redirect:inquiry_list";
    }

    // 管理者へのメール通知
    private void sendAdminNotification(Inquiry inquiry) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo("admin@example.com"); // 管理者メールアドレス
            helper.setSubject("新しいお問合せが届きました");
            helper.setText("お問合せID: " + inquiry.getId() + "\nタイトル: " + inquiry.getTitle() + "\nカテゴリ: " + inquiry.getCategory());

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    // ユーザーへの返信メール送信
    private void sendUserReply(Inquiry inquiry, String adminMessage) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // 🔑 ここでユーザーのメールアドレスを取得
            String userEmail = getUserEmailById(inquiry.getUserId());
            if (userEmail == null) {
                System.out.println("ユーザーのメールアドレスが見つかりません");
                return;
            }

            helper.setTo(userEmail); // ユーザーのメールアドレス宛に送信
            helper.setSubject("お問い合わせへの返信");
            helper.setText("お問合せID: " + inquiry.getId() + "\n管理者からの返信:\n" + adminMessage);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    // ユーザーのメールアドレスを取得（仮のメソッド）
    private String getUserEmailById(String userId) {
        Optional<Account> accountOpt = accountRepository.findById(userId);
        if (accountOpt.isPresent()) {
            return accountOpt.get().getMail();
        }
        return null;
    }
    
    
}
