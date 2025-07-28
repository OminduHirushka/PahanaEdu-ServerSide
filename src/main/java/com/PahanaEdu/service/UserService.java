package com.PahanaEdu.service;

import com.PahanaEdu.dto.UserDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    List<UserDTO> getAllUsers();
    UserDTO getUserByAccountNumber(String accountNumber);
    UserDTO updateUser(String accountNumber, UserDTO userDTO);
    void deleteUser(String accountNumber);
}
