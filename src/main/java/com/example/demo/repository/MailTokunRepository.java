package com.example.demo.repository;

import com.example.demo.entity.MailTokun;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MailTokunRepository extends JpaRepository<MailTokun, String>{
	
	
	 //MailTokun findByTokun(String tokun);
	    void deleteByTokun(String tokun);
	    Optional<MailTokun> findByTokun(String tokun);

}
