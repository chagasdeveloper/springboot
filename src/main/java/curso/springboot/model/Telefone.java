package curso.springboot.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.ForeignKey;

@SuppressWarnings("deprecation")
@Entity
public class Telefone implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@NotNull(message = "O número não pode ser nulo!")
	@NotEmpty(message = "O número não pode ser vazio!")
	@Max(value = 999999999, message = "número Inválido!")
	@Min(value = 910000000, message = "Digite um número válido")
	private String numero;
	@NotNull(message = "O Tipo não pode ser nulo!")
	@NotEmpty(message = "O Tipo não pode ser vazio!")
	private String tipo;
	@ForeignKey(name = "pessoa_id")
	@ManyToOne
	private Pessoa pessoa;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

}
