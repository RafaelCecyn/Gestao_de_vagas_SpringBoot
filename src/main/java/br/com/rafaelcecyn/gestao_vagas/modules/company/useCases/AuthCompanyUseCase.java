package br.com.rafaelcecyn.gestao_vagas.modules.company.useCases;

import br.com.rafaelcecyn.gestao_vagas.modules.company.dto.AuthCompanyDTO;
import br.com.rafaelcecyn.gestao_vagas.modules.company.exceptions.AuthenticationHandlerException;
import br.com.rafaelcecyn.gestao_vagas.modules.company.repositories.CompanyRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.auth0.jwt.JWT;

import java.time.Duration;
import java.time.Instant;

@Service
public class AuthCompanyUseCase {

    @Value("${security.token.secret}")
    private String secretkey;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String execute(AuthCompanyDTO authCompanyDTO) throws Exception{
        var company = this.companyRepository.findByUsername(authCompanyDTO.getUsername())
                .orElseThrow(
                        () -> {
                            throw new UsernameNotFoundException("Username/password incorrect");
                        }
                );
        var passwordMatches = this.passwordEncoder.matches(authCompanyDTO.getPassword(),company.getPassword());

        if(!passwordMatches) {
            throw new AuthenticationHandlerException();
        }
        Algorithm algorithm = Algorithm.HMAC256(secretkey);
        var token = JWT.create().withIssuer("javagas")
                .withExpiresAt(Instant.now().plus(Duration.ofHours(2)))
                .withSubject(company.getId().toString())
                .sign(algorithm);
        return token;
    }
}
