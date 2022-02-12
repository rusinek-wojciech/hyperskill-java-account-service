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
    @Size(min = 12, message = "The password length must be at least 12 chars!")
    private String password;

}
