package account.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateLockUserDto {

    @NotNull(message = "User cannot be null")
    @NotBlank(message = "User cannot be blank")
    private String user;

    @NotNull(message = "Operation cannot be null")
    @NotBlank(message = "Operation cannot be blank")
    private String operation;

    public enum Operation {
        LOCK, UNLOCK
    }
}
