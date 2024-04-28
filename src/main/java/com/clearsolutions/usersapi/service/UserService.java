package com.clearsolutions.usersapi.service;

import com.clearsolutions.usersapi.dto.UserDto;
import com.clearsolutions.usersapi.entity.User;
import com.clearsolutions.usersapi.exceptions.ResourceNotFoundException;
import com.clearsolutions.usersapi.exceptions.UserEmailAlreadyInUseException;
import com.clearsolutions.usersapi.mapper.UserMapper;
import com.clearsolutions.usersapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Value("${validation.required-age}")
    private long requiredAge;

    @Transactional
    public UserDto create(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserEmailAlreadyInUseException("Email already in use");
        }

        LocalDate dateOfBirth = user.getBirthDate();
        LocalDate eighteenYearsAgo = LocalDate.now().minusYears(requiredAge);


        if (dateOfBirth.isAfter(eighteenYearsAgo)) {
            throw new IllegalArgumentException("User must be at least " + requiredAge + " years old.");
        }

        User createdUser = userRepository.save(user);

        return userMapper.toDto(createdUser);
    }

    @Transactional
    public UserDto update(Long id, User user) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            throw new ResourceNotFoundException("User with this id not found");
        }

        LocalDate dateOfBirth = user.getBirthDate();
        LocalDate eighteenYearsAgo = LocalDate.now().minusYears(requiredAge);

        if (dateOfBirth.isAfter(eighteenYearsAgo)) {
            throw new IllegalArgumentException("User must be at least " + requiredAge + " years old.");
        }

        User existinUser = optionalUser.get();

        existinUser.setEmail(user.getEmail())
                .setFirstName(user.getFirstName())
                .setSurname(user.getSurname())
                .setBirthDate(user.getBirthDate())
                .setAddress(user.getAddress())
                .setPhoneNumber(user.getPhoneNumber());

        User savedUser = userRepository.save(existinUser);
        return userMapper.toDto(savedUser);

    }

    @Transactional
    public UserDto particularUpdate(Long id, User user) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            throw new ResourceNotFoundException("User with this id not found");
        }
        User existinUser = optionalUser.get();

        LocalDate dateOfBirth = user.getBirthDate();
        if (dateOfBirth != null) {
            LocalDate eighteenYearsAgo = LocalDate.now().minusYears(requiredAge);

            if (dateOfBirth.isAfter(eighteenYearsAgo)) {
                throw new IllegalArgumentException("User must be at least " + requiredAge + " years old.");
            } else {
                existinUser.setBirthDate(dateOfBirth);
            }
        }

        existinUser.setEmail(user.getEmail() != null ? user.getEmail() : existinUser.getEmail())
                .setFirstName(user.getFirstName() != null ? user.getFirstName() : existinUser.getFirstName())
                .setSurname(user.getSurname() != null ? user.getSurname() : existinUser.getSurname())
                .setAddress(user.getAddress() != null ? user.getAddress() : existinUser.getAddress())
                .setPhoneNumber(user.getPhoneNumber() != null ? user.getPhoneNumber() : existinUser.getPhoneNumber());


        User savedUser = userRepository.save(existinUser);
        return userMapper.toDto(savedUser);

    }

    @Transactional
    public void delete(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            throw new ResourceNotFoundException("User with this id not found");
        }
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<UserDto> findByDateOfBirthBetween(LocalDate from, LocalDate to) {
        List<User> users = userRepository.findByBirthDateBetween(from, to);
        return userMapper.toDto(users);
    }
}
