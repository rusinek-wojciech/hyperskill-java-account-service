package account.repository;

import account.model.Payment;
import account.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findPaymentsByUser(User user);

    Optional<Payment> findPaymentByUserAndPeriod(User user, LocalDate period);

    @Modifying
    @Query("UPDATE Payment p SET p.salary = :salary WHERE p.period = :period AND p.user = :user")
    void updatePaymentByUserAndPeriod(@Param("user") User user,
                                      @Param("period") LocalDate period,
                                      @Param("salary") Long salary);
}
