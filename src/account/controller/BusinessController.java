package account.controller;

import account.dto.PaymentPostDto;
import account.dto.PaymentStatusDto;
import account.model.User;
import account.service.BusinessService;
import account.util.ValidList;
import account.validator.Validators;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("api")
public class BusinessController {

    private final BusinessService businessService;

    @GetMapping("empl/payment")
    public ResponseEntity<?> getPayment(@RequestParam Optional<String> period,
                                        @AuthenticationPrincipal User user) {
        if (period.isPresent()) {
            Validators.validatePeriod(period.get());
            return ResponseEntity.ok(businessService.getUserPaymentByPeriod(user, period.get()));
        }
        return ResponseEntity.ok(businessService.getUserPayments(user));
    }


    @PostMapping("acct/payments")
    public PaymentStatusDto uploadPayment(@RequestBody @Valid ValidList<PaymentPostDto> payments) {
        return businessService.uploadPayment(payments);
    }

    @PutMapping("acct/payments")
    public PaymentStatusDto updatePayment(@RequestBody @Valid PaymentPostDto payment) {
        return businessService.updatePayment(payment);
    }

}
