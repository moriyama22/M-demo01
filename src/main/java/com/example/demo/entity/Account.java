package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq", nullable = false, unique = true)
    private Long seq;

    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    //@Column(nullable = false, unique = true)
    //private String mail;

    @Column(name = "levelflag", nullable = false)
    private int levelflag;

    @Column(name = "registrationDate", nullable = false)
    private LocalDateTime registrationDate;

    @Column(name = "registrationUser", nullable = false)
    private String registrationUser;

    @Column(name = "updatedDate", nullable = false)
    private LocalDateTime updatedDate;

    @Column(name = "updatedUser", nullable = false)
    private String updatedUser;

    @Column(name = "adflag",nullable = false)
    private String adflag;
    
    @Column(name = "mailaddress", nullable = false, unique = true)
    private String mail;
    
    

    // デフォルトコンストラクタ
    public Account() {
        this.registrationDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
        this.adflag = "0"; // デフォルト値
        this.registrationUser = "system"; // デフォルト値を設定
        this.updatedUser = "system";     // 同様に設定
    }
    
    
    // フルコンストラクタ
    public Account(String id, String password, String name, String mail) {
        this();
        this.id = id;
        this.password = password;
        this.name = name;
        this.mail = mail;
    }

    // ゲッターとセッター
    public Long getSeq() {
        return seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
    
    public int getLevelflag() {
        return levelflag;
    }

    public void setLevelflag(int levelflag) {
        this.levelflag = levelflag;
    }
    
    public LocalDateTime getrRgistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    public String getRegistrationUser() {
        return registrationUser;
    }

    public void setRegistrationUser(String registrationUser) {
        this.registrationUser = registrationUser;
    }
    
    public LocalDateTime getupdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }
    
    public String getUpdatedUser() {
        return updatedUser;
    }

    public void setUpdatedUser(String updatedUser) {
        this.updatedUser = updatedUser;
    }
    
    public String getAdflag() {
        return adflag;
    }

    public void setAdflag(String adflag) {
        this.adflag = adflag;
    }

}
