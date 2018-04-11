package io.pivotal.pal.tracker.tracker;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.util.List;

public class JdbcTimeEntryRepository implements TimeEntryRepository{

    private final JdbcTemplate jdbcTemplate;

    public JdbcTimeEntryRepository(MysqlDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        String insertSql = "INSERT INTO time_entries (project_id, user_id, date, hours) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update( new PreparedStatementCreator() {
                                 public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                                     PreparedStatement ps = connection.prepareStatement(insertSql, new String[] {"id"});
                                     ps.setLong(1, timeEntry.getProjectId());
                                     ps.setLong(2, timeEntry.getUserId());
                                     ps.setDate(3, Date.valueOf(timeEntry.getDate()));
                                     ps.setInt(4, timeEntry.getHours());
                                     return ps;
                                 }
                             },
                keyHolder);

        timeEntry.setId(keyHolder.getKey().longValue());
        return timeEntry;
    }

    @Override
    public TimeEntry find(long id) {
        try {
            TimeEntry timeEntry = jdbcTemplate.queryForObject("SELECT * from time_entries WHERE id = ?",
                    new Object[]{id},
                    new RowMapper<TimeEntry>() {
                        public TimeEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
                            TimeEntry timeEntry = new TimeEntry();
                            timeEntry.setId(rs.getLong("id"));
                            timeEntry.setProjectId(rs.getLong("project_id"));
                            timeEntry.setUserId(rs.getLong("user_id"));
                            timeEntry.setDate(rs.getDate("date").toLocalDate());
                            timeEntry.setHours(rs.getInt("hours"));
                            return timeEntry;
                        }
                    });
            return timeEntry;
        }catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    @Override
    public List<TimeEntry> list() {
        List<TimeEntry> timeEntries = jdbcTemplate.query("SELECT * from time_entries",
                new RowMapper<TimeEntry>() {
                    public TimeEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
                        TimeEntry timeEntry = new TimeEntry();
                        timeEntry.setId(rs.getLong("id"));
                        timeEntry.setProjectId(rs.getLong("project_id"));
                        timeEntry.setUserId(rs.getLong("user_id"));
                        timeEntry.setDate(rs.getDate("date").toLocalDate());
                        timeEntry.setHours(rs.getInt("hours"));
                        return timeEntry;
                    }
                });
        return timeEntries;
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        String updateSql = "UPDATE time_entries SET project_id= ?, user_id = ?, date = ?, hours = ? WHERE id = ?";
        jdbcTemplate.update(updateSql, new Object[]{timeEntry.getProjectId(), timeEntry.getUserId(), timeEntry.getDate(), timeEntry.getHours(), id});
        timeEntry.setId(id);
        return timeEntry;
    }

    @Override
    public void delete(long id) {
        String updateSql = "DELETE from time_entries WHERE id = ?";
        jdbcTemplate.update(updateSql, new Object[]{id});
    }
}
