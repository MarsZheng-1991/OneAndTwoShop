package com.OneAndTwoShop.userService.controller;

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

    // 取得全部使用者
    @GetMapping
    public ResponseEntity<ApiResponse<ApiData<List<User>>>> getAllUsers(
            @RequestHeader(value = "Accept-Language", defaultValue = "zh") String locale) {

        ApiData<List<User>> users = userService.getAllUsers(locale).block();

        return ResponseEntity.ok(
                new ApiResponse<>(HttpStatus.OK.value(), users)
        );
    }

    // 查詢單筆
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApiData<User>>> getUserById(
            @PathVariable Long id,
            @RequestHeader(value = "Accept-Language", defaultValue = "zh") String locale) {

        ApiData<User> user = userService.getUserById(id, locale).block();

        return ResponseEntity.ok(
                new ApiResponse<>(HttpStatus.OK.value(), user)
        );
    }

    // 建立使用者
    @PostMapping
    public ResponseEntity<ApiResponse<ApiData<Object>>> createUser(
            @RequestBody User user,
            @RequestHeader(value = "Accept-Language", defaultValue = "zh") String locale) {

        ApiData<Object> result = userService.createUser(user, locale).block();

        return ResponseEntity.ok(
                new ApiResponse<>(HttpStatus.OK.value(), result)
        );
    }

    // 更新使用者
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ApiData<Object>>> updateUser(
            @PathVariable Long id,
            @RequestBody User user,
            @RequestHeader(value = "Accept-Language", defaultValue = "zh") String locale) {

        ApiData<Object> result = userService.updateUser(id, user, locale).block();

        return ResponseEntity.ok(
                new ApiResponse<>(HttpStatus.OK.value(), result)
        );
    }

    // 刪除使用者
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<ApiData<Object>>> deleteUser(
            @PathVariable Long id,
            @RequestHeader(value = "Accept-Language", defaultValue = "zh") String locale) {

        ApiData<Object> result = userService.deleteUser(id, locale).block();

        return ResponseEntity.ok(
                new ApiResponse<>(HttpStatus.OK.value(), result)
        );
    }
}