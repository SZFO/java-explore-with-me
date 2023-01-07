package ru.practicum.ewmmain.user.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewmmain.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsUserByName(String name);

    @Query("SELECT u FROM User u WHERE u.id IN ?1")
    List<User> findUsersByIds(List<Long> ids, Pageable pageable);
}