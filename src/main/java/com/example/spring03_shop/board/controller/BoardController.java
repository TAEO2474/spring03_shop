package com.example.spring03_shop.board.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.spring03_shop.board.dto.BoardDTO;
import com.example.spring03_shop.board.dto.PageDTO;
import com.example.spring03_shop.board.service.BoardService;
import com.example.spring03_shop.common.file.FileUpload;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;


//@CrossOrigin("*")
@Slf4j
@RestController
public class BoardController {
	@Autowired
	private BoardService boardService;
	private int currentPage;
	private PageDTO pdto;

	@Value("${spring.servlet.multipart.location}") // # 파일 업로드 경로
	private String tempDir;

	public BoardController() {

	}

	// http://localhost:8090/board/list/1 (포스트맨용)
	@GetMapping(value = "/board/list/{currentPage}")
	public ResponseEntity<Map<String, Object>> listExecute(@PathVariable("currentPage") int currentPage) {
		Map<String, Object> map = new HashMap();
		long totalRecord = boardService.countProcess(); // 페이징수: 전체 게시글 수(총 레코드 수) 를 가져오는 역할
		log.info("totalRecord:{}", totalRecord);

		if (totalRecord >= 1) {
			this.currentPage = currentPage;
			this.pdto = new PageDTO(this.currentPage, totalRecord);

			map.put("boardList", boardService.listProcess(pdto));
			map.put("pv", this.pdto);

		}
		return ResponseEntity.ok().body(map);
	}

	// 첨부파일이 있을 경우, @RequestBody를 선언라면 안된다.
	// 답변글일 경우, ref, refStep, refLevel를 담아서 클라이언트로 넘겨야한다
	@PostMapping("/board/write")
	public ResponseEntity<String> writeProExecute(BoardDTO dto, HttpServletRequest req) {
		MultipartFile file = dto.getFilename();
		log.info("file:{}", file);

		// 파일 첨부가 있는 경우
		if (file != null && !file.isEmpty()) {
			UUID random = FileUpload.saveCopyFile(file, tempDir);
			dto.setUpload(random + "_" + file.getOriginalFilename());
		}
		;
		dto.setIp(req.getRemoteAddr());
		boardService.insertProcess(dto);

		return ResponseEntity.ok(String.valueOf(1));
	}// end writeProExecute()

	@GetMapping(value = "/board/view/{num}")
	public ResponseEntity<BoardDTO> viewExecute(@PathVariable("num") long num) {
		BoardDTO boardDTO = boardService.contentProcess(num);
		return ResponseEntity.ok(boardDTO);
	}

	@PutMapping(value = "/board/update")
	public ResponseEntity<Void> updateExecute(BoardDTO dto, HttpServletRequest req) {
		MultipartFile file = dto.getFilename();

		if (file != null && !file.isEmpty()) {
			UUID random = FileUpload.saveCopyFile(file, tempDir);
			dto.setUpload(random + "_" + file.getOriginalFilename());
		}
		boardService.updateProcess(dto, tempDir);
		return ResponseEntity.ok(null);
	}

	@DeleteMapping(value = "/board/delete/{num}")
	public ResponseEntity<Void> delateExecute(@PathVariable("num") long num) {
		boardService.deleteProcess(num, tempDir);
		return ResponseEntity.ok(null);
	}

	// http://localhost:8090/board/contentdownload/35d49b3c-4a49-4e4c-91c2-c03c4eeafe29_프리미어리그(과제).docx
	// 35d49b3c-4a49-4e4c-91c2-c03c4eeafe29_프리미어리그(과제).docx
	@GetMapping(value = "/board/contentdownload/{filename}")
	public ResponseEntity<Resource> downloadExecute(@PathVariable("filename") String filename) throws IOException {
		String fileName = filename.substring(filename.indexOf("_") + 1);

		// 파일명이 한글일때 인코딩 작업을 한다.
		String str = URLEncoder.encode(fileName, "UTF-8");
		log.info("str => {}", str);

		// 원본파일명에 공백이 있을 때, "+"표시가 되므로 공백으로 처리해줌
		str = str.replaceAll("\\+", "%20");

		Path path = Paths.get(tempDir + "\\" + filename);
		Resource resource = new InputStreamResource(Files.newInputStream(path));
		// log.info("resource => {}", resource.contentLength());
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");// 첨부파일형식 지정
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + str + ";");//첨부파일명 지정

		return ResponseEntity.ok().headers(headers).body(resource);
	}

}// end class
