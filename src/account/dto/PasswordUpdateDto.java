package account.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordUpdateDto {

    @NotNull
    @NotEmpty
    @Size(min = 12, message = "The password length must be at least 12 chars!")
    private String newPassword;
}
