package br.com.rafaelcecyn.gestao_vagas.modules.company.exceptions;

import org.springframework.web.bind.annotation.ControllerAdvice;

import javax.naming.AuthenticationException;

public class AuthenticationHandlerException extends AuthenticationException {
    public AuthenticationHandlerException() {
        super("Password incorrect");
    }
}
