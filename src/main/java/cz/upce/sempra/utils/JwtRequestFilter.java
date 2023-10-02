package cz.upce.sempra.utils;

import cz.upce.sempra.services.JwtUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import liquibase.repackaged.org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    private static final List<String> NON_SECURED_ENDPOINTS = Arrays.asList(
            "/auth/login",
            "/auth/register",
            "/auth/reset",
            "/auth/new_password"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (isNonSecuredEndpoint(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        final String requestTokenHeader = request.getHeader("Authorization");
        if (StringUtils.startsWith(requestTokenHeader, "Bearer ")) {
            String jwtToken = requestTokenHeader.substring(7);
            try {
                String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                if (StringUtils.isNotEmpty(username) && null == SecurityContextHolder.getContext().getAuthentication()) {
                    UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
                    if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                        usernamePasswordAuthenticationToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                }
            } catch (IllegalArgumentException e) {
                logger.error("Unable to fetch JWT Token.");
            } catch (ExpiredJwtException e) {
                logger.error("JWT Token is expired.");
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        } else {
            logger.warn("JWT Token does not begin with Bearer String.");
        }
        chain.doFilter(request, response);
    }

    private boolean isNonSecuredEndpoint(String requestURI) {
        return NON_SECURED_ENDPOINTS.stream()
                .anyMatch(requestURI::startsWith);
    }
}