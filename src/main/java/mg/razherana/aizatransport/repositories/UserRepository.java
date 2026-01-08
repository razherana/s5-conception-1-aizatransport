package mg.razherana.aizatransport.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.razherana.aizatransport.models.users.User;

public interface UserRepository extends JpaRepository<User, Integer> {

  Optional<User> findByUsername(String username);
}
