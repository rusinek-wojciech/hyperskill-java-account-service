package account.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;

public class BreachedPasswordValidator
        implements ConstraintValidator<BreachedPassword, String> {

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

    @Override
    public boolean isValid(String password, ConstraintValidatorContext ctx) {
        if (password == null) return true;
        return !breachedPasswords.contains(password);
    }
}
