package account.dto;

import lombok.*;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreateDto {

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @NotEmpty
    private String lastname;

    @NotNull
    @NotEmpty
    @Email
    @Pattern(regexp = ".+@acme.com")
    private String email;

    @NotNull
    @NotEmpty
    @Size(min = 12, message = "Password length must be 12 chars minimum!")
    private String password;

}
