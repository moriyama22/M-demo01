package com.example.demo.repository;

import com.example.demo.entity.Inquiry;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Inquiry;

import java.util.List;

@Repository
public class InquiryDao {
	
    @PersistenceContext
    private EntityManager entityManager; // JPAの生SQLを実行するオブジェクト
    
    public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    	

        /**
         * お問い合わせ検索（条件がある場合のみ）
         */
    	@Query("""
    		    SELECT i FROM Inquiry i 
    		    WHERE (:id IS NULL OR :id = 0 OR i.id = :id)
    		    AND (:title IS NULL OR LOWER(i.title) LIKE LOWER(CONCAT('%', :title, '%')))
    		    AND (:category IS NULL OR i.category = :category)
    		    AND (:status = -1 OR i.status = :status)
    		    ORDER BY i.createdAt DESC
    		""")
    		Page<Inquiry> findByCriteria(
    		    @Param("id") Long id,
    		    @Param("title") String title,
    		    @Param("category") String category,
    		    @Param("status") Integer status,
    		    Pageable pageable
    		);

        /**
         * 全件取得（作成日の新しい順、ページング対応）
         */
        Page<Inquiry> findAllByOrderByCreatedAtDesc(Pageable pageable);
    }
}