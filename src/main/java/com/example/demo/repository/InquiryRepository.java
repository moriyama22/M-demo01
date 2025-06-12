package com.example.demo.repository;

import com.example.demo.entity.Inquiry;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

	@Query("SELECT i FROM Inquiry i WHERE "
		     + "(:id IS NULL OR i.id = :id) "
		     + "AND (:title IS NULL OR LOWER(i.title) LIKE CONCAT('%', LOWER(:title), '%')) "
		     + "AND (:category IS NULL OR i.category = :category) "
		     + "AND (:status IS NULL OR :status = -1 OR i.status = :status) "
		     + "ORDER BY i.createdAt DESC")
		Page<Inquiry> findByCriteria(
		    @Param("id") Long id,
		    @Param("title") String title,
		    @Param("category") String category,
		    @Param("status") Integer status,
		    Pageable pageable
		);
	
	
	
	@Query("SELECT i FROM Inquiry i ORDER BY i.createdAt DESC")
	Page<Inquiry> findAllByOrderByCreatedAtDesc(Pageable pageable);
	
}