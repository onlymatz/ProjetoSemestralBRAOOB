package com.rankitup.backend.config;

import com.rankitup.backend.model.Administrador;
import com.rankitup.backend.model.Equipe;
import com.rankitup.backend.model.Inscricao;
import com.rankitup.backend.model.Jogador;
import com.rankitup.backend.model.Jogo;
import com.rankitup.backend.model.Torneio;
import com.rankitup.backend.model.enums.GeneroJogo;
import com.rankitup.backend.model.enums.PerfilUsuario;
import com.rankitup.backend.model.enums.StatusInscricao;
import com.rankitup.backend.repository.AdministradorRepository;
import com.rankitup.backend.repository.EquipeRepository;
import com.rankitup.backend.repository.InscricaoRepository;
import com.rankitup.backend.repository.JogadorRepository;
import com.rankitup.backend.repository.JogoRepository;
import com.rankitup.backend.repository.TorneioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AdministradorRepository administradorRepository;
    private final JogoRepository jogoRepository;
    private final EquipeRepository equipeRepository;
    private final JogadorRepository jogadorRepository;
    private final TorneioRepository torneioRepository;
    private final InscricaoRepository inscricaoRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${rankitup.seed.enabled:true}")
    private boolean seedEnabled;

    @Value("${rankitup.admin.email:admin@rankitup.com}")
    private String adminEmail;

    @Value("${rankitup.admin.password:admin123}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(String... args) {
        if (!seedEnabled) {
            return;
        }

        Administrador admin = admin();

        Jogo valorant = jogo("Valorant", GeneroJogo.FPS, "Riot Games");
        Jogo dota = jogo("Dota 2", GeneroJogo.MOBA, "Valve");
        Jogo lol = jogo("League of Legends", GeneroJogo.MOBA, "Riot Games");
        Jogo cs2 = jogo("Counter-Strike 2", GeneroJogo.FPS, "Valve");
        Jogo fortnite = jogo("Fortnite", GeneroJogo.BATTLE_ROYALLE, "Epic Games");

        Equipe zenith = equipe("Team Zenith", "TZ", "Brasil");
        Equipe shadow = equipe("Shadow Reapers", "SR", "Brasil");
        Equipe aurora = equipe("Aurora Six", "A6", "Brasil");
        Equipe delta = equipe("Delta Fury", "DFY", "Brasil");
        Equipe pixel = equipe("Pixel Storm", "PXS", "Brasil");

        Jogador luna = jogador("luna@rankitup.com", "Luna Martins", "LunaRush", aurora);
        Jogador kaique = jogador("kaique@rankitup.com", "Kaique Rocha", "KQRaven", delta);
        Jogador bia = jogador("bia@rankitup.com", "Beatriz Nunes", "BiaClutch", pixel);
        Jogador nina = jogador("nina@rankitup.com", "Nina Costa", "NinaByte", shadow);
        Jogador theo = jogador("theo@rankitup.com", "Theo Alves", "TheoTank", zenith);

        Torneio risingStars = torneio("VALORANT RISING STARS", BigDecimal.valueOf(5000), valorant, admin);
        Torneio dotaCup = torneio("DOTA 2 COMMUNITY CUP", BigDecimal.valueOf(2500), dota, admin);
        Torneio lolShowdown = torneio("LEAGUE OF LEGENDS SHOWDOWN", BigDecimal.valueOf(1000), lol, admin);
        Torneio cs2Circuit = torneio("CIRCUITO CS2 PRO-AM", BigDecimal.valueOf(3500), cs2, admin);
        Torneio battleRoyale = torneio("ARENA BATTLE ROYALE", BigDecimal.valueOf(1800), fortnite, admin);

        inscricao(risingStars, luna, aurora, 40, 2, 3);
        inscricao(risingStars, kaique, delta, 20, 1, 3);
        inscricao(dotaCup, nina, shadow, 15, 1, 2);
        inscricao(lolShowdown, theo, zenith, 10, 0, 1);
        inscricao(lolShowdown, bia, pixel, 30, 1, 1);
        inscricao(cs2Circuit, kaique, delta, 25, 1, 2);
        inscricao(cs2Circuit, bia, pixel, 35, 2, 2);
        inscricao(battleRoyale, luna, aurora, 20, 1, 1);
        inscricao(battleRoyale, nina, shadow, 5, 0, 1);
    }

    private Administrador admin() {
        return administradorRepository.findByEmail(adminEmail)
                .orElseGet(() -> {
                    Administrador admin = new Administrador();
                    admin.setEmail(adminEmail);
                    admin.setSenha(passwordEncoder.encode(adminPassword));
                    admin.setPerfil(PerfilUsuario.ROLE_ADMIN);
                    return administradorRepository.save(admin);
                });
    }

    private Jogo jogo(String titulo, GeneroJogo genero, String desenvolvedora) {
        return jogoRepository.findByTitulo(titulo)
                .orElseGet(() -> {
                    Jogo jogo = new Jogo();
                    jogo.setTitulo(titulo);
                    jogo.setGenero(genero);
                    jogo.setDesenvolvedora(desenvolvedora);
                    return jogoRepository.save(jogo);
                });
    }

    private Equipe equipe(String nome, String tag, String pais) {
        return equipeRepository.findByTagEquipe(tag)
                .orElseGet(() -> {
                    Equipe equipe = new Equipe();
                    equipe.setNomeEquipe(nome);
                    equipe.setTagEquipe(tag);
                    equipe.setPaisOrigem(pais);
                    return equipeRepository.save(equipe);
                });
    }

    private Jogador jogador(String email, String nome, String nickname, Equipe equipe) {
        return jogadorRepository.findByEmail(email)
                .or(() -> jogadorRepository.findByNickname(nickname))
                .orElseGet(() -> {
                    Jogador jogador = new Jogador();
                    jogador.setEmail(email);
                    jogador.setSenha(passwordEncoder.encode("123456"));
                    jogador.setPerfil(PerfilUsuario.ROLE_USER);
                    jogador.setNome(nome);
                    jogador.setNickname(nickname);
                    jogador.setEquipe(equipe);
                    return jogadorRepository.save(jogador);
                });
    }

    private Torneio torneio(String nome, BigDecimal premiacaoTotal, Jogo jogo, Administrador criador) {
        return torneioRepository.findByNome(nome)
                .orElseGet(() -> {
                    Torneio torneio = new Torneio();
                    torneio.setNome(nome);
                    torneio.setPremiacaoTotal(premiacaoTotal);
                    torneio.setJogo(jogo);
                    torneio.setCriador(criador);
                    return torneioRepository.save(torneio);
                });
    }

    private void inscricao(Torneio torneio, Jogador jogador, Equipe equipe, int pontos, int vitorias, int partidas) {
        if (inscricaoRepository.existsByTorneio_IdTorneioAndJogador_IdUsuario(
                torneio.getIdTorneio(),
                jogador.getIdUsuario()
        )) {
            return;
        }

        Inscricao inscricao = new Inscricao();
        inscricao.setTorneio(torneio);
        inscricao.setJogador(jogador);
        inscricao.setEquipe(equipe);
        inscricao.setStatus(StatusInscricao.APROVADO);
        inscricao.setPontosAcumulados(pontos);
        inscricao.setVitoriasTotais(vitorias);
        inscricao.setPartidasTotais(partidas);
        inscricaoRepository.save(inscricao);
    }
}
