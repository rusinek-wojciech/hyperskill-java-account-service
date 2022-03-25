package account.mapper;

import account.dto.payment.GetPaymentDto;
import account.dto.payment.PostPaymentDto;
import account.dto.user.GetUserDto;
import account.model.Payment;
import account.model.user.Role;
import account.model.user.User;
import account.service.UserService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

@Component
public class Mapper {

    public GetUserDto userToGetUserDto(User user) {
        return GetUserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .roles(user.getRoles().stream()
                        .map(Role::getAuthority)
                        .sorted(String::compareTo)
                        .collect(Collectors.toCollection(LinkedHashSet::new)))
                .accountNonLocked(user.getAccountNonLocked())
                .build();
    }

    public Payment postPaymentDtoToPayment(PostPaymentDto postPaymentDto,
                                           UserService userService) {
        User user = userService.loadUserByUsername(emailToUsername(postPaymentDto.getEmployee()));
        return Payment.builder()
                .user(user)
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

    public String emailToUsername(String email) {
        return splitEmail(email)[0];
    }

    public String[] splitEmail(String email) {
        return email.toLowerCase().split("@");
    }
}
