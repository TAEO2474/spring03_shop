package com.example.spring03_shop.members.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring03_shop.members.dto.AuthInfo;
import com.example.spring03_shop.members.dto.MembersDTO;
import com.example.spring03_shop.members.service.MembersService;

import lombok.extern.slf4j.Slf4j;
@Slf4j
//@CrossOrigin(origins ={"http://localhost:3000"})  // 특정 도메인에서의 요청만 허용할 경우
//@CrossOrigin("*") // 모든 도메인에서의 요청 허용 (보안상 실제 서비스에선 권장되지 않음)
@RestController  // REST API 컨트롤러임을 나타냄 (JSON 형태로 응답)
public class MembersController {
	
   @Autowired
	private MembersService membersService;// 비즈니스 로직 처리 서비스
  
   @Autowired
   private BCryptPasswordEncoder encodePassword;// 비밀번호 암호화용 객체
  
   public MembersController() {// 기본 생성자
	
	}
   /**
    * [회원가입 요청 처리]
    * - 요청: POST /member/signup
    * - 요청 바디(JSON): MembersDTO (이메일, 비밀번호 등)
    * - 비밀번호는 BCrypt로 암호화하여 저장
    * - 응답: 가입된 사용자 정보(AuthInfo)
    */
   
   //회원가입
   // 클라이언트가 JSON 형태로 회원 정보를 보내면 DTO로 매핑됨
   // 비밀번호를 BCrypt로 암호화한 뒤, 서비스로 전달하여 DB에 저장
   @PostMapping(value="/member/signup")
   public ResponseEntity<AuthInfo> addMember(@RequestBody MembersDTO membersDTO){
	// 입력된 비밀번호를 BCrypt로 암호화
	membersDTO.setMemberPass(encodePassword.encode(membersDTO.getMemberPass()));
	// 서비스 계층에 회원가입 처리 요청
   	AuthInfo authInfo = membersService.addMemberProcess(membersDTO); 
	// 가입된 회원 정보 반환 (HTTP 200 OK)
   	return ResponseEntity.ok(authInfo);
   }  
   
   // 회원 정보 조회
   // 이메일을 경로 변수로 받아 해당 회원의 정보를 조회함
   @GetMapping(value="/member/editinfo/{memberEmail}")
   public ResponseEntity<MembersDTO> getMember (@PathVariable("memberEmail") String memberEmail){
	   // 이메일 기준으로 회원 정보 조회
	   MembersDTO memDTO = membersService.getByMemberProcess(memberEmail);
	   
	   // 조회된 정보 반환 (HTTP 200 OK)
	   return ResponseEntity.ok(memDTO);
	   }
  
  
}




