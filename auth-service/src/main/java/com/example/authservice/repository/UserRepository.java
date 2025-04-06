package com.example.authservice.repository;

import com.example.authservice.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findBySisiId(String sisiId);
    boolean existsBySisiId(String sisiId);
} 