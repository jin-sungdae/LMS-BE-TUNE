package com.savelms.core.user.domain.repository;

import com.savelms.core.user.AttendStatus;
import com.savelms.core.user.domain.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Override
    @EntityGraph(attributePaths = {"userRoles", "userRoles.role"})
    List<User> findAll();

    Optional<User> findByUsername(String username);

    List<User> findAllByEmailAuth(boolean ch);

    Optional<User> findByApiId(String apiId);

    List<User> findAllByAttendStatus(AttendStatus ch);

}
