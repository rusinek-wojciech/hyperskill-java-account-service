package account.service;

import account.dto.PaymentGetDto;
import account.dto.PaymentPostDto;
import account.dto.PaymentStatusDto;
import account.mapper.Mapper;
import account.model.Payment;
import account.model.User;
import account.repository.PaymentRepository;
import account.validator.Validators;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class BusinessService {

    private final PaymentRepository paymentRepository;
    private final UserService userService;
    private final Mapper mapper;

    public List<PaymentGetDto> getUserPayments(User user) {
        log.info("Getting payments for \"" + user.getUsername() + "\"");
        return paymentRepository.findPaymentsByUser(user)
                .stream()
                .map(mapper::paymentToPaymentGetDto)
                .collect(Collectors.toList());
    }

    public PaymentGetDto getUserPaymentByPeriod(User user, String period) {
        log.info("Getting payment for \"" + user.getUsername() + "\" at \"" + period + "\"");
        LocalDate periodDate = mapper.periodToLocalDate(period);
        return paymentRepository.findPaymentByUserAndPeriod(user, periodDate)
                .stream()
                .map(mapper::paymentToPaymentGetDto)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find resource"));
    }

    public PaymentStatusDto uploadPayment(List<PaymentPostDto> paymentPostDtoList) {
        log.info("Uploading payment \"" + paymentPostDtoList + "\"");

        Validators.validateIsOneUser(paymentPostDtoList);
        String username = paymentPostDtoList.get(0).getEmployee().toLowerCase();
        User user = userService.loadUserByUsername(username);

        List<Payment> previousPayments = paymentRepository.findPaymentsByUser(user);
        List<Payment> payments = paymentPostDtoList.stream()
                .map(p -> mapper.paymentPostDtoToPayment(p, userService))
                .sorted((p1, p2) -> p2.getPeriod().compareTo(p1.getPeriod()))
                .collect(Collectors.toList());

        previousPayments.addAll(payments);
        Validators.validateDistinctPeriodUserPairs(previousPayments);

        paymentRepository.saveAll(payments);
        return PaymentStatusDto.builder()
                .status("Added successfully!")
                .build();
    }

    @Transactional
    public PaymentStatusDto updatePayment(PaymentPostDto paymentPostDto) {
        log.info("Updating payment \"" + paymentPostDto + "\"");
        Payment payment = mapper.paymentPostDtoToPayment(paymentPostDto, userService);
        List<Payment> userPayments = paymentRepository.findPaymentsByUser(payment.getUser());
        Validators.validatePaymentPeriodExist(userPayments, payment);
        paymentRepository.updatePaymentByUserAndPeriod(
                payment.getUser(),
                payment.getPeriod(),
                payment.getSalary()
        );
        return PaymentStatusDto.builder()
                .status("Updated successfully!")
                .build();
    }

}
