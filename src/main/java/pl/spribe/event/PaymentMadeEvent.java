package pl.spribe.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PaymentMadeEvent extends ApplicationEvent {
    private final Long bookingId;
    public PaymentMadeEvent(Object source, Long bookingId) {
        super(source);
        this.bookingId = bookingId;
    }
}
