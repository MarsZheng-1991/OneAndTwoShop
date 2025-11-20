package com.OneAndTwoShop.userService.service;

import com.OneAndTwoShop.commonLib.common.error.BusinessException;
import com.OneAndTwoShop.commonLib.common.i18n.ErrorMessageService;
import com.OneAndTwoShop.commonLib.response.ApiData;
import com.OneAndTwoShop.userService.model.User;
import com.OneAndTwoShop.userService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final ErrorMessageService errorMessageService;

    // 取得所有使用者
    public Mono<ApiData<List<User>>> getAllUsers(String locale) {
        return Mono.fromCallable(() -> {

            List<User> list = userRepository.findAll();
            String message = errorMessageService.translate("user.list.success", locale);

            return new ApiData<>(message, list);

        }).subscribeOn(Schedulers.boundedElastic());
    }

    // 查詢單筆
    public Mono<ApiData<User>> getUserById(Long id, String locale) {
        return Mono.fromCallable(() -> {

            User user = userRepository.findById(id)
                    .orElseThrow(() -> new BusinessException("user.notfound"));

            String message = errorMessageService.translate("user.query.success", locale);

            return new ApiData<>(message, user);

        }).subscribeOn(Schedulers.boundedElastic());
    }

    // 建立使用者
    public Mono<ApiData<Object>> createUser(User user, String locale) {
        return Mono.fromCallable(() -> {

            userRepository.save(user);

            String message = errorMessageService.translate("user.created", locale);

            return new ApiData<>(message, null);

        }).subscribeOn(Schedulers.boundedElastic());
    }

    // 更新使用者
    public Mono<ApiData<Object>> updateUser(Long id, User req, String locale) {
        return Mono.fromCallable(() -> {

            User existing = userRepository.findById(id)
                    .orElseThrow(() -> new BusinessException("user.notfound"));

            existing.setUsername(req.getUsername());
            existing.setEmail(req.getEmail());
            existing.setAddress(req.getAddress());
            existing.setBirthday(req.getBirthday());
            existing.setChildBirthday(req.getChildBirthday());
            existing.setGender(req.getGender());

            userRepository.save(existing);

            String message = errorMessageService.translate("user.updated", locale);

            return new ApiData<>(message, null);

        }).subscribeOn(Schedulers.boundedElastic());
    }

    // 刪除
    public Mono<ApiData<Object>> deleteUser(Long id, String locale) {
        return Mono.fromCallable(() -> {

            if (!userRepository.existsById(id)) {
                throw new BusinessException("user.notfound");
            }

            userRepository.deleteById(id);

            String message = errorMessageService.translate("user.deleted", locale);

            return new ApiData<>(message, null);

        }).subscribeOn(Schedulers.boundedElastic());
    }
}