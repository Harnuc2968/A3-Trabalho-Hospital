package com.clinica.demo.repository;

import com.clinica.demo.model.Medico;
import com.clinica.demo.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {
    Optional<Medico> findByEmailAndSenha(String email, String senha);
    Optional<Medico> findByEmail(String email);
    List<Medico> findByDisponivel(boolean disponivel); // Busca médicos com base na disponibilidade
    @Query(value = "SELECT * FROM medico ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Medico> findRandomMedico();

}
