package account.controller;

import account.dto.DeleteUserStatusDto;
import account.dto.UserGetDto;
import account.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("api/admin")
public class AdminController {

    private final UserService userService;

    @PutMapping("user/role")
    public void updateUserRole() {

    }

    @DeleteMapping("user/{username}")
    public DeleteUserStatusDto deleteUser(@PathVariable String username) {
        return userService.deleteUser(username);
    }

    @GetMapping("user")
    public List<UserGetDto> getUser() {
        return userService.getAllUsers();
    }
}
