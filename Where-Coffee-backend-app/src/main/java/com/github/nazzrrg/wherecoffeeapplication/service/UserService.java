package com.github.nazzrrg.wherecoffeeapplication.service;

import com.github.nazzrrg.wherecoffeeapplication.model.Cafe;
import com.github.nazzrrg.wherecoffeeapplication.model.ERole;
import com.github.nazzrrg.wherecoffeeapplication.payload.request.UserUpdateRequest;
import com.github.nazzrrg.wherecoffeeapplication.payload.response.MessageResponse;
import com.github.nazzrrg.wherecoffeeapplication.repo.UserRepository;
import com.github.nazzrrg.wherecoffeeapplication.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Value("${netcracker.app.itemsOnPage}")
    private int itemsOnPage;
    private final UserRepository repository;
    private final PasswordEncoder encoder;

    public UserService(UserRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    public void create(@RequestBody User user) {
        if (repository.existsByUsername(user.getUsername())) {
            repository.save(user);
        }
        else new RuntimeException("Error: username is taken!");
    }

    public Page<User> getPage(Integer page, String username, String role) {
        Pageable pageable = PageRequest.of(page, itemsOnPage);
        return repository.findUserByUsernameAndRole(username, role, pageable);
    }

    public User getById(long id) {
        return repository.findById(id).orElseThrow(RuntimeException::new);
    }
    public ResponseEntity<MessageResponse> update(long id, UserUpdateRequest userRequest) {
        User userToBeUpdated = getById(id);

        userToBeUpdated.setFirstName(userRequest.getFirstName());;
        userToBeUpdated.setSurname(userRequest.getSurname());
        userToBeUpdated.setPatronymic(userRequest.getPatronymic());
        userToBeUpdated.setBirthDay(userRequest.getBirthDay());
        userToBeUpdated.setEmail(userRequest.getEmail());
        userToBeUpdated.setPhone(userRequest.getPhone());
        if (userRequest.getNewPassword() != null) {
            if (!encoder.matches(userRequest.getOldPassword(), userToBeUpdated.getPassword())) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: old password is incorrect!"));
            }
            userToBeUpdated.setPassword(encoder.encode(userRequest.getNewPassword()));
        }
        repository.save(userToBeUpdated);

        return ResponseEntity
                .ok()
                .body(new MessageResponse("Profile information updated successfully!"));
    }
    public void delete(long id) {
        repository.deleteById(id);
    }
}
