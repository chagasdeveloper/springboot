package curso.springboot.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.security.core.GrantedAuthority;

@Entity
public class Role implements GrantedAuthority {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String nomeRole;

	@Override
	public String getAuthority() { // ROLE_ADMIN, ROLE_GERENTE, ROLE_SECRETARIO
		return nomeRole;
	}

	public String getNomeRoleString() {
		return nomeRole;
	}

	public void setNomeRoleString(String nomeRoleString) {
		this.nomeRole = nomeRoleString;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Role [id=" + id + ", nomeRole=" + nomeRole + "]";
	}
	
	
}
