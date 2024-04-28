package com.clearsolutions.usersapi.service;

import com.clearsolutions.usersapi.dto.UserDto;
import com.clearsolutions.usersapi.entity.User;
import com.clearsolutions.usersapi.exceptions.ResourceNotFoundException;
import com.clearsolutions.usersapi.exceptions.UserEmailAlreadyInUseException;
import com.clearsolutions.usersapi.mapper.UserMapper;
import com.clearsolutions.usersapi.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    private static final Long ID = 1L;
    private static final Integer EXCEPTED_ARRAY_SIZE = 2;
    private static final LocalDate INVALID_DATE = LocalDate.now().minusYears(17);
    private static final LocalDate DATE_FROM = LocalDate.of(1999, 01, 01);
    private static final LocalDate DATE_TO = LocalDate.of(2005, 01, 01);

    private final User validUser = User.builder()
            .email("test@gmail.com")
            .firstName("TestName")
            .surname("TestSurname")
            .birthDate(LocalDate.of(1999, 9, 9))
            .address("TestAddress")
            .phoneNumber("999-999-999")
            .build();
    private final User updatedUser = User.builder()
            .id(ID)
            .email("updated@gmail.com")
            .firstName("UpdatedName")
            .surname("UpdatedName")
            .birthDate(LocalDate.of(1999, 9, 9))
            .build();


    @Test
    void create() {
        when(userRepository.save(validUser)).thenReturn(validUser.setId(ID));
        System.out.println(validUser.getBirthDate());
        UserDto resultUser = userService.create(validUser);

        assertNotNull(resultUser);
        assertEquals(userMapper.toDto(validUser), resultUser);
    }

    @Test
    void createWithExistedEmail() {
        when(userRepository.findByEmail(validUser.getEmail())).thenReturn(Optional.of(validUser.setId(ID)));

        assertThrows(UserEmailAlreadyInUseException.class, () -> userService.create(validUser));
    }

    @Test
    void createWithInvalidAge() {
        User userWithInvalidBirthDate = validUser.setBirthDate(INVALID_DATE);

        assertThrows(IllegalArgumentException.class, () -> userService.create(userWithInvalidBirthDate));
    }

    @Test
    void update() {
        when(userRepository.findById(ID)).thenReturn(Optional.of(validUser.setId(ID)));
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        UserDto resultUser = userService.update(ID, updatedUser);

        assertNotNull(resultUser);
        assertEquals(userMapper.toDto(updatedUser), resultUser);

    }

    @Test
    void updateByInvalidId() {
        when(userRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.update(ID, updatedUser));
    }

    @Test
    void updateWithInvalidBirthDate() {
        when(userRepository.findById(ID)).thenReturn(Optional.of(validUser));

        assertThrows(IllegalArgumentException.class, () -> userService.update(ID, updatedUser.setBirthDate(INVALID_DATE)));
    }


    @Test
    void particularUpdate() {
        when(userRepository.findById(ID)).thenReturn(Optional.of(validUser));

        User particularUpdatedUser = validUser.setId(updatedUser.getId())
                .setEmail(updatedUser.getEmail())
                .setFirstName(updatedUser.getFirstName())
                .setSurname(updatedUser.getSurname())
                .setBirthDate(updatedUser.getBirthDate());
        when(userRepository.save(particularUpdatedUser)).thenReturn(particularUpdatedUser);


        UserDto resultUser = userService.particularUpdate(ID, updatedUser);

        assertNotNull(resultUser);
        assertEquals(userMapper.toDto(particularUpdatedUser), resultUser);

    }

    @Test
    void particularUpdateByInvalidId() {
        when(userRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.particularUpdate(ID, updatedUser));
    }


    @Test
    void particularUpdateUserWithInvalidBirthDate() {
        when(userRepository.findById(ID)).thenReturn(Optional.of(validUser));

        assertThrows(IllegalArgumentException.class, () -> userService.particularUpdate(ID, updatedUser.setBirthDate(INVALID_DATE)));
    }

    @Test
    void delete() {
        when(userRepository.findById(ID)).thenReturn(Optional.of(validUser));

        assertDoesNotThrow(() -> userService.delete(ID));
    }

    @Test
    void deleteByInvalidId() {
        when(userRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.delete(ID));
    }


    @Test
    void findByDateOfBirthBetween() {
        when(userRepository.findByBirthDateBetween(Mockito.any(), Mockito.any())).thenReturn(List.of(validUser, updatedUser));

        List<UserDto> result = userService.findByDateOfBirthBetween(DATE_FROM, DATE_TO);

        assertNotNull(result);
        assertThat(result).hasSize(EXCEPTED_ARRAY_SIZE);

    }
}