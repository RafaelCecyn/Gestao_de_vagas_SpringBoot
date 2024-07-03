package br.com.rafaelcecyn.gestao_vagas.modules.company.useCases;

import br.com.rafaelcecyn.gestao_vagas.modules.company.dto.AuthCompanyDTO;
import br.com.rafaelcecyn.gestao_vagas.modules.company.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthCompanyUseCase {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void execute(AuthCompanyDTO authCompanyDTO) throws Exception{
        var company = this.companyRepository.findByUsername(authCompanyDTO.getUsername())
                .orElseThrow(
                        () -> {
                            throw new UsernameNotFoundException("Company not found");
                        }
                );
        var passwordMatches = this.passwordEncoder.matches(authCompanyDTO.getUsername(),company.getUsername());

        if(!passwordMatches) {
            throw new AuthenticationException();
        }
    }
}
