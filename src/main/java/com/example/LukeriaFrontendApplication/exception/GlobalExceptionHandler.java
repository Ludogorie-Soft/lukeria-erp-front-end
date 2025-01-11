package com.example.LukeriaFrontendApplication.exception;

import com.example.LukeriaFrontendApplication.slack.SlackService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RequiredArgsConstructor
@ControllerAdvice
public class GlobalExceptionHandler {

    private final SlackService slackService;

    @ExceptionHandler(Exception.class)
    public String alertSlackChannelWhenUnexpectedErrorOccurs(Exception ex, HttpServletRequest request) {
        feign.FeignException feignException = (feign.FeignException) ex;
        if (feignException.status() == HttpStatus.FORBIDDEN.value()) {
            String originalUrl = request.getRequestURI();
            request.getSession().setAttribute("redirectAfterLogin", originalUrl);

            return "redirect:/login?message=loginRequired";
        }

        slackService.publishMessage("lukeria-notifications",
                "Error occurred from the FRONTEND application -> " + ex.getMessage());

        request.setAttribute("javax.servlet.error.status_code", HttpStatus.INTERNAL_SERVER_ERROR.value());
        request.setAttribute("javax.servlet.error.message", ex.getMessage());

        return "forward:/error";
    }
}
