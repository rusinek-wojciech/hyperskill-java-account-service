package account.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordUpdateDto {

    @NotNull(message = "New password cannot be null")
    @NotBlank(message = "New password cannot be blank")
    @Size(min = 12, message = "Password length must be 12 chars minimum!")
    private String newPassword;
}
