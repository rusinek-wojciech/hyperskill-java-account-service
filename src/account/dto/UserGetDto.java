package account.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserGetDto {

    private Long id;
    private String name;
    private String lastname;
    private String email;

}
