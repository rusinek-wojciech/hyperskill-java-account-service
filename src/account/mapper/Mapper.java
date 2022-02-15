package account.mapper;

import account.dto.PaymentGetDto;
import account.dto.PaymentPostDto;
import account.dto.UserCreateDto;
import account.dto.UserGetDto;
import account.model.Payment;
import account.model.User;
import account.service.UserService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class Mapper {

    public UserGetDto userToUserGetDto(User user) {
        return UserGetDto.builder()
                .id(user.getId())
                .name(user.getName())
                .lastname(user.getLastname())
                .email(user.getUsername())
                .build();
    }

    public User userCreateDtoToUser(UserCreateDto userCreateDto) {
        return User.builder()
                .name(userCreateDto.getName())
                .lastname(userCreateDto.getLastname())
                .username(userCreateDto.getEmail())
                .password(userCreateDto.getPassword())
                .build();
    }


    public Payment paymentPostDtoToPayment(PaymentPostDto paymentPostDto,
                                           UserService userService) {
        return Payment.builder()
                .user(userService.loadUserByUsername(paymentPostDto.getEmployee().toLowerCase()))
                .salary(paymentPostDto.getSalary())
                .period(periodToLocalDate(paymentPostDto.getPeriod()))
                .build();
    }

    public PaymentGetDto paymentToPaymentGetDto(Payment payment) {
        return PaymentGetDto.builder()
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
