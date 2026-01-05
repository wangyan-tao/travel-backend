package com.qingchun.travelloan.security;

import com.qingchun.travelloan.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * JWT认证过滤器
 * 
 * @author Qingchun Team
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${jwt.prefix}")
    private String tokenPrefix;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        
        String header = request.getHeader(tokenHeader);
        
        if (header != null && header.startsWith(tokenPrefix)) {
            String token = header.substring(tokenPrefix.length()).trim();
            
            if (jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.getUserIdFromToken(token);
                String username = jwtUtil.getUsernameFromToken(token);
                String role = jwtUtil.getRoleFromToken(token);
                
                if (userId != null && username != null) {
                    // 创建认证对象
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                            userId, 
                            null, 
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                        );
                    
                    // 设置到Security上下文
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        
        chain.doFilter(request, response);
    }
}
