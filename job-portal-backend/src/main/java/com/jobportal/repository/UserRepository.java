package com.jobportal.repository;

import com.jobportal.model.User;
import com.jobportal.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    
    List<User> findByRole(Role role);
    
    long countByRole(Role role);
}
