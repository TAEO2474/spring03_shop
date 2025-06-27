package com.example.spring03_shop.config.jwt;


import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.spring03_shop.config.auth.PrincipalDetails;
import com.example.spring03_shop.members.dto.MembersDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

//인증(Authentication)처리 필터 클래스 (사용자 로그인 시 JWT 발급)
@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private AuthenticationManager authManager; // 인증 처리 매니저 (스프링 시큐리티 핵심 컴포넌트)
	private Authentication authentication;     // 인증 결과 저장용 객체

	// 생성자: AuthenticationManager 주입받음
	public JwtAuthenticationFilter(AuthenticationManager authManager) {
		this.authManager = authManager;
	}

	/**
	 * 로그인 시도 시 실행되는 메서드 (사용자 인증 시도)
	 * -> UsernamePasswordAuthenticationToken을 생성하여 인증 시도
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		log.info("JwtAuthenticationFilter => attemptAuthentication() => login요청 처리를 시작");

		try {
// 포스트맨 입력 : {"memberEmail": "dong@google.com", "memberPass":"1234"}
// 아래 콘솔에서 확인: readLine()=>{"memberEmail": "dong@google.com", "memberPass":"1234"}
			// BufferedReader br = request.getReader();
			// String input = null;
			// while ((input = br.readLine()) != null) {
			// log.info("readLine()=>{}", input);
			// }

// 포스트맨 입력: {"memberEmail": "dong@google.com", "memberPass":"1234"}		
// 아래 콘솔에서 확인: memberEmail:dong@google.com, memberPass :1234
			// JSON 형식의 로그인 정보(memberEmail, memberPass)를 파싱
			ObjectMapper om = new ObjectMapper();
			MembersDTO membersDTO = om.readValue(request.getInputStream(), MembersDTO.class);
			log.info("memberEmail:{}, memberPass :{}", membersDTO.getMemberEmail(), membersDTO.getMemberPass());
			
			// 사용자가 입력한 이메일/비밀번호를 인증 토큰에 담는다
			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
					membersDTO.getMemberEmail(), membersDTO.getMemberPass());
			
			// 토큰을 통해 실제 인증 처리 수행
			authentication = authManager.authenticate(authenticationToken);

			log.info("authentication: {}", authentication.getPrincipal());
			
			// 인증 완료 후 사용자 principal 정보 로깅// 인증 완료 후 사용자 principal 정보 로깅
			PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
			log.info("로그인 완료됨(인증) : {}, {}, {}", principalDetails.getUsername(), principalDetails.getPassword(),
					principalDetails.getAuthInfo().getAuthRole());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();// 예외 발생 시 콘솔 출력
		}

		return authentication; // 인증 결과 객체 반환
	}

	//attemptAuthentication() 실행 후 인증이 정상적으로 완료되면 실행된다.
	//여기에서 JWT(Json Web Token) 토큰을 만들어서 request요청한 사용자에게 JWT 토큰을 response 해준다.
	/**
	 * 인증 성공 시 실행되는 메서드
	 * -> JWT 토큰을 생성하고 클라이언트에 반환함
	 */
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		log.info("successfulAuthentication 실행됨");
		
		// 인증된 사용자 정보 꺼내기
		PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();
		// JWT 토큰 생성!
		String jwtToken = JWT.create() // JWT 토큰 생성을 시작 
			    .withSubject("mycors") // 토큰의 주제(subject) 설정. 토큰의 목적이나 대상 시스템을 설명할 때 사용 (예: "로그인 토큰")
			    
			    // 토큰의 만료 시간 설정
			    // 현재 시간(System.currentTimeMillis())으로부터 1시간 뒤로 설정됨
			    .withExpiresAt(new Date(System.currentTimeMillis() + (60 * 60 * 1000 * 1L)))
			    
			    // 사용자 정보를 클레임(claim)으로 추가
			    // 클레임은 JWT 페이로드에 담기는 사용자 정의 데이터


			    // 비밀번호를 memberPass라는 이름으로 클레임에 추가 (보안상 저장 권장 ❌)
			    .withClaim("memberPass", principalDetails.getPassword())


			    // 사용자 이메일(또는 ID)을 memberEmail이라는 이름으로 클레임에 추가
			    .withClaim("memberEmail", principalDetails.getUsername())


			    // 사용자 역할(Role)을 authRole이라는 이름으로 클레임에 추가
			    .withClaim("authRole", principalDetails.getAuthInfo().getAuthRole().toString())


			    // HMAC512 알고리즘과 시크릿 키를 이용해 서명하여 JWT 토큰 생성
			    .sign(Algorithm.HMAC512("mySecurityCos"));

		log.info("jwtToken:{}",jwtToken);
		
		// jwtToken을 Authorization 헤더에 담아 응답
		response.addHeader("Authorization", jwtToken);
		
		// 사용자 이름과 이메일을 JSON 형식으로 응답 본문에 포함
		final Map<String, Object> body = new HashMap<>();
		body.put("memberName", principalDetails.getAuthInfo().getMemberName());
		body.put("memberEmail", principalDetails.getAuthInfo().getMemberEmail());
		
		// JSON 응답 전송
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(response.getOutputStream(), body);		



	}
	/**
	 * 인증 실패 시 실행되는 메서드
	 * -> 401 상태 코드와 함께 실패 메시지 반환
	 */
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		log.info("unsuccessfulAuthentication 실행됨");
		
		// 401 Unauthorized 상태 설정
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        // 에러 메시지 JSON 구성
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", HttpStatus.UNAUTHORIZED.value());
        body.put("error", failed.getMessage());

    	// JSON 응답 전송
        new ObjectMapper().writeValue(response.getOutputStream(), body);


	}
	
}

