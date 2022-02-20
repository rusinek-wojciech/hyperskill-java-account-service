package account.validator;

import account.dto.payment.PostPaymentDto;
import account.exception.ValidException;
import account.model.Payment;
import account.model.user.Role;
import account.model.user.User;
import account.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;

public class Validators {

    private static final Set<String> breachedPasswords = Set.of(
            "PasswordForJanuary",
            "PasswordForFebruary",
            "PasswordForMarch",
            "PasswordForApril",
            "PasswordForMay",
            "PasswordForJune",
            "PasswordForJuly",
            "PasswordForAugust",
            "PasswordForSeptember",
            "PasswordForOctober",
            "PasswordForNovember",
            "PasswordForDecember"
    );

    public static void validatePeriod(String period) {
        if (period == null) {
            throw new ValidException("Period cannot be null");
        }
        if (!period.matches("(0?[1-9]|1[0-2])-\\d+")) {
            throw new ValidException("Period invalid format");
        }
    }

    public static void validatePasswordBreached(String password) {
        if (breachedPasswords.contains(password)) {
            throw new ValidException("The password is in the hacker's database!");
        }
    }

    public static void validatePasswordSame(String password, User user, PasswordEncoder passwordEncoder) {
        if (passwordEncoder.matches(password, user.getPassword())) {
            throw new ValidException("The passwords must be different!");
        }
    }

    public static void validatePaymentPeriodExist(List<Payment> payments, Payment payment) {
        boolean periodNotExist = payments.stream()
                .map(Payment::getPeriod)
                .noneMatch(d -> d.equals(payment.getPeriod()));
        if (periodNotExist) {
            throw new ValidException("Provided period data does not exist");
        }
    }

    public static void validateIsOneUser(List<PostPaymentDto> payments) {
        long users = payments.stream()
                .map(PostPaymentDto::getEmployee)
                .map(String::toLowerCase)
                .distinct()
                .count();
        if (users == 1) {
            throw new ValidException("Should be only one employee");
        }
    }

    /**
     * @param payments must implement equals and hashcode by username and period
     */
    public static void validateDistinctPeriodUserPairs(List<Payment> payments) {
        long distinctCount = payments.stream().distinct().count();
        if (distinctCount != payments.size()) {
            throw new ValidException("Employee-period pair must be unique");
        }
    }

    public static void validateRemoveUserRole(Set<Role> roles, Role roleToRemove) {
        if (roleToRemove == Role.ADMINISTRATOR) {
            throw new ValidException("Can't remove ADMINISTRATOR role!");
        }
        if (!roles.contains(roleToRemove)) {
            throw new ValidException("The user does not have a role!");
        }
        if (roles.size() == 1) {
            throw new ValidException("The user must have at least one role!");
        }
    }

}
