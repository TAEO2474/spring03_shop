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
	/**
	 * Spring Security가 로그인 시 호출하는 메서드
	 * - UsernamePasswordAuthenticationToken에서 전달된 아이디(이메일)를 기준으로
	 *   DB에서 사용자 정보를 조회하고, UserDetails 형태로 반환한다.
	 * - 인증에 사용되는 핵심 로직
	 */
	@Override
	public UserDetails loadUserByUsername(String memberEmail) throws UsernameNotFoundException {
		log.info("principalDetailService => loadUserByUsername() => memberEmail:{}", memberEmail );
		// 이메일(memberEmail)을 기준으로 DB에서 사용자 정보를 조회
		Optional<MembersEntity> optMemberEntity = memberRepository.findById(memberEmail);// DB정보와 연결
		// 사용자가 존재하지 않으면 예외 발생 → Spring Security에서 로그인 실패 처리
		if(optMemberEntity.isEmpty()) {
			 throw new UsernameNotFoundException(memberEmail+"사용자명이 존재하지 않습니다");
		}
		
		// 사용자 정보가 존재하면 꺼냄
		MembersEntity membersEntity = optMemberEntity.get();
		log.info("memberEmail:{} memberPass:{} memberName:{}", 
				membersEntity.getMemberEmail(), membersEntity.getMemberPass(),
				membersEntity.getMemberName(), membersEntity.getAuthRole());
		
		// 사용자 정보를 DTO 형태로 포장 (비즈니스용 데이터 구조)
		AuthInfo authInfo = AuthInfo.builder().memberEmail(membersEntity.getMemberEmail())
				.memberPass(membersEntity.getMemberPass())
				.memberName(membersEntity.getMemberName())
				.authRole (membersEntity.getAuthRole()).build();
		
		// 스프링 시큐리티가 인증 처리를 위해 사용할 수 있도록 PrincipalDetails에 담아 반환
	    // PrincipalDetails는 UserDetails를 구현한 사용자 정의 클래스
		return new PrincipalDetails(authInfo);
	}
	
}
