package aisystem.demo.jdbc.jdbcDemo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JdbcDemoApplicationTests {

    //	@Test
//	public void contextLoads() {
//	}
    @Autowired
    JdbcTemplate jdbcTemplate;
    final long NEXTLONGID = abs(new Random().nextLong()) % 99;

    @Before
    public void init() {
        jdbcTemplate.execute("DELETE from building");

    }

    @Test
    public void ttt1() {
        //given
        jdbcTemplate.execute("Insert into building (id, name) values (?, 'not')",
                (PreparedStatementCallback<Boolean>) this::getaBooleanWithIdStatement);

        //when
        Building buildings = jdbcTemplate.queryForObject("UPDATE building set name = 'inprogrss' " +
                "where name = 'not' " +
                "returning id, name", new Object[]{}, new BeanPropertyRowMapper<>(Building.class));

        //then
        assertThat(buildings, notNullValue());
        assertThat(buildings.name, is("inprogrss"));
        assertThat(buildings.id, is(NEXTLONGID));
    }

    @Test
    public void ttt2() {
        //given
        jdbcTemplate.execute("Insert into building (id, name) values (?, 'not')",
                (PreparedStatementCallback<Boolean>) this::getaBooleanWithIdStatement);

        jdbcTemplate.queryForObject("UPDATE building set name = 'inprogrss' " +
                "where name = 'not' " +
                "returning id, name", new Object[]{}, new BeanPropertyRowMapper<>(Building.class));

        //when
        jdbcTemplate.execute("UPDATE building set name = 'DONE' " +
                        "where id = ? ",
                (PreparedStatementCallback<Boolean>) this::getaBooleanWithIdStatement);

        //then
        Building buildings = jdbcTemplate.queryForObject("SELECT id, name from building " +
                        "where id = ? ",
                new Object[]{NEXTLONGID}, new BeanPropertyRowMapper<>(Building.class));
        assertThat(buildings, notNullValue());
        assertThat(buildings.name, is("DONE"));
        assertThat(buildings.id, is(NEXTLONGID));
    }

    private Boolean getaBooleanWithIdStatement(PreparedStatement ps) throws SQLException {
        ps.setLong(1, NEXTLONGID);
        return ps.execute();
    }
}
