package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Notice;


public interface NoticeRepository extends JpaRepository<Notice, Long> {
	
    // お知らせ投稿画面用（全取得）
    //@Select("SELECT * FROM news")
    //List<Notice> findAllNews();

	
	// 投稿日の降順で全件取得
    List<Notice> findAllByOrderByPostsDateDesc();

    // adflagが "0"（存在）のお知らせを、投稿日降順で取得
    List<Notice> findByAdflagOrderByPostsDateDesc(String adflag);
    

}
