package pl.spribe.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.spribe.dto.UserCreateRequestDTO;
import pl.spribe.dto.UserDTO;
import pl.spribe.entity.Event;
import pl.spribe.entity.User;
import pl.spribe.event.UserCreatedEvent;
import pl.spribe.repository.UserRepository;
import pl.spribe.util.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final EventService eventService;
    private final ApplicationEventPublisher publisher;

    public UserService(UserRepository userRepository, EventService eventService, ApplicationEventPublisher publisher) {
        this.userRepository = userRepository;
        this.eventService = eventService;
        this.publisher = publisher;
    }

    @Transactional
    public UserDTO createUser(UserCreateRequestDTO req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        User user = new User();
        user.setUsername(req.getUsername());
        user.setFullName(req.getFullName());
        user.setRole(req.getRole());
        user = userRepository.save(user);

        eventService.createEvent(null, Event.EventType.USER_CREATED, "User created: " + user.getUsername());

        publisher.publishEvent(new UserCreatedEvent(user.getId(), user.getUsername()));

        return Mapper.userToDTO(user);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(Mapper::userToDTO).collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        return userRepository.findById(id).map(Mapper::userToDTO)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Transactional
    public UserDTO updateUser(Long id, UserCreateRequestDTO req) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setFullName(req.getFullName());
        user.setRole(req.getRole());
        user = userRepository.save(user);

        eventService.createEvent(null, Event.EventType.USER_UPDATED, "User updated: " + user.getUsername());

        return Mapper.userToDTO(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        userRepository.delete(user);

        eventService.createEvent(null, Event.EventType.USER_DELETED, "User deleted: " + user.getUsername());
    }
}
