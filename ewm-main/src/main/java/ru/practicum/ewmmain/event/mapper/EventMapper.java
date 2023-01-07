package ru.practicum.ewmmain.event.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewmmain.category.dto.CategoryDto;
import ru.practicum.ewmmain.category.mapper.CategoryMapper;
import ru.practicum.ewmmain.category.model.Category;
import ru.practicum.ewmmain.event.dto.EventFullDto;
import ru.practicum.ewmmain.event.dto.EventShortDto;
import ru.practicum.ewmmain.event.dto.NewEventDto;
import ru.practicum.ewmmain.event.model.Event;
import ru.practicum.ewmmain.event.model.Location;
import ru.practicum.ewmmain.user.dto.UserShortDto;
import ru.practicum.ewmmain.user.mapper.UserMapper;
import ru.practicum.ewmmain.user.model.User;

@Component
@RequiredArgsConstructor
public class EventMapper {
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;

    public Event dtoToNewEvent(NewEventDto newEventDto, Category category, User initiator) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .initiator(initiator)
                .category(category)
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .locationLat(newEventDto.getLocation().getLat())
                .locationLon(newEventDto.getLocation().getLon())
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .title(newEventDto.getTitle())
                .build();
    }

    public EventFullDto eventToFullDto(Event event, CategoryDto category, UserShortDto initiator,
                                       Long confirmedRequests, Long view) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(category)
                .confirmedRequests(confirmedRequests)
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(initiator)
                .location(getLocation(event.getLocationLat(), event.getLocationLon()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(view)
                .build();
    }

    public EventShortDto eventToShortDto(Event event, CategoryDto category, UserShortDto initiator,
                                         Long confirmedRequests, Long view) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(category)
                .confirmedRequests(confirmedRequests)
                .eventDate(event.getEventDate())
                .initiator(initiator)
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(view)
                .build();
    }

    private static Location getLocation(float lat, float lon) {
        return Location.builder()
                .lat(lat)
                .lon(lon)
                .build();
    }

    public EventShortDto toShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .category(categoryMapper.categoryToDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .paid(event.getPaid())
                .initiator(userMapper.userToShortDto(event.getInitiator()))
                .build();
    }
}