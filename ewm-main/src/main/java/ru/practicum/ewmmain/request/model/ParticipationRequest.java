package ru.practicum.ewmmain.request.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewmmain.event.model.Event;
import ru.practicum.ewmmain.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "requests")
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "created", nullable = false)
    LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    Event event;

    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    User requester;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    StateParticipationRequest status;
}
