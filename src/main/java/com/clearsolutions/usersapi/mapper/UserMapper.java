package com.clearsolutions.usersapi.mapper;

import com.clearsolutions.usersapi.dto.UserDto;
import com.clearsolutions.usersapi.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper implements Mapper<User, UserDto> {
    @Override
    public UserDto toDto(User entity) {
        UserDto dto = new UserDto();
        dto.setId(entity.getId())
                .setEmail(entity.getEmail())
                .setFirstName(entity.getFirstName())
                .setSurname(entity.getSurname())
                .setBirthDate(entity.getBirthDate())
                .setAddress(entity.getAddress())
                .setPhoneNumber(entity.getPhoneNumber());

        return dto;

    }

    @Override
    public List<UserDto> toDto(List<User> entityList) {
        return entityList.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public User toEntity(UserDto dto) {
        User user = User.builder()
                .id(dto.getId())
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .surname(dto.getSurname())
                .birthDate(dto.getBirthDate())
                .address(dto.getAddress())
                .phoneNumber(dto.getPhoneNumber())
                .build();

        return user;
    }

}
