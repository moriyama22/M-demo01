package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "notice")
public class Notice {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq", nullable = false, unique = true)
    private Long seq;
	
	@Column(name = "title", nullable = false)
    private String title;
	
	@Column(name = "content", nullable = false)
    private String content;
	
	@Column(name = "adflag",nullable = false)
    private String adflag;
	
	@Column(name = "Posts_date", columnDefinition = "timestamp(6) without time zone")
    private LocalDateTime postsDate;
	
	@Column(name = "Posts_usre", nullable = false)
    private String Posts_usre;
	
	@Column(name = "Updated_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime Updated_date;
	
	@Column(name = "Updated_usre", nullable = false)
    private String Updated_usre;
	
    
    private String formattedDate;   // フォーマット済みの日付データ
	
	//public Notice() {}
	// デフォルトコンストラクタ
    public Notice() {
        this.postsDate = LocalDateTime.now();
        this.Updated_date = LocalDateTime.now();
        this.adflag = "0"; // デフォルト値
    }
    
    // フルコンストラクタ
    public Notice(String title, String content) {
        this();
        this.title = title;
        this.content = content;
    }
    
    // ゲッターとセッター
    public Long getSeq() {
        return seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    public String getAdflag() {
        return adflag;
    }

    public void setAdflag(String adflag) {
        this.adflag = adflag;
    }
    
    public LocalDateTime getPosts_date() {
        return postsDate;
    }

    public void setPosts_date(LocalDateTime Posts_date) {
        this.postsDate = Posts_date;
    }
    
    public String getPosts_usre() {
        return Posts_usre;
    }

    public void setPosts_usre(String Posts_usre) {
        this.Posts_usre = Posts_usre;
    }
    
    public LocalDateTime getUpdated_date() {
        return Updated_date;
    }

    public void setUpdated_date(LocalDateTime Updated_date) {
        this.Updated_date = Updated_date;
    }
    
    public String getUpdated_usre() {
        return Updated_usre;
    }

    public void setUpdated_usre(String Updated_usre) {
        this.Updated_usre = Updated_usre;
    }

    
    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }

}
