package com.example.spring03_shop.members.service;

import com.example.spring03_shop.members.dto.AuthInfo;
import com.example.spring03_shop.members.dto.ChangePwdCommand;
import com.example.spring03_shop.members.dto.MembersDTO;
//회원 관련 기능의 서비스 인터페이스
public interface MembersService {
//	public AuthInfo addMemberProcess(MembersDTO dto);
//	public AuthInfo loginProcess(MembersDTO dto);
//	public MembersDTO  getByMemberProcess(String memberEmail);
//	public AuthInfo updateMemberProcess(MembersDTO dto);
//	public void updatePassProcess(String memberEmail, ChangePwdCommand changePwd);
//	
//	public void deleteMemberProcess(String memberEmail);
	
	/**
	 * 회원가입 처리
	 * - 회원 정보를 DB에 저장하고 가입 결과 정보를 반환
	 */
	public AuthInfo addMemberProcess(MembersDTO dto);

	/**
	 * 로그인 처리
	 * - 전달받은 로그인 정보로 사용자 인증을 시도하고 결과 반환
	 */
	public AuthInfo loginProcess(MembersDTO dto);

	/**
	 * 회원 정보 조회
	 * - 이메일을 기준으로 DB에서 회원 정보를 조회
	 */
	public MembersDTO getByMemberProcess(String memberEmail);

	/**
	 * 회원 정보 수정
	 * - 사용자의 이름, 권한 등 정보를 수정하고 결과 반환
	 */
	public AuthInfo updateMemberProcess(MembersDTO dto);

	/**
	 * 비밀번호 변경
	 * - 이메일과 새 비밀번호 정보(ChangePwdCommand)를 받아 비밀번호를 변경
	 */
	public void updatePassProcess(String memberEmail, ChangePwdCommand changePwd);

	/**
	 * 회원 탈퇴
	 * - 이메일을 기준으로 회원 정보를 삭제
	 */
	public void deleteMemberProcess(String memberEmail);


}
