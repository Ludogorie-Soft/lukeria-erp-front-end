package com.example.LukeriaFrontendApplication.session;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class SessionManager {

    public void setSessionToken(HttpServletRequest request, String sessionToken) {
        String token = "Bearer " + sessionToken;

        HttpSession session = request.getSession(true);
        session.setAttribute("sessionToken", token);
    }

    public void invalidateSession(HttpServletRequest request ){
        HttpSession session = request.getSession(false);
        if(session!= null){
            session.invalidate();
        }
    }

}
