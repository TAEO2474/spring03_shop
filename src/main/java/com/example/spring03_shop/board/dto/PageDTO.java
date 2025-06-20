package com.example.spring03_shop.board.dto;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Setter
@Getter
public class PageDTO {
	private long currentPage; // 현재페이지
	private long totalCount; // 총 레코드수
	private long blockCount = 5; // 한 페이지에 보여줄 레코드 수
	private long blockPage = 3; // 한 블록에 보여줄 페이지 수
	private long totalPage; // 총 페이지수
	private long startRow; // 시작 레코드 번호
	private long endRow; // 끝 레코드 번호
	private long startPage; // 한 블록의 시작 페이지 번호
	private long endPage; // 한 블록의 끝 페이지 번호
	private long number;

	private String searchKey;
	private String searchWord;

	public PageDTO() {

	}

	public PageDTO(long currentPage, long totalCount) {
		this.currentPage = currentPage;
		this.totalCount = totalCount;
		
		// 총 페이지수
		totalPage = totalCount / blockCount + (totalCount % blockCount == 0 ? 0 : 1);
		if(totalPage<currentPage)
		  this.currentPage = totalPage;

		// 시작레코드
		startRow = (this.currentPage - 1) * blockCount;

		// 끝레코드
		endRow = startRow + blockCount - 1;		
	

		// 시작 페이지
		startPage = (int) ((this.currentPage - 1) / blockPage) * blockPage + 1;

		// 끝 페이지
		endPage = startPage + blockPage - 1;
		if (totalPage < endPage)
			endPage = totalPage;

		// 리스트에서에 출력번호
		number = totalCount - (this.currentPage - 1) * blockCount;
	}

	public PageDTO(long currentPage, long totalCount, String searchKey, String searchWord) {
		this(currentPage, totalCount);
		this.searchKey = searchKey;
		this.searchWord = searchWord;
	}

	
}// end class













