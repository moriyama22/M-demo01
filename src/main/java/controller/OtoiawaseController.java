package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.entity.Account;

import jakarta.servlet.http.HttpSession;

@Controller
public class OtoiawaseController {
	
	@Autowired
	HttpSession session;
	
	@GetMapping("/otoiawase")
    public String showAdminPage(Model model) {
		Account account = (Account) session.getAttribute("loggedInAccount");
	    if (account == null) {
	        return "redirect:/login"; // セッションがない場合、ログイン画面にリダイレクト
	    }

	    model.addAttribute("account", account);
    	return "otoiawase";
    }

}
