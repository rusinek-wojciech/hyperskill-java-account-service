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
    @Size(min = 12, message = "Password length must be 12 chars minimum!")
    private String newPassword;
}
