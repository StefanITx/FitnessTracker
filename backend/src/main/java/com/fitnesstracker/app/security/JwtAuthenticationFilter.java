package com.fitnesstracker.app.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        // Custom logic here
        String header=request.getHeader("Authorization");
        if(header==null || !header.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }
        String token=header.substring(7);
        if(!jwtUtils.isTokenValid(token)){
            filterChain.doFilter(request, response);
            return;
        }
        String email=jwtUtils.extractEmail(token);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=new UsernamePasswordAuthenticationToken(email,null, List.of());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

        filterChain.doFilter(request, response);
    }
}
