package com.clinica.demo.controller;

import com.clinica.demo.model.Atendimento;
import com.clinica.demo.model.Paciente;
import com.clinica.demo.repository.AtendimentoRepository;
import com.clinica.demo.repository.MedicoRepository;
import com.clinica.demo.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.clinica.demo.model.Medico;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/atendimento")
public class AtendimentoController {

    @Autowired
    private AtendimentoRepository atendimentoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    private Random random = new Random();

    // Adicionar paciente para consulta
    @PostMapping("/consulta")
    public Paciente adicionarConsulta() {
        return gerarPaciente("consulta", false);
    }

    // Adicionar paciente para emergência
    @PostMapping("/emergencia")
    public Paciente adicionarEmergencia() {
        // Encontrar a última posição de emergência atual
        long ultimaPosicaoEmergencia = pacienteRepository.findAllByTipoAtendimento("emergencia", Sort.by(Sort.Direction.ASC, "posicaoNaFila"))
                .stream()
                .mapToLong(Paciente::getPosicaoNaFila)
                .max()
                .orElse(0L);

        // Criar um novo paciente de emergência e colocá-lo na primeira posição
        Paciente pacienteEmergencia = gerarPaciente("emergencia", true);
        pacienteEmergencia.setPosicaoNaFila(ultimaPosicaoEmergencia + 1);
        pacienteRepository.save(pacienteEmergencia);

        // Atualizar as posições dos demais pacientes (consulta e coleta)
        atualizarPosicoesNaoEmergencia(pacienteEmergencia.getPosicaoNaFila());

        return pacienteEmergencia;
    }

    // Adicionar paciente para coleta
    @PostMapping("/coleta")
    public Paciente adicionarColeta() {
        return gerarPaciente("coleta", false);
    }

    // Método para atualizar as posições dos pacientes que não são emergência
    private void atualizarPosicoesNaoEmergencia(long novaPosicaoInicial) {
        List<Paciente> pacientesNaoEmergencia = pacienteRepository.findAllByTipoAtendimentoNot("emergencia", Sort.by(Sort.Direction.ASC, "posicaoNaFila"));

        long novaPosicao = novaPosicaoInicial + 1;
        for (Paciente paciente : pacientesNaoEmergencia) {
            paciente.setPosicaoNaFila(novaPosicao);
            novaPosicao++;
            pacienteRepository.save(paciente);
        }
    }

    // Método para gerar paciente
    private Paciente gerarPaciente(String tipoAtendimento, boolean prioridadeAlta) {
        // Cria um novo atendimento
        Atendimento atendimento = new Atendimento(tipoAtendimento);
        atendimento.setPrioridade(prioridadeAlta ? 1 : 0);
        atendimentoRepository.save(atendimento);

        // Gerar número de paciente sequencial
        long totalPacientes = pacienteRepository.count();
        String numeroPaciente = "P" + String.format("%04d", totalPacientes + 1); // Total + 1 garante sequência correta

        // Define uma sala aleatória
        String[] salas = {"Sala 1", "Sala 2", "Sala 3", "Sala 4"};
        String sala = salas[random.nextInt(salas.length)];

        // Seleciona um médico aleatório do banco de dados
        String nomeMedico = medicoRepository.findRandomMedico()
                .map(Medico::getNome)
                .orElse("Nenhum Médico Disponível");

        // Cria um novo paciente
        Paciente paciente = new Paciente();
        paciente.setNumeroPaciente(numeroPaciente);
        paciente.setSituacao(tipoAtendimento);
        paciente.setSala(sala);
        paciente.setPosicaoNaFila(totalPacientes + 1L); // Última posição na fila
        paciente.setMedico(nomeMedico);
        paciente.setTipoAtendimento(tipoAtendimento);

        // Salva o paciente no repositório
        return pacienteRepository.save(paciente);
    }

    @PostMapping("/chamarProximo")
    public Paciente chamarProximoPaciente() {
        List<Paciente> fila = pacienteRepository.findAllBySituacaoNot("Atendido", Sort.by(Sort.Direction.ASC, "posicaoNaFila"));
        if (fila.isEmpty()) {
            throw new RuntimeException("Nenhum paciente na fila.");
        }
        Paciente proximoPaciente = fila.get(0);
        proximoPaciente.setSituacao("Atendido");
        pacienteRepository.save(proximoPaciente);

        // Ajustar posições após chamar o próximo paciente
        ajustarPosicoes();

        return proximoPaciente;
    }
    @GetMapping("/paciente/{id}")
    public ResponseEntity<Paciente> obterPaciente(@PathVariable Long id) {
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(id);
        if (pacienteOpt.isPresent()) {
            Paciente paciente = pacienteOpt.get();
            return ResponseEntity.ok(paciente);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    // Retornar a fila de atendimentos
    @GetMapping("/fila")
    public List<Paciente> obterFila() {
        return pacienteRepository.findAll(Sort.by(Sort.Direction.ASC, "posicaoNaFila"));
    }
    @GetMapping("/pacientes/{tipoAtendimento}")
    public ResponseEntity<List<Paciente>> listarPacientesPorTipo(@PathVariable String tipoAtendimento) {
        List<Paciente> pacientes = pacienteRepository.findByTipoAtendimento(tipoAtendimento, Sort.by(Sort.Direction.ASC, "posicaoNaFila"));
        return ResponseEntity.ok(pacientes);
    }
    private void ajustarPosicoes() {
        List<Paciente> fila = pacienteRepository.findAllBySituacaoNot("Atendido", Sort.by(Sort.Direction.ASC, "posicaoNaFila"));
        long posicao = 1;
        for (Paciente paciente : fila) {
            paciente.setPosicaoNaFila(posicao++);
            pacienteRepository.save(paciente);
        }
    }
    @GetMapping("/dados")
    public ResponseEntity<?> obterDadosGerais() {
        List<String> numerosPacientes = pacienteRepository.findAllNumeroPaciente();
        String numeroAtual = numerosPacientes.isEmpty() ? "N/A" : numerosPacientes.get(numerosPacientes.size() - 1);
        Paciente ultimaEmergencia = pacienteRepository.findFirstByTipoAtendimentoOrderByPosicaoNaFilaDesc("emergencia").orElse(null);
        Paciente ultimaConsulta = pacienteRepository.findFirstByTipoAtendimentoOrderByPosicaoNaFilaDesc("consulta").orElse(null);
        Paciente ultimaColeta = pacienteRepository.findFirstByTipoAtendimentoOrderByPosicaoNaFilaDesc("coleta").orElse(null);

        return ResponseEntity.ok(Map.of(
                "numeroAtual", numeroAtual,
                "situacaoAtual", !numerosPacientes.isEmpty() ? "Em Andamento" : "Nenhuma Situação",
                "ultimaEmergencia", ultimaEmergencia != null ? ultimaEmergencia.getNumeroPaciente() : "N/A",
                "ultimaConsulta", ultimaConsulta != null ? ultimaConsulta.getNumeroPaciente() : "N/A",
                "ultimaColeta", ultimaColeta != null ? ultimaColeta.getNumeroPaciente() : "N/A"
        ));
    }


}
