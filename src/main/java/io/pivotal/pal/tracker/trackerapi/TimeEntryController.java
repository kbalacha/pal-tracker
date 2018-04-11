package io.pivotal.pal.tracker.trackerapi;

import io.pivotal.pal.tracker.tracker.TimeEntry;
import io.pivotal.pal.tracker.tracker.TimeEntryRepository;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/time-entries")
public class TimeEntryController {

    private final TimeEntryRepository timeEntryRepository;
    private final CounterService counter;
    private final GaugeService gauge;

    public TimeEntryController(TimeEntryRepository timeEntryRepository,
                               CounterService counter, GaugeService gauge) {
        this.timeEntryRepository = timeEntryRepository;
        this.counter = counter;
        this.gauge = gauge;
    }

    @PostMapping
    public ResponseEntity<TimeEntry> create(@RequestBody TimeEntry timeEntry) {
        TimeEntry timeEntryCreated = timeEntryRepository.create(timeEntry);
        counter.increment("TimeEntry.created");
        gauge.submit("timeEntries.count", timeEntryRepository.list().size());
        return new ResponseEntity<>(timeEntryCreated, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<TimeEntry> read(@PathVariable("id") long id) {
        TimeEntry timeEntry = timeEntryRepository.find(id);
        if (timeEntry != null) {
            counter.increment("TimeEntry.read");
            return new ResponseEntity<>(timeEntry, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<TimeEntry>> list() {
        counter.increment("TimeEntry.listed");
        return ResponseEntity.ok(timeEntryRepository.list());
    }

    @PutMapping("{id}")
    public ResponseEntity<TimeEntry> update(@PathVariable("id") long id, @RequestBody TimeEntry timeEntry) {
        TimeEntry timeEntryUpdated = timeEntryRepository.update(id, timeEntry);
        if (timeEntryUpdated != null) {
            counter.increment("TimeEntry.updated");
            return new ResponseEntity<>(timeEntryUpdated, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }    }

    @DeleteMapping("{id}")
    public ResponseEntity<TimeEntry> delete(@PathVariable("id") long id) {
        timeEntryRepository.delete(id);
        counter.increment("TimeEntry.deleted");
        gauge.submit("timeEntries.count", timeEntryRepository.list().size());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
