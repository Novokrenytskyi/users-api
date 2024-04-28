package com.clearsolutions.usersapi.dto;

import com.clearsolutions.usersapi.dto.validation.OnCreate;
import com.clearsolutions.usersapi.dto.validation.OnParticalUpdate;
import com.clearsolutions.usersapi.dto.validation.OnUpdate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class UserDto {
    @NotNull(message = "Email must be not null.", groups = OnUpdate.class)
    private Long id;

    @NotNull(message = "Email must be not null.", groups = {OnUpdate.class, OnCreate.class})
    @Email(message = "Email address you entered is invalid.",
            groups = {OnUpdate.class, OnCreate.class, OnParticalUpdate.class})
    private String email;

    @NotBlank(message = "Name must be not null and empty.", groups = {OnUpdate.class, OnCreate.class})
    private String firstName;

    @NotBlank(message = "Surname must be not null and empty.", groups = {OnUpdate.class, OnCreate.class})
    private String surname;

    @NotNull(message = "Birth date must be not null.", groups = {OnUpdate.class, OnCreate.class})
    @Past(message = "Value must be a date that occurred before the current point in time.",
            groups = {OnUpdate.class, OnCreate.class, OnParticalUpdate.class})
    private LocalDate birthDate;

    private String address;

    private String phoneNumber;
}
