package br.com.rafaelcecyn.gestao_vagas.modules.candidate.useCases;

import br.com.rafaelcecyn.gestao_vagas.modules.candidate.CandidateEntity;
import br.com.rafaelcecyn.gestao_vagas.modules.candidate.CandidateRepository;
import br.com.rafaelcecyn.gestao_vagas.modules.candidate.exceptions.UserFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CreateCandidateUseCase {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CandidateRepository candidateRepository;

    public CandidateEntity execute(CandidateEntity candidateEntity) {
        this.candidateRepository.findByUsernameOrEmail(candidateEntity.getUsername(),candidateEntity.getEmail()).ifPresent(
                (user) -> {
                    throw new UserFoundException();
                }
        );
        var password = this.passwordEncoder.encode(candidateEntity.getPassword());
        candidateEntity.setPassword(password);

        return this.candidateRepository.save(candidateEntity);
    }
}
