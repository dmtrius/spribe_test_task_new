package pl.spribe.event;

public record UserCreatedEvent(Long userId, String username) {}
