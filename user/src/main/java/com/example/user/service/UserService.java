package com.example.user.service;

import com.example.user.model.User;
import com.example.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
@Service
public class UserService {
    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public User register(User user) {
        return repo.save(user);
    }

    public Optional<User> login(String email, String password) {
        return repo.findByEmailAndPassword(email, password);
    }

    public Optional<User> getUserById(Integer id) {
        return repo.findById(id);
    }

    public List<User> getAllUsers() {
        return repo.findAll();
    }

    public void deleteUser(Integer id) {
        repo.deleteById(id);
    }

    public boolean existsById(Integer id) {
        return repo.existsById(id); // Add this method
    }
}
