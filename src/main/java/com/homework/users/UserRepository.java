package com.homework.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.age >= ?1 - 5 and u.age <= ?1 + 5")
    List<User> findByAge(int age);
}
