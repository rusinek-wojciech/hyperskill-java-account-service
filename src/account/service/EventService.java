package account.service;

import account.model.event.Action;
import account.model.event.Event;
import account.model.user.User;
import account.repository.EventRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class EventService {

    private final HttpServletRequest request;
    private final EventRepository eventRepository;

    public void log(Action action, String object, String username) {
        log.info(action.name() + ": " + object);
        Event event = Event.builder()
                .date(new Date())
                .action(action)
                .subject(username == null ? "Anonymous" : username)
                .object(object)
                .path(request.getRequestURI().substring(request.getContextPath().length()))
                .build();
        eventRepository.save(event);
    }

    public void log(Action action, String object, User user) {
        this.log(action, object, user.getUsername());
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

}
