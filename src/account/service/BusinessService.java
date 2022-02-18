package account.service;

import account.dto.payment.GetPaymentDto;
import account.dto.payment.PostPaymentDto;
import account.mapper.Mapper;
import account.model.Payment;
import account.model.user.User;
import account.repository.PaymentRepository;
import account.util.ResponseStatus;
import account.validator.Validators;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public List<GetPaymentDto> getUserPayments(User user) {
        log.info("Getting payments for \"" + user.getUsername() + "\"");
        return paymentRepository.findPaymentsByUser(user)
                .stream()
                .map(mapper::paymentToGetPaymentDto)
                .collect(Collectors.toList());
    }

    public GetPaymentDto getUserPaymentByPeriod(User user, String period) {
        log.info("Getting payment for \"" + user.getUsername() + "\" at \"" + period + "\"");
        LocalDate periodDate = mapper.periodToLocalDate(period);
        return paymentRepository.findPaymentByUserAndPeriod(user, periodDate)
                .stream()
                .map(mapper::paymentToGetPaymentDto)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find resource"));
    }

    public ResponseEntity<?> uploadPayment(List<PostPaymentDto> postPaymentDtoList) {
        log.info("Uploading payment \"" + postPaymentDtoList + "\"");

        Validators.validateIsOneUser(postPaymentDtoList);
        String username = postPaymentDtoList.get(0).getEmployee().toLowerCase();
        User user = userService.loadUserByUsername(username);

        List<Payment> previousPayments = paymentRepository.findPaymentsByUser(user);
        List<Payment> payments = postPaymentDtoList.stream()
                .map(p -> mapper.postPaymentDtoToPayment(p, userService))
                .sorted((p1, p2) -> p2.getPeriod().compareTo(p1.getPeriod()))
                .collect(Collectors.toList());

        previousPayments.addAll(payments);
        Validators.validateDistinctPeriodUserPairs(previousPayments);

        paymentRepository.saveAll(payments);
        return ResponseStatus.builder()
                .add("status", "Added successfully!")
                .build();
    }

    @Transactional
    public ResponseEntity<?> updatePayment(PostPaymentDto postPaymentDto) {
        log.info("Updating payment \"" + postPaymentDto + "\"");
        Payment payment = mapper.postPaymentDtoToPayment(postPaymentDto, userService);
        List<Payment> userPayments = paymentRepository.findPaymentsByUser(payment.getUser());
        Validators.validatePaymentPeriodExist(userPayments, payment);
        paymentRepository.updatePaymentByUserAndPeriod(
                payment.getUser(),
                payment.getPeriod(),
                payment.getSalary()
        );
        return ResponseStatus.builder()
                .add("status", "Updated successfully!")
                .build();
    }

}
