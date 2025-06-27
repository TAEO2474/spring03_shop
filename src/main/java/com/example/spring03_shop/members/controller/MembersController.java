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
//@CrossOrigin(origins ={"http://localhost:3000"})
//@CrossOrigin("*")
@RestController
public class MembersController {
	
   @Autowired
	private MembersService membersService;
  
   @Autowired
   private BCryptPasswordEncoder encodePassword;
  
   public MembersController() {
	
	}
  
   //회원가입
   @PostMapping(value="/member/signup")
   public ResponseEntity<AuthInfo> addMember(@RequestBody MembersDTO membersDTO){
   	membersDTO.setMemberPass(encodePassword.encode(membersDTO.getMemberPass()));
   	AuthInfo authInfo = membersService.addMemberProcess(membersDTO);      	
   	return ResponseEntity.ok(authInfo);
   }  
   
   //회원정보가져오기
   @GetMapping(value="/member/editinfo/{memberEmail}")
   public ResponseEntity<MembersDTO> getMember (@PathVariable("memberEmail") String memberEmail){
	   MembersDTO memDTO = membersService.getByMemberProcess(memberEmail);
	   return ResponseEntity.ok(memDTO);
	   }
  
  
}




