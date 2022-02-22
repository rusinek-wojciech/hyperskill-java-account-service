package account.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BreachedPasswordValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BreachedPassword {
    String message() default "The password is in the hacker's database!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
