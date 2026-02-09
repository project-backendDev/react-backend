package com.project.cmm;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.Data;

@Data
public class PageRequestDto {

	private int page = 1;
	
    private int size = 5;

    public Pageable getPageable(Sort sort) {
        int pageNum = (page > 0) ? page - 1 : 0;
        
        return PageRequest.of(pageNum, size, sort);
    }
}
