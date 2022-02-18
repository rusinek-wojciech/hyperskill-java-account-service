package account.mapper;

import account.dto.payment.GetPaymentDto;
import account.dto.payment.PostPaymentDto;
import account.dto.user.CreateUserDto;
import account.dto.user.GetUserDto;
import account.model.Payment;
import account.model.Role;
import account.model.User;
import account.repository.RoleRepository;
import account.service.UserService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Component
public class Mapper {

    public GetUserDto userToGetUserDto(User user) {
        return GetUserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .lastname(user.getLastname())
                .email(user.getUsername())
                .roles(user.getRoles())
                .build();
    }


    public User createUserDtoToUser(CreateUserDto createUserDto,
                                    Set<Role> roles,
                                    RoleRepository roleRepository) {
        return User.builder()
                .name(createUserDto.getName())
                .lastname(createUserDto.getLastname())
                .username(createUserDto.getEmail())
                .password(createUserDto.getPassword())
                .roles(roles, roleRepository)
                .build();
    }

    public Payment postPaymentDtoToPayment(PostPaymentDto postPaymentDto,
                                           UserService userService) {
        return Payment.builder()
                .user(userService.loadUserByUsername(postPaymentDto.getEmployee().toLowerCase()))
                .salary(postPaymentDto.getSalary())
                .period(periodToLocalDate(postPaymentDto.getPeriod()))
                .build();
    }

    public GetPaymentDto paymentToGetPaymentDto(Payment payment) {
        return GetPaymentDto.builder()
                .name(payment.getUser().getName())
                .lastname(payment.getUser().getLastname())
                .period(DateTimeFormatter.ofPattern("MMMM-yyyy").format(payment.getPeriod()))
                .salary(String.format("%d dollar(s) %d cent(s)", payment.getSalary() / 100, payment.getSalary() % 100))
                .build();
    }

    /**
     * @param period in format {month_number}-{year}
     * @return LocalDate with month and year
     */
    public LocalDate periodToLocalDate(String period) {
        String[] data = period.split("-");
        int month = Integer.parseInt(data[0]);
        int year = Integer.parseInt(data[1]);
        return LocalDate.of(year, month, 1);
    }
}
