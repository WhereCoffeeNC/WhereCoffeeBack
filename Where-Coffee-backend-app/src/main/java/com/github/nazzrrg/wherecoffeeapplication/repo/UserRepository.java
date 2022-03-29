package com.github.nazzrrg.wherecoffeeapplication.repo;

import com.github.nazzrrg.wherecoffeeapplication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByName(String name);
    Boolean existsByName(String name);
    Boolean existsByEmail(String email);
    @Query(value =
            "select * from users " +
            "where id in ( " +
            "        select user_id from user_roles" +
            "        join roles r on r.id = user_roles.role_id" +
            "        where name = :role" +
            "    )",
            nativeQuery = true)
    List<User> findAllByRole(String role);
}
