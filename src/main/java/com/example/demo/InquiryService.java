package com.example.demo;

import com.example.demo.entity.Inquiry;
import com.example.demo.repository.InquiryRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class InquiryService {
	
	private static final Logger logger = LoggerFactory.getLogger(InquiryService.class);

    @Autowired
    private InquiryRepository inquiryRepository;

    /**
     * お問い合わせ検索
     */
    public Page<Inquiry> searchInquiries(Long id, String title, String category, Integer status, Pageable pageable) {
        logger.info("searchInquiries() 実行: id={}, title={}, category={}, status={}", id, title, category, status);
        return inquiryRepository.findByCriteria(id, title, category, status, pageable);
    }
}