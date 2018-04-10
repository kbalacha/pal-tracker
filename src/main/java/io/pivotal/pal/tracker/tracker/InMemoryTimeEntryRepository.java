package io.pivotal.pal.tracker.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {

    private Map<Long, TimeEntry> timeEntries = new HashMap<>();
    private AtomicLong id = new AtomicLong();

    public TimeEntry create(TimeEntry timeEntry) {
        timeEntry.setId(id.incrementAndGet());
        timeEntries.put(timeEntry.getId(), timeEntry);
        return timeEntry;
    }

    public TimeEntry find(long id) {
        return timeEntries.get(id);
    }

    public TimeEntry update(long id, TimeEntry timeEntry) {
        timeEntry.setId(id);
        timeEntries.put(id, timeEntry);
        return timeEntry;
    }

    public void delete(long id) {
        timeEntries.remove(id);
    }

    public List<TimeEntry> list() {
        return new ArrayList<>(timeEntries.values());
    }
}
