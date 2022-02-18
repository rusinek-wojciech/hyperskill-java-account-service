package account.dto.user;

import account.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateRoleUserDto {

    private String user;
    private Role role;
    private Operation operation;

    public enum Operation {
        GRANT, REMOVE
    }
}
