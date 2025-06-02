package com.example.pasir_maderak_michal.repository;

import com.example.pasir_maderak_michal.model.Group;
import com.example.pasir_maderak_michal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByMemberships_User(User user);
}