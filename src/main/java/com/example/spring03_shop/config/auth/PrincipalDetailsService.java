package com.example.spring03_shop.config.auth;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.spring03_shop.member.entity.MembersEntity;
import com.example.spring03_shop.members.dto.AuthInfo;
import com.example.spring03_shop.members.repository.MemberRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PrincipalDetailsService implements UserDetailsService {
	@Autowired
	private MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String memberEmail) throws UsernameNotFoundException {
		log.info("principalDetailService => loadUserByUsername() => memberEmail:{}", memberEmail );
		Optional<MembersEntity> optMemberEntity = memberRepository.findById(memberEmail);
		
		if(optMemberEntity.isEmpty()) {
			 throw new UsernameNotFoundException(memberEmail+"사용자명이 존재하지 않습니다");
		}
		
		MembersEntity membersEntity = optMemberEntity.get();
		log.info("memberEmail:{} memberPass:{} memberName:{}", 
				membersEntity.getMemberEmail(), membersEntity.getMemberPass(),
				membersEntity.getMemberName(), membersEntity.getAuthRole());
		
		AuthInfo authInfo = AuthInfo.builder().memberEmail(membersEntity.getMemberEmail())
				.memberPass(membersEntity.getMemberPass())
				.memberName(membersEntity.getMemberName())
				.authRole (membersEntity.getAuthRole()).build();
		
		return new PrincipalDetails(authInfo);
	}

}
