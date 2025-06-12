package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mailtokun")
public class MailTokun {

    @Id
    @Column(name = "tokun") // データベース列名を明示
    private String tokun; // トークン（主キー）

    @Column(nullable = false)
    private LocalDateTime udate; // 登録日

    @Column(nullable = false)
    private String adflag; // 削除フラグ

    @Column(nullable = false)
    private LocalDateTime deadtime; // トークン期限
    
    @Column( nullable = false)
    private String mail;

    // ゲッターとセッター
    public String getTokun() {
        return tokun;
    }

    public void setTokun(String tokun) {
        this.tokun = tokun;
    }

    public LocalDateTime getUdate() {
        return udate;
    }

    public void setUdate(LocalDateTime udate) {
        this.udate = udate;
    }

    public String getAdflag() {
        return adflag;
    }

    public void setAdflag(String adflag) {
        this.adflag = adflag;
    }

    public LocalDateTime getDeadtime() {
        return deadtime;
    }

    public void setDeadtime(LocalDateTime deadtime) {
        this.deadtime = deadtime;
    }
    
    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

}
