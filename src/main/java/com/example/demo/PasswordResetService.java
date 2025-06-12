package com.example.demo;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.repository.*;
import com.example.demo.entity.*;

@Service
public class PasswordResetService {
	
	@Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
	
	@Autowired
    private AccountRepository accountRepository;
	
	// トークンを保存する
    public void saveToken(String accountId, String token) {
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setAccountId(accountId.toString()); // 正確に Long 型を文字列化
        resetToken.setToken(token);
        resetToken.setCreatedAt(LocalDateTime.now());
        resetToken.setExpiresAt(LocalDateTime.now().plusHours(1)); // 有効期限1時間
        passwordResetTokenRepository.save(resetToken);
    }
    
 // トークンの有効性をチェックする
    public boolean validateToken(String token) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);
        return resetToken != null && resetToken.getExpiresAt().isAfter(LocalDateTime.now());
    }

 // トークンを無効化
    public void invalidateToken(String token) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);
        if (resetToken != null) {
            passwordResetTokenRepository.delete(resetToken);
        }
    }
    
 // トークンからアカウントを検索する
    public Optional<Account> findAccountByToken(String token) {
        // トークン検索
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);
        if (resetToken == null) {
            System.out.println("Token not found in the database: " + token);
            return Optional.empty();
        }

        System.out.println("Token found: " + resetToken);

        // accountId をそのまま使って検索
        String accountId = resetToken.getAccountId();
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isEmpty()) {
            System.out.println("Account not found for accountId: " + accountId);
        } else {
            System.out.println("Account found: " + accountOpt.get());
        }
        return accountOpt;
    }
       
}
