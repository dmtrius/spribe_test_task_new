package pl.spribe.component;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pl.spribe.event.UserCreatedEvent;

@Component
public class UserEventListener {
    @Async
    @EventListener
    public void handleUserCreated(UserCreatedEvent event) {
        // Handle the event: e.g., log, send notification, audit, etc.
        System.out.println("User created: " + event.username() + " (ID: " + event.userId() + ")");
        // You can also trigger further business logic here
    }

//    @EventListener
//    public void handleUserUpdated(UserUpdatedEvent event) { /* ... */ }
//
//    @EventListener
//    public void handleUserDeleted(UserDeletedEvent event) { /* ... */ }
}
