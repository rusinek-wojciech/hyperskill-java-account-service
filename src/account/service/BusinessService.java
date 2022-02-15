package account.service;

import account.dto.PaymentGetDto;
import account.dto.PaymentPostDto;
import account.dto.PaymentStatusDto;
import account.exception.PaymentDuplicatedException;
import account.exception.PeriodNotExist;
import account.mapper.Mapper;
import account.model.Payment;
import account.model.User;
import account.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class BusinessService {

    private final AuthFacade authFacade;
    private final PaymentRepository paymentRepository;
    private final UserService userService;
    private final Mapper mapper;

    public List<PaymentGetDto> getUserPayments() {
        String username = authFacade.getAuth().getName();
        log.info("Getting payments for \"" + username + "\"");
        User user = userService.loadUserByUsername(username);
        return paymentRepository.findPaymentsByUser(user)
                .stream()
                .map(mapper::paymentToPaymentGetDto)
                .collect(Collectors.toList());
    }

    public PaymentGetDto getUserPaymentByPeriod(String period) {
        String username = authFacade.getAuth().getName();
        log.info("Getting payment for \"" + username + "\" at \"" + period + "\"");
        User user = userService.loadUserByUsername(username);
        LocalDate periodDate = mapper.periodToLocalDate(period);
        return paymentRepository.findPaymentByUserAndPeriod(user, periodDate)
                .stream()
                .map(mapper::paymentToPaymentGetDto)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find resource"));
    }

    public PaymentStatusDto uploadPayment(List<PaymentPostDto> paymentPostDtoList) {
        log.info("Uploading payment \"" + paymentPostDtoList + "\"");

        long distinctCount = paymentPostDtoList.stream()
                .map(p -> new DoubleEqual<>(p.getEmployee(), p.getPeriod()))
                .distinct()
                .count();
        if (distinctCount != paymentPostDtoList.size()) {
            throw new PaymentDuplicatedException();
        }

        // TODO: check user payment

        List<Payment> payments = paymentPostDtoList.stream()
                .map(p -> mapper.paymentPostDtoToPayment(p, userService))
                .sorted((p1, p2) -> p2.getPeriod().compareTo(p1.getPeriod()))
                .collect(Collectors.toList());

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

        boolean periodNotExist = userPayments.stream()
                .map(Payment::getPeriod)
                .noneMatch(d -> d.equals(payment.getPeriod()));
        if (periodNotExist) {
            throw new PeriodNotExist();
        }

        paymentRepository.updatePaymentByUserAndPeriod(
                payment.getUser(),
                payment.getPeriod(),
                payment.getSalary()
        );
        return PaymentStatusDto.builder()
                .status("Updated successfully!")
                .build();
    }


    @AllArgsConstructor
    private static class DoubleEqual<P, T> {
        private P p;
        private T t;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DoubleEqual<?, ?> that = (DoubleEqual<?, ?>) o;
            return Objects.equals(p, that.p) && Objects.equals(t, that.t);
        }

        @Override
        public int hashCode() {
            return Objects.hash(p, t);
        }
    }
}
