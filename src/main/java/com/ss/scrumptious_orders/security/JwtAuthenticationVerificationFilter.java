package com.ss.scrumptious_orders.security;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JwtAuthenticationVerificationFilter extends BasicAuthenticationFilter {
    private final SecurityConstants securityConstants;

    public JwtAuthenticationVerificationFilter(AuthenticationManager authenticationManager,
                                               SecurityConstants securityConstants) {
        super(authenticationManager);
        this.securityConstants = securityConstants;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(securityConstants.getHEADER_STRING());

        // If header does not contain BEARER or is null delegate to Spring impl and exit
        if (header == null || !header.startsWith(securityConstants.getTOKEN_PREFIX())) {
            chain.doFilter(request, response);
            return;
        }

        // If header is present, try grab user principal from database and perform authorization
        Authentication authentication = getAuthenticationToken(request);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Continue filter execution
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthenticationToken(HttpServletRequest request){
    	 String token = request.getHeader(securityConstants.getHEADER_STRING())
                 .replace(securityConstants.getTOKEN_PREFIX(), "");
         if (token == null) {
             return null;
         }

         DecodedJWT jwt = JWT.require(HMAC512(securityConstants.getSECRET().getBytes()))
                 .build()
                 .verify(token);
         
         // parse the token and validate it
         String userName = jwt.getSubject();
  
         if (userName == null){
             return null;
         }
         
         List<SimpleGrantedAuthority> authorities = Arrays.asList(
         		jwt.getClaim(securityConstants.getAUTHORITY_CLAIM_KEY()).asString().split(","))
         		.stream()
         		.map(s -> new SimpleGrantedAuthority(s))
         		.collect(Collectors.toList());
         
         String userId = jwt.getClaim(securityConstants.getUSER_ID_CLAIM_KEY()).asString();
         
         JwtPrincipalModel jwtPrincipalModel = JwtPrincipalModel.builder()
         		.username(userName)
         		.userId(UUID.fromString(userId))
         		.build();
       
         return new UsernamePasswordAuthenticationToken(jwtPrincipalModel, null, authorities);
    }
}