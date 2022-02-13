package account.controller;

import account.service.AuthFacade;
import account.dto.UserGetDto;
import account.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class BusinessController {

    private final UserService userService;
    private final AuthFacade authFacade;

    @GetMapping("api/empl/payment")
    public UserGetDto getPayment() {
        return userService.findUser(authFacade.getAuth().getName());
    }

    @PostMapping("api/acct/payments")
    public void uploadPayment() {

    }

    @PutMapping("api/acct/payments")
    public void updatePayment() {

    }
}
