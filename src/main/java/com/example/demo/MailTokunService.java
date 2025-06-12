package com.example.demo;

import com.example.demo.entity.MailTokun;
import com.example.demo.repository.MailTokunRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class MailTokunService {

    @Autowired
    private MailTokunRepository mailTokunRepository;
    
    

    public String generateAndSaveToken(String email) {
        // トークンを生成
        String token = UUID.randomUUID().toString();
        
        System.out.println("Generated Token: " + token); // デバッグ用

        // トークン情報をデータベースに保存
        MailTokun mailtokun = new MailTokun();
        mailtokun.setTokun(token);
        mailtokun.setUdate(LocalDateTime.now());
        mailtokun.setAdflag("0"); // 削除フラグ: 0（有効）
        mailtokun.setDeadtime(LocalDateTime.now().plusHours(1)); // 有効期限を1時間後に設定
        mailtokun.setMail(email); // メールアドレスをセット
        
        System.out.println("保存するトークン: " + token + ", メール: " + email); // デバッグ用ログ

        mailTokunRepository.save(mailtokun);

        return token; // 生成したトークンを返す
    }
    
    /**
     * トークンの有効性を検証するメソッド
     * 
     * @param token トークン文字列
     * @return 検証結果 (成功: true, 無効: false)
     */

    public boolean validateToken(String token) {
        // トークンを検索
        Optional<MailTokun> optionalMailTokun = mailTokunRepository.findByTokun(token);

        // トークンが存在しない場合は無効
        if (optionalMailTokun.isEmpty()) {
            return false;
        }

        // Optional から MailTokun オブジェクトを取り出す
        MailTokun mailTokun = optionalMailTokun.get();

        // 有効期限内か確認
        return mailTokun.getDeadtime().isAfter(LocalDateTime.now());
    }
    
    /*
    public String getEmailByToken(String token) {
        // トークンを検索
        Optional<MailTokun> optionalMailTokun = mailTokunRepository.findByTokun(token);

        // トークンが存在しない場合は例外をスロー
        if (optionalMailTokun.isEmpty()) {
            throw new IllegalArgumentException("無効なトークンです: " + token);
        }

        // MailTokun オブジェクトを取得
        MailTokun mailTokun = optionalMailTokun.get();

        // メールアドレスを返す
        return mailTokun.getMail();
    }
    */

    
}