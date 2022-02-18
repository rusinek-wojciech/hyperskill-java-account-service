package account.dto.payment;

import javax.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostPaymentDto {

    @NotNull(message = "Employee cannot be null")
    @NotBlank(message = "Employee cannot be blank")
    @Email(message = "Employee invalid format")
    @Pattern(regexp = ".+@acme.com", message = "Employee invalid format")
    private String employee;

    @NotNull(message = "Period cannot be null")
    @Pattern(regexp = "(0?[1-9]|1[0-2])-\\d+", message = "Period invalid format")
    private String period;

    @NotNull(message = "Salary cannot be null")
    @PositiveOrZero(message = "Salary cannot be negative")
    private Long salary;

}
