package com.homework.users;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class UserCreationDTO {
    private String username;
    private String password;
    private String repeatPassword;
    private long age;

    public UserCreationDTO(String username, String password, String repeatPassword, long age) {
        this.username = username;
        this.password = password;
        this.repeatPassword = repeatPassword;
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    public long getAge() {
        return age;
    }

    public void setAge(long age) {
        this.age = age;
    }
}

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public List<User> getAllUsers(@RequestParam(required = false) Long age)
    {
        if (age == null) {
            return new ArrayList<User>(userRepository.findAll());
        } else {
            return new ArrayList<User>(userRepository.findByAge(Math.toIntExact(age)));
        }
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable("id") long id) {
        Optional<User> userData = userRepository.findById(id);
        if (userData.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return userData.get();
    }

    @PostMapping("/users")
    public User createUser(@RequestBody UserCreationDTO user) {
        if (!user.getPassword().equals(user.getRepeatPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        List<User> alreadyExistingUser = userRepository.findByUsername(user.getUsername());
        if (!alreadyExistingUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        return userRepository.save(new User(user.getUsername(), user.getPassword(), user.getAge()));
    }

    @DeleteMapping("/users/{id}")
    public HttpStatus deleteUserById(@PathVariable("id") long id) {
        userRepository.deleteById(id);
        return HttpStatus.NO_CONTENT;
    }

    @PutMapping("/users/{id}")
    public User updateUser(@PathVariable("id") long id, @RequestBody UserCreationDTO input) {
        if (!input.getPassword().equals(input.getRepeatPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Optional<User> userData = userRepository.findById(id);
        if (userData.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        User user = userData.get();
        user.setUsername(input.getUsername());
        user.setPassword(input.getPassword());
        user.setAge(input.getAge());
        return userRepository.save(user);
    }
}
