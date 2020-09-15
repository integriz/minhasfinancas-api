package com.mmarques.minhasfinancas.service;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mmarques.minhasfinancas.exception.ErroAutenticacao;
import com.mmarques.minhasfinancas.exception.RegraNegocioException;
import com.mmarques.minhasfinancas.model.entity.Usuario;
import com.mmarques.minhasfinancas.model.repository.UsuarioRepository;
import com.mmarques.minhasfinancas.service.impl.UsuarioServiceImpl;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

	@SpyBean
	UsuarioServiceImpl service;
	
	@MockBean
	UsuarioRepository repository;
	
	@Test
	public void deveSalvarUmUsuario() {
		//cenário
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		Usuario usuario = Usuario.builder()
					.id(1l)
					.nome("nome")
					.email("email@email.com")
					.senha("senha").build();
		
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		//acao
		Usuario usuarioSalvo = service.salvarUsuario(new Usuario());
		
		//verificao
		Assertions.assertNotNull(usuarioSalvo);
		Assertions.assertEquals(1l,usuarioSalvo.getId());
		Assertions.assertEquals("nome",usuarioSalvo.getNome());
		Assertions.assertEquals("email@email.com",usuarioSalvo.getEmail());
		Assertions.assertEquals("senha",usuarioSalvo.getSenha());
		
	}
	
	@Test
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
		//cenario
		String email = "email@email.com";
		Usuario usuario = Usuario.builder().email(email).build();
		Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);
		
		//acao
		Assertions.assertThrows(RegraNegocioException.class, () -> service.salvarUsuario(usuario) ) ;
		
		//verificacao
		Mockito.verify( repository, Mockito.never() ).save(usuario);
	}
	
	@Test
	public void deveAutenticarUmUsuarioComSucesso() {
		//cenário
		String email = "email@email.com";
		String senha = "senha";
		
		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
		Mockito.when( repository.findByEmail(email) ).thenReturn(Optional.of(usuario));
		
		//acao
		Usuario result = service.autenticar(email, senha);
		
		//verificacao
		Assertions.assertNotNull(result);
		
	}
	
	@Test
	public void deveLancarErroQUandoNaoEncontrarUsuarioCadastradoComOEmailInformado() {
		
		//cenário
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		
		//acao e verificacao
		Throwable exception  = Assertions.assertThrows(ErroAutenticacao.class,() -> service.autenticar("email@email.com", "123") ,"Usuário não encontrado para o email informado.");

	
		
	}
	
	@Test
	public void deveLancarErroQuandoSenhaNaoBater() {
		//cenario
		String senha = "senha";
		Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
		
		//acao e verificão
		Throwable exception  = Assertions.assertThrows(ErroAutenticacao.class,() -> service.autenticar("email@email.com", "123") ,"Senha inválida.");
	}
	
	@Test
	public void deveValidarEmail() {
		// cenario
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
		
		//acao
		service.validarEmail("email@email.com");
	}
	
	@Test
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
		//cenario
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
		
		//acao
		Assertions.assertThrows(RegraNegocioException.class, () -> service.validarEmail("email@email.com"));
	}
}
