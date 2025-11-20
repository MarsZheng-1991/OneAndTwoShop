package com.OneAndTwoShop.userService.controller;

import com.OneAndTwoShop.commonLib.common.error.BusinessException;
import com.OneAndTwoShop.commonLib.response.ApiData;
import com.OneAndTwoShop.commonLib.response.ApiResponse;
import com.OneAndTwoShop.userService.model.User;
import com.OneAndTwoShop.userService.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 取得所有使用者
    @GetMapping
    public ResponseEntity<ApiResponse<ApiData<List<User>>>> getAllUsers() {

        ApiData<List<User>> users = userService.getAllUsers().block();

        return ResponseEntity.ok(
                new ApiResponse<>(HttpStatus.OK.value(), users)
        );
    }

    // 查詢 ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApiData<User>>> getUserById(@PathVariable Long id) {

        ApiData<User> user = userService.getUserById(id).block();

        return ResponseEntity.ok(
                new ApiResponse<>(HttpStatus.OK.value(), user)
        );
    }

    // 建立使用者
    @PostMapping
    public ResponseEntity<ApiResponse<ApiData<String>>> createUser(@RequestBody User user) {

        ApiData<String> result = userService.createUser(user).block();

        return ResponseEntity.ok(
                new ApiResponse<>(HttpStatus.OK.value(), result)
        );
    }

    // 更新使用者
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ApiData<String>>> updateUser(
            @PathVariable Long id,
            @RequestBody User user) {

        ApiData<String> result = userService.updateUser(id, user).block();

        return ResponseEntity.ok(
                new ApiResponse<>(HttpStatus.OK.value(), result)
        );
    }

    // 刪除使用者
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<ApiData<String>>> deleteUser(@PathVariable Long id) {

        ApiData<String> result = userService.deleteUser(id).block();

        return ResponseEntity.ok(
                new ApiResponse<>(HttpStatus.OK.value(), result)
        );
    }
}