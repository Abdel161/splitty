package server.api;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import server.service.AdminService;
import server.service.EventPollingService;
import commons.dtos.EventDump;

@Controller
@ResponseBody
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final EventPollingService eventPollingService;

    /**
     * Creates an instance of the AdminController.
     *
     * @param adminService        Service for admin.
     * @param eventPollingService EventPollingService instance for long-polling event updates
     */
    public AdminController(AdminService adminService, EventPollingService eventPollingService) {
        this.adminService = adminService;
        this.eventPollingService = eventPollingService;
    }

    /**
     * GET `/api/admin/validate-password` endpoint
     *
     * @param password provided password to check
     * @return 200 OK if the password is correct and 403 Forbidden if it is not
     */
    @GetMapping("/validate-password")
    public ResponseEntity<?> validatePassword(@RequestParam String password) {
        if (!adminService.isPasswordValid(password)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok().build();
    }

    /**
     * GET `/api/admin/event-dump/{id}` endpoint
     *
     * @param id provided id of event to download JSON dump
     * @return the JSON dump of the specified event
     */
    @GetMapping("/event-dump/{id}")
    public ResponseEntity<EventDump> downloadDump(@PathVariable("id") Long id) {
        return ResponseEntity.ok(adminService.getEventDump(id));
    }

    /**
     * POST `/api/admin/event-dump/` endpoint
     *
     * @param eventDump to upload to the server
     * @return 200 OK if the dump was uploaded successfully
     */
    @PostMapping("/event-dump")
    public ResponseEntity<?> uploadDump(@RequestBody EventDump eventDump) {
        adminService.uploadEventDump(eventDump);

        if (eventPollingService != null) {
            eventPollingService.sendEventsToListeners();
        }

        return ResponseEntity.ok().build();
    }
}
