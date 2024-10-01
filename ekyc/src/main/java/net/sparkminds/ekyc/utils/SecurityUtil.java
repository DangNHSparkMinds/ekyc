package net.sparkminds.ekyc.utils;

import jakarta.servlet.http.HttpServletRequest;
import net.sparkminds.ekyc.constant.Constants;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class SecurityUtil {
    private static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    public static Long getCurrentUser() {
        HttpServletRequest currentRequestContext = getRequest();
        if (currentRequestContext == null) {
            return null;
        }
        return (Long) currentRequestContext.getAttribute(Constants.HEADER_USER_ID);
    }
}
