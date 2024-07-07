package br.com.rafaelcecyn.gestao_vagas.modules.candidate.useCases;

import br.com.rafaelcecyn.gestao_vagas.modules.candidate.CandidateRepository;
import br.com.rafaelcecyn.gestao_vagas.modules.candidate.dto.AuthCandidateRequestDTO;
import br.com.rafaelcecyn.gestao_vagas.modules.candidate.dto.AuthCandidateResponseDTO;
import br.com.rafaelcecyn.gestao_vagas.modules.candidate.exceptions.UserFoundException;
import br.com.rafaelcecyn.gestao_vagas.modules.company.exceptions.AuthenticationHandlerException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

@Service
public class AuthCandidateUseCase {
    @Value("${security.token.secret.candidate}")
    private String secretKey;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthCandidateResponseDTO execute(AuthCandidateRequestDTO authCandidateRequestDTO) {
        var candidate = this.candidateRepository.findByUsername(authCandidateRequestDTO.username())
                .orElseThrow(() -> {
                    throw new UsernameNotFoundException("Username/password incorrect");
                });
        var passwordMatches = this.passwordEncoder.matches(authCandidateRequestDTO.password(),
                candidate.getPassword());

        if(!passwordMatches) {
            try {
                throw new AuthenticationHandlerException();
            } catch (AuthenticationHandlerException e) {
                throw new RuntimeException(e);
            }
        }
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        var expires_in = Instant.now().plus(Duration.ofMinutes(10));
        var token = JWT.create().withIssuer("javagas").withSubject(candidate.getId().toString())
                .withClaim("roles", Arrays.asList("CANDIDATE"))
                .withExpiresAt(expires_in)
                .sign(algorithm);

        var authCandidateResponse = AuthCandidateResponseDTO.builder().access_token(token)
        .expires_in(expires_in.toEpochMilli()).build();
        return authCandidateResponse;
    }
}
