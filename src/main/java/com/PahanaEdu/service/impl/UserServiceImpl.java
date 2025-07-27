package com.PahanaEdu.service.impl;

import com.PahanaEdu.dto.UserDTO;
import com.PahanaEdu.exception.DuplicateResourceException;
import com.PahanaEdu.exception.ResourceNotFoundException;
import com.PahanaEdu.model.User;
import com.PahanaEdu.repository.UserRepository;
import com.PahanaEdu.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.findByAccountNumber(userDTO.getAccountNumber()).isPresent()) {
            throw new DuplicateResourceException("User already exists");
        }
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already exists");
        }
        if (userRepository.existsByContact(userDTO.getContact())) {
            throw new DuplicateResourceException("Contact already exists");
        }
        if (userRepository.existsByNic(userDTO.getNic())) {
            throw new DuplicateResourceException("Nic already exists");
        }

        User user = new User();
        user.setAccountNumber(userDTO.getAccountNumber());
        user.setFullName(userDTO.getFullName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setContact(userDTO.getContact());
        user.setNic(userDTO.getNic());
        user.setAddress(userDTO.getAddress());
        user.setRole(userDTO.getRole());

        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDTO.class);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserByAccountNumber(String accountNumber) {
        User user = userRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public UserDTO updateUser(String accountNumber, UserDTO userDTO) {
        User existingUser = userRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!existingUser.getContact().equals(userDTO.getContact()) &&
                userRepository.existsByContact(userDTO.getContact())) {
            throw new DuplicateResourceException("Contact already exists");
        }
        if (!existingUser.getEmail().equals(userDTO.getEmail()) &&
                userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already exists");
        }

        existingUser.setFullName(userDTO.getFullName());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setPassword(userDTO.getPassword());
        existingUser.setContact(userDTO.getContact());
        existingUser.setAddress(userDTO.getAddress());

        User updatedUser = userRepository.save(existingUser);
        return modelMapper.map(updatedUser, UserDTO.class);
    }

    @Override
    public void deleteUser(String accountNumber) {
        User user = userRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userRepository.delete(user);
    }
}
