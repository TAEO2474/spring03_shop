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

//Authentication(인증)
@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private AuthenticationManager authManager;
	private Authentication authentication;

	public JwtAuthenticationFilter(AuthenticationManager authManager) {
		this.authManager = authManager;
	}

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
			ObjectMapper om = new ObjectMapper();
			MembersDTO membersDTO = om.readValue(request.getInputStream(), MembersDTO.class);
			log.info("memberEmail:{}, memberPass :{}", membersDTO.getMemberEmail(), membersDTO.getMemberPass());

			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
					membersDTO.getMemberEmail(), membersDTO.getMemberPass());

			authentication = authManager.authenticate(authenticationToken);

			log.info("authentication: {}", authentication.getPrincipal());

			PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
			log.info("로그인 완료됨(인증) : {}, {}, {}", principalDetails.getUsername(), principalDetails.getPassword(),
					principalDetails.getAuthInfo().getAuthRole());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return authentication;
	}

	//attemptAuthentication() 실행 후 인증이 정상적으로 완료되면 실행된다.
	//여기에서 JWT(Json Web Token) 토큰을 만들어서 request요청한 사용자에게 JWT 토큰을 response 해준다.
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		log.info("successfulAuthentication 실행됨");
		PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();
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
		
		//response 응답 header에 jwtToken 추가
		response.addHeader("Authorization", jwtToken);
		
		final Map<String, Object> body = new HashMap<>();
		body.put("memberName", principalDetails.getAuthInfo().getMemberName());
		body.put("memberEmail", principalDetails.getAuthInfo().getMemberEmail());
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(response.getOutputStream(), body);		



	}
	
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		log.info("unsuccessfulAuthentication 실행됨");
		
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", HttpStatus.UNAUTHORIZED.value());
        body.put("error", failed.getMessage());

        new ObjectMapper().writeValue(response.getOutputStream(), body);


	}
	
}

