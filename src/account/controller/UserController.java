package account.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UserController {

    @PutMapping("api/admin/user/role")
    public void updateUserRole() {

    }

    @DeleteMapping("api/admin/user")
    public void deleteUser() {

    }

    @GetMapping("api/admin/user")
    public void getUser() {

    }
}
