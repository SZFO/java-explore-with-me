package ru.practicum.ewmmain.user.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmmain.user.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsUserByName(String name);

    List<User> findUsersByIdInOrderById(List<Long> ids, Pageable pageable);
}