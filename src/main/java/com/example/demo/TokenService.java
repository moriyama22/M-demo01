package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

import com.example.demo.entity.MailTokun;
import com.example.demo.repository.*;


/**		@Service
		public class TokenService {
			
			@Autowired
		    private MailTokunRepository mailTokunRepository;
		
				    public boolean validateToken(String token) {
		        Optional<MailTokun> mailTokun = mailTokunRepository.findByTokun(token);
		
		        // トークンが存在しない場合
		        if (mailTokun.isEmpty()) {
		            return false;
		        }
		
		        MailTokun entity = mailTokun.get();
		
		        // トークンが期限切れの場合
		        if (entity.getDeadtime().isBefore(LocalDateTime.now())) {
		            return false;
		        }
		
		        // トークンが既に承認済みの場合
		        if (!"0".equals(entity.getAdflag())) {
		            return false;
		        }
		
		        // 有効なトークン
		        return true;
		    }
		
		}
*/