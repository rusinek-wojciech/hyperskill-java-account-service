package account.dto.user;

import lombok.*;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetUserDto {

    private Long id;
    private String name;
    private String lastname;
    private String email;
    private Set<String> roles;

}
