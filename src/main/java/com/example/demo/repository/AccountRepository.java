package com.example.demo.repository;

import com.example.demo.entity.Account;
//import com.example.demo.entity.MailTokun;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findBySeq(Long seq); 
    Optional<Account> findById(String id);
    Account findByPassword(String password);
    List<Account> findByName(String name);
    Optional<Account> findByMail(String mail);
    Optional<Account> findByLevelflag(Integer levelFlag);
    Account findByRegistrationDate(LocalDateTime registrationDate);
    Account findByRegistrationUser(String registrationUser);
    Account findByUpdatedDate(LocalDateTime updatedDate);
    Account findByUpdatedUser(String updatedUser);
    Account findByAdflag(String adflag);
    
    Account findByIdAndPassword(String id, String password);
    
 // IDが既に存在するかチェック
    boolean existsById(String id);
}
