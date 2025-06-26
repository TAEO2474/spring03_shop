package com.example.spring03_shop.members.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring03_shop.members.dto.AuthInfo;
import com.example.spring03_shop.members.dto.MembersDTO;
import com.example.spring03_shop.members.service.MembersService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
//@CrossOrigin(origins ={"http//localhost:3000"})
@CrossOrigin("*")

@RestController
public class MembersController {
	@Autowired
	private MembersService memberService;
	
	@Autowired
	private BCryptPasswordEncoder encodePassword;
	public MembersController() {
		
	}
	
	//회원가입
	@PostMapping(value="/member/signup")
	public ResponseEntity<AuthInfo> addMember (@RequestBody MembersDTO membersDTO){
		membersDTO.setMemberPass(encodePassword.encode(membersDTO.getMemberPass())); // 사용자가 입력한 비번을 암호함시킨다.
		AuthInfo authInfo = memberService.addMemberProcess(membersDTO);
		return ResponseEntity.ok(authInfo);
	}
	
	

}
