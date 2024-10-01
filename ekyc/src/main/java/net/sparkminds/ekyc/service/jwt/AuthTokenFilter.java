package net.sparkminds.ekyc.service.jwt;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.sparkminds.ekyc.constant.Constants;
import net.sparkminds.ekyc.entity.UserSession;
import net.sparkminds.ekyc.exception.NotFoundException;
import net.sparkminds.ekyc.repository.UserSessionRepository;
import net.sparkminds.ekyc.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @PostConstruct
    public void init() {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader(Constants.HEADER_TOKEN);

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7, headerAuth.length());
        }

        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String accessToken = parseJwt(request);
            if (accessToken != null && JwtUtils.validateJwtToken(accessToken)) {
                Optional<UserSession> userSessionOptional = userSessionRepository.findByToken(accessToken);
                if (userSessionOptional.isEmpty()) {
                    throw new NotFoundException("Invalid token");
                }

                if (userSessionOptional.get().getRevoked()) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
                String email = JwtUtils.getClaimFromJwtToken(accessToken, "email");
                String phoneNumber = JwtUtils.getClaimFromJwtToken(accessToken, "phoneNumber");
                String emailOrPhoneNumber = (email == null) ? phoneNumber : email;
                setAuth(emailOrPhoneNumber, request);
                request.setAttribute(Constants.HEADER_USER_ID, JwtUtils.getUserIdFromJwtToken(accessToken));
            } else {
                filterChain.doFilter(request, response);
                return;
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e);
        }
        filterChain.doFilter(request, response);
    }

    public void setAuth(String emailOrPhoneNumber, HttpServletRequest request) {
        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(emailOrPhoneNumber);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,
                null, userDetails.getAuthorities());

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

}
