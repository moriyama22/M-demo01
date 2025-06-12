package com.example.demo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.entity.Account;
import com.example.demo.repository.AccountRepository;
import com.example.*;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    // アカウントを保存
    public void save(Account account) {
        accountRepository.save(account);
    }

    // IDの重複チェック
    public boolean existsById(String id) {
        return accountRepository.existsById(id);
    }
}
