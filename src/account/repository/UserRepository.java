package account.repository;

import account.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    long deleteByUsername(String username);
    
    @Modifying
    @Query("UPDATE User user SET user.password = :password WHERE user.username = :username")
    void updatePassword(@Param("password") String password, @Param("username") String username);

}
