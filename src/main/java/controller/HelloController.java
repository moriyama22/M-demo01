package controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class HelloController {
	
	@GetMapping("/hello")
    public String hello() {
        return "hello"; // ログイン画面にリダイレクト
    }

}
