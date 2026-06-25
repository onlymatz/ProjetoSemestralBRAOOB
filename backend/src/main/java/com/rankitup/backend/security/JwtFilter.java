package com.rankitup.backend.security;



import jakarta.servlet.FilterChain;

import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.lang.NonNull;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;



import java.io.IOException;

import java.util.List;



@Component

@RequiredArgsConstructor

public class JwtFilter extends OncePerRequestFilter {



    private final JwtService jwtService;



    @Override

    protected void doFilterInternal(@NonNull HttpServletRequest request,

                                    @NonNull HttpServletResponse response,

                                    @NonNull FilterChain filterChain)

            throws ServletException, IOException {



        String header = request.getHeader("Authorization");



        if (header == null || !header.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);

            return;

        }



        String token = header.substring(7);



        if (jwtService.tokenValido(token)) {

            String email = jwtService.extrairEmail(token);

            String perfil = jwtService.extrairPerfil(token);



            var auth = new UsernamePasswordAuthenticationToken(

                    email,

                    null,

                    List.of(new SimpleGrantedAuthority(perfil))

            );

            SecurityContextHolder.getContext().setAuthentication(auth);

        }



        filterChain.doFilter(request, response);

    }

}
