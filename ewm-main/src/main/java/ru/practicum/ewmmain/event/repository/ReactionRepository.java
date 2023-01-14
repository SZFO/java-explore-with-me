package ru.practicum.ewmmain.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmmain.event.model.Reaction;
import ru.practicum.ewmmain.event.model.StateReaction;

import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Optional<Reaction> findByEventIdAndUserId(Long eventId, Long userId);

    Long countAllByEventIdAndStateReaction(Long eventId, StateReaction stateReaction);

    Long countAllByEventId(Long eventId);

    Long countAllByEventInitiatorIdAndStateReaction(Long userId, StateReaction stateReaction);

    Long countAllByEventInitiatorId(Long userId);
}