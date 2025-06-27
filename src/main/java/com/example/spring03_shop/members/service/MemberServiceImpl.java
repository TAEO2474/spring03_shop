package com.example.spring03_shop.members.service;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.spring03_shop.member.entity.MembersEntity;
import com.example.spring03_shop.members.dto.AuthInfo;
import com.example.spring03_shop.members.dto.ChangePwdCommand;
import com.example.spring03_shop.members.dto.MembersDTO;
import com.example.spring03_shop.members.repository.MemberRepository;

@Service // Spring이 관리하는 서비스 컴포넌트로 등록
public class MemberServiceImpl  implements  MembersService{
	
	
	@Autowired
	private  MemberRepository membersRepository;//회원 정보에 접근하기 위한 JPA 리포지토리
	
	
	public MemberServiceImpl () {// 기본 생성자
		
	}
	/**
	 * 회원가입 처리
	 * - DTO를 엔티티로 변환하여 DB에 저장
	 * - 저장된 정보를 기반으로 AuthInfo 객체 생성 후 반환
	 */
	
	@Override
	public AuthInfo addMemberProcess(MembersDTO dto) {
		membersRepository.save(dto.toEntity());// DTO → Entity 변환 후 저장
		return new AuthInfo(dto.getMemberEmail(),dto.getMemberPass(),dto.getMemberName(),dto.getAuthRole());
	}
	
	/**
	 * 로그인 처리 (현재 미구현)
	 */
	@Override
	public AuthInfo loginProcess(MembersDTO dto) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 회원 정보 조회
	 * - 이메일로 DB에서 회원 정보를 가져와 DTO로 변환 후 반환
	 */
	@Override
	public MembersDTO getByMemberProcess(String memberEmail) {
		Optional<MembersEntity> optMembersEntity = membersRepository.findById(memberEmail);
		return MembersDTO.toDTO(optMembersEntity.get());
	}
	
	/**
	 * 회원 정보 수정 (현재 미구현)
	 */
	@Override
	public AuthInfo updateMemberProcess(MembersDTO dto) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 비밀번호 변경 (현재 미구현)
	 */
	@Override
	public void updatePassProcess(String memberEmail, ChangePwdCommand changePwd) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 회원 탈퇴 (현재 미구현)
	 */
	@Override
	public void deleteMemberProcess(String memberEmail) {
		// TODO Auto-generated method stub
		
	}
		

}
