package com.clearsolutions.usersapi.controller;

import com.clearsolutions.usersapi.dto.UserDto;
import com.clearsolutions.usersapi.dto.validation.OnCreate;
import com.clearsolutions.usersapi.dto.validation.OnParticalUpdate;
import com.clearsolutions.usersapi.dto.validation.OnUpdate;
import com.clearsolutions.usersapi.entity.User;
import com.clearsolutions.usersapi.exceptions.InvalidRequestParametersException;
import com.clearsolutions.usersapi.mapper.UserMapper;
import com.clearsolutions.usersapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping()
    public List<UserDto> findByDateOfBirthBetween(@RequestParam("from")
                                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                  @RequestParam("to")
                                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        if (from.isAfter(to)) {
            throw new InvalidRequestParametersException("'from' date must be before 'to' date");
        }
        return userService.findByDateOfBirthBetween(from, to);
    }

    @PostMapping
    public UserDto createUser(@Validated(OnCreate.class) @RequestBody UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        return userService.create(user);
    }

    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id,
                              @Validated(OnUpdate.class) @RequestBody UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        return userService.update(id, user);
    }

    @PatchMapping("/{id}")
    public UserDto particularUpdateUser(@PathVariable Long id,
                                     @Validated(OnParticalUpdate.class) @RequestBody UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        return userService.particularUpdate(id, user);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        userService.delete(id);
    }


}
