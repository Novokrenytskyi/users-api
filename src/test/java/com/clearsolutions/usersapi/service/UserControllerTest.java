package com.clearsolutions.usersapi.service;


import com.clearsolutions.usersapi.dto.UserDto;
import com.clearsolutions.usersapi.exceptions.InvalidRequestParametersException;
import com.clearsolutions.usersapi.mapper.UserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserMapper userMapper;
    @MockBean
    private UserService userService;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Long ID = 1L;
    private static final String INVALID_EMAIL = "invalidEmail";
    private static final LocalDate INVALID_DATE = LocalDate.now().plusYears(1);
    private static final String DATE_FROM = "1999-01-01";
    private static final String DATE_TO = "2005-01-01";

    private UserDto userDto = new UserDto()
            .setId(1L)
            .setEmail("test@gmail.com")
            .setFirstName("TestName")
            .setSurname("TestSurname")
            .setBirthDate(LocalDate.of(1999, 9, 9))
            .setAddress("TestAddress")
            .setPhoneNumber("999-999-999");

    private UserDto WithoutRequiredFields = new UserDto()
            .setAddress("TestAddress")
            .setPhoneNumber("999-999-999");

    @Test
    void findByDateOfBirthBetween() throws Exception {
        when(userService.findByDateOfBirthBetween(LocalDate.parse(DATE_FROM, DATE_TIME_FORMATTER),
                LocalDate.parse(DATE_TO, DATE_TIME_FORMATTER)))
                .thenReturn(List.of(userDto));


        mockMvc.perform(get("/api/v1/users").
                        param("from", DATE_FROM).
                        param("to", DATE_TO))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(userDto.getId()))
                .andExpect(jsonPath("$[0].firstName").value(userDto.getFirstName()))
                .andExpect(jsonPath("$[0].email").value(userDto.getEmail()));


    }

    @Test
    void findByDateOfBirthBetweenByInvalidParams() throws Exception {

        mockMvc.perform(get("/api/v1/users")
                        .param("from", DATE_TO)
                        .param("to", DATE_FROM))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("'from' date must be before 'to' date"))
                .andExpect(result -> {
                    if (!(result.getResolvedException() instanceof InvalidRequestParametersException)) {
                        throw new AssertionError("Expected InvalidRequestParametersException");
                    }
                });
    }

    @Test
    void create() throws Exception {
        when(userService.create(userMapper.toEntity(userDto))).thenReturn(userDto);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.firstName").value(userDto.getFirstName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));


    }

    @Test
    void createWithoutRequiredFields() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(WithoutRequiredFields)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(result -> {
                    if (!(result.getResolvedException() instanceof MethodArgumentNotValidException)) {
                        throw new AssertionError("Expected MethodArgumentNotValidException");
                    }
                });
    }

    @Test
    void createWithInvalidEmail() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto.setEmail(INVALID_EMAIL))))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(result -> {
                    if (!(result.getResolvedException() instanceof MethodArgumentNotValidException)) {
                        throw new AssertionError("Expected MethodArgumentNotValidException");
                    }
                });
    }

    @Test
    void createWithInvalidBirthDate() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto.setBirthDate(INVALID_DATE))))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(result -> {
                    if (!(result.getResolvedException() instanceof MethodArgumentNotValidException)) {
                        throw new AssertionError("Expected MethodArgumentNotValidException");
                    }
                });
    }

    @Test
    void update() throws Exception {
        when(userService.update(ID, userMapper.toEntity(userDto)))
                .thenReturn(userDto);

        mockMvc.perform(put("/api/v1/users/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()))
                .andExpect(jsonPath("$.firstName").value(userDto.getFirstName()))
                .andExpect(jsonPath("$.surname").value(userDto.getSurname()))
                .andExpect(jsonPath("$.birthDate").value(userDto.getBirthDate().format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.address").value(userDto.getAddress()))
                .andExpect(jsonPath("$.phoneNumber").value(userDto.getPhoneNumber()));


    }

    @Test
    void updateWithInvalidEmail() throws Exception {
        mockMvc.perform(put("/api/v1/users/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto.setEmail(INVALID_EMAIL))))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(result -> {
                    if (!(result.getResolvedException() instanceof MethodArgumentNotValidException)) {
                        throw new AssertionError("Expected MethodArgumentNotValidException");
                    }
                });
    }

    @Test
    void updateWithInvalidBirthDate() throws Exception {
        mockMvc.perform(put("/api/v1/users/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto.setBirthDate(INVALID_DATE))))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(result -> {
                    if (!(result.getResolvedException() instanceof MethodArgumentNotValidException)) {
                        throw new AssertionError("Expected MethodArgumentNotValidException");
                    }
                });
    }

    @Test
    void partialUpdate() throws Exception {
        when(userService.particularUpdate(ID, userMapper.toEntity(userDto))).thenReturn(userDto);

        mockMvc.perform(patch("/api/v1/users/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()))
                .andExpect(jsonPath("$.firstName").value(userDto.getFirstName()))
                .andExpect(jsonPath("$.surname").value(userDto.getSurname()))
                .andExpect(jsonPath("$.birthDate").value(userDto.getBirthDate().format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.address").value(userDto.getAddress()))
                .andExpect(jsonPath("$.phoneNumber").value(userDto.getPhoneNumber()));



    }

    @Test
    void particularUpdateWithInvalidEmail() throws Exception {
        mockMvc.perform(patch("/api/v1/users/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto.setEmail(INVALID_EMAIL))))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(result -> {
                    if (!(result.getResolvedException() instanceof MethodArgumentNotValidException)) {
                        throw new AssertionError("Expected MethodArgumentNotValidException");
                    }
                });
    }

    @Test
    void particularUpdateWithInvalidBirthDate() throws Exception {
        mockMvc.perform(patch("/api/v1/users/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto.setBirthDate(INVALID_DATE))))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(result -> {
                    if (!(result.getResolvedException() instanceof MethodArgumentNotValidException)) {
                        throw new AssertionError("Expected MethodArgumentNotValidException");
                    }
                });
    }

    @Test
    void deleteById() throws Exception {
        mockMvc.perform(delete("/api/v1/users/{id}", ID))
                .andExpect(status().isOk());

    }
}