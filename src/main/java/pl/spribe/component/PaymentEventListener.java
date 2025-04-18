package pl.spribe.component;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.EventListener;
// import org.springframework.context.event.SimpleApplicationEventMulticaster;
// import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pl.spribe.event.PaymentMadeEvent;

@Component
class PaymentEventListener {

    @Async
    @EventListener
    public void handlePaymentEvent(PaymentMadeEvent event) {
        // This runs in a different thread
        // e.g., send notification, log, etc.
    }

//    @Bean(name = "applicationEventMulticaster")
//    public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
//        SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
//        eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor());
//        return eventMulticaster;
//    }
}
