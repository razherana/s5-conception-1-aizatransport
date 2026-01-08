package mg.razherana.aizatransport.models.users;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import mg.razherana.aizatransport.models.bases.BasicEntity;

@Entity
@Data
@Table(name = "users", schema = "public")
public class User extends BasicEntity implements UserDetails {
  @Column(unique = true, nullable = false)
  private String username;

  @Column(nullable = false)
  private String password_hash;

  @Override
  public String getPassword() {
    return password_hash;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return null;
  }
}
