package account.controller;

import account.model.event.Event;
import account.service.EventService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("api/security")
public class SecurityController {

    private final EventService eventService;

    @GetMapping("events")
    public List<Event> getEvents() {
        return eventService.getAllEvents();
    }
}
