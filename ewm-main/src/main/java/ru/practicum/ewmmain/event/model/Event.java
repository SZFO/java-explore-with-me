package ru.practicum.ewmmain.event.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.ewmmain.category.model.Category;
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
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String annotation;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    User initiator;

    String title;

    @Column(name = "created_on")
    @CreationTimestamp
    LocalDateTime createdOn;

    @Column(nullable = false)
    String description;

    @Column(name = "event_date")
    LocalDateTime eventDate;

    Boolean paid;

    @Column(name = "participant_limit")
    Long participantLimit;

    @Column(name = "published_on")
    LocalDateTime publishedOn;

    @Enumerated(EnumType.STRING)
    StateEvent state;

    @Column(name = "location_lat")
    Float locationLat;

    @Column(name = "location_lon")
    Float locationLon;

    @Column(name = "request_moderation")
    Boolean requestModeration;
}