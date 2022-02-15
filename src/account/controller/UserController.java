package account.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("api/admin")
public class UserController {

    @PutMapping("user/role")
    public void updateUserRole() {

    }

    @DeleteMapping("user")
    public void deleteUser() {

    }

    @GetMapping("user")
    public void getUser() {

    }
}
