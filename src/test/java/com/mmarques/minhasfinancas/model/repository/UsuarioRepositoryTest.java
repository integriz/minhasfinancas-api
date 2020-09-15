package com.mmarques.minhasfinancas.model.repository;

import java.util.Optional;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import com.mmarques.minhasfinancas.model.entity.Usuario;
import com.mmarques.minhasfinancas.model.repository.UsuarioRepository;

//@SpringBootTest comentado para evitar do spring subir todo o contexto do ambiente.
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
//@TestMethodOrder(OrderAnnotation.class) só utilizado se queremos garantir uma ordem de execução dos testes junto com a anotação @Order()

@DataJpaTest //para criar um banco para atuar somente em cada teste.
@AutoConfigureTestDatabase(replace = Replace.NONE) //para que ao criar o banco na memória ele não sobrescreva a configuração feita no banco de teste.
public class UsuarioRepositoryTest {
	
	@Autowired
	UsuarioRepository repository;
	
	@Autowired
	TestEntityManager entityManager;//trata-se de um entitymanager de teste do jpa
	@Test
	//@Order(2)
	public void deveVerificarAExistenciaDeUmEmail() {
		//cenário
		Usuario usuario = criarUsuario();
		//repository.save(usuario);
		entityManager.persist(usuario);
		
		
		//ação/ execução
		boolean result = repository.existsByEmail("usuario@email.com");
		
		//verificacao
		Assertions.assertThat(result).isTrue();
		
	}
	
	@Test
	//@Order(1)
	public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastradoComOEmail() {
		//cenário
		
		//acao
		boolean result = repository.existsByEmail("usuario@email.com");
		
		//verificacao
		Assertions.assertThat(result).isFalse();
	}
	
	
	
	@Test
	public void devePersistirUmUsuarioNaBaseDeDados() {
		//cenário
		Usuario usuario =criarUsuario();
		
		//acao
		Usuario usuarioSalvo = repository.save(usuario);
		
		// verificacao
		Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
	}
	
	@Test
	public void deveBuscarUmUsuarioPorEmail() {
		//cenario
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		
		//verificacao
		Optional<Usuario> result = repository.findByEmail("usuario@email.com");
		
		Assertions.assertThat( result.isPresent() ).isTrue();
		
	}
	
	@Test
	public void deveRetornarVazioAoBuscarUsuarioPorEmailQuandoNaoExisteNaBase() {
		
		//verificacao
		Optional<Usuario> result = repository.findByEmail("usuario@email.com");
		
		Assertions.assertThat( result.isPresent() ).isFalse();
		
	}
	
	public static Usuario criarUsuario() {
		return Usuario
				.builder()
				.nome("usuario")
				.email("usuario@email.com")
				.senha("senha")
				.build();
	}

}
