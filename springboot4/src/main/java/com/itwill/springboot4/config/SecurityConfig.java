package com.itwill.springboot4.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
//-> 스프링 컨테이너에 객체(bean)를 생성, 관리 - 필요한 곳에 의존성 주입.
public class SecurityConfig {
    
    // Spring Security 5 버전부터 비밀번호는 반드시 암호화를 해야 함.
    // 비밀번호를 암호화하지 않으면 HTTP 403(access denied, 접근 거부) 또는
    // HTTP 500 (내부 서버 오류, internal server error) 에러가 발생함.
    // 비밀번호 암호화를 할 수 있는 객체를 스프링 컨테이너가 bean으로 관리해야 함.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    // 사용자 관리(로그인, 로그아웃, 회원 가입 등)를 위한 서비스 인터페이스.
    // 스프링 부트 애플리케이션에서 스프링 시큐리트를 이용한 로그인/로그아웃을 하려면
    // UserDetailsService 인터페이스를 구현하는 서비스 클래스와
    // UserDetails 인터페이스를 구현하는 엔터티 클래스가 있어야 함.
    @Bean
    public UserDetailsService inMemoryUserDetailsService() {
        // 메모리에 임시 저장하는 사용자 객체를 생성
        UserDetails user1 = User.withUsername("user1") // 로그인 사용자 이름(아이디)
                .password(passwordEncoder().encode("1111")) // 로그인 비밀번호
                .roles("USER") // 사용자 권한(ADMIN, USER, ...)
                .build();
        
        UserDetails user2 = User.withUsername("user2")
                .password(passwordEncoder().encode("2222"))
                .roles("ADMIN", "USER")
                .build();
        
        UserDetails user3 = User.withUsername("user3")
                .password(passwordEncoder().encode("3333"))
                .roles("ADMIN")
                .build();
        
        return new InMemoryUserDetailsManager(user1, user2, user3);
    }
    
    // 스프링 시큐리티 필터 체인 객체(bean):
    // 로그인/로그아웃 관련 설정.
    // 로그인 페이지(뷰), 로그아웃 페이지(뷰) 설정.
    // 페이지 접근 권한, 인증 설정.(로그인 없이 접근 가능한 페이지/로그인해야만 접근 가능한 페이지)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 기능을 활성화한 경우,
        // Ajax POST/PUT/DELETE 요청에서 csrf 토큰을 서버로 전송하지 않으면 HTTP 403 에러가 발생.
        // -> CSRF(Cross Site Request Forgery) 비활성화
        http.csrf((csrf) -> csrf.disable());
        
        // 로그인 페이지(폼) 설정 - 스프링 시큐리티에서 제공하는 기본 페이지를 이용.
        http.formLogin(Customizer.withDefaults());
        
        // 로그아웃 이후에 이동할 페이지 설정 - 홈 페이지(/)
        http.logout((logout) -> logout.logoutSuccessUrl("/"));
        
        return http.build(); // 스프링 시큐리티 필터 체인 객체를 생성하고 리턴.
    }

}
