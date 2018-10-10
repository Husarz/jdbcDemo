package aisystem.demo.jdbc.jdbcDemo;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;

@SpringBootApplication
@Slf4j
public class JdbcDemoApplication {

    public final static String SAMPLE_TAB =
            "    SELECT v,dd\n" +
                    "    FROM\n" +
                    "      (VALUES\n" +
                    "        (1, Date 'now'),\n" +
                    "        (2, (Date 'now' - INTERVAL '1 day') :: Date)\n" +
                    "      ) AS foo(v,dd)";

    public static void main(String[] args) {
        SpringApplication.run(JdbcDemoApplication.class, args);
    }

    @Bean
    CommandLineRunner getCurrentLocalDate(JdbcTemplate jdbcTemplate) {
        return __ -> Try.of(() -> jdbcTemplate
                .queryForObject("Select now()::Date", LocalDate.class))
                .forEach(d -> log.info("getCurrentLocalDate: {}", d));
    }

    @Bean
    CommandLineRunner checkCase(JdbcTemplate jdbcTemplate) {
        return __ -> Try.of(() ->
                jdbcTemplate.queryForObject(
                        "WITH foo as (" +
                                SAMPLE_TAB +
                                ") " +
                                "SELECT  v from foo " +
                                "WHERE dd = now()::Date",
                        Integer.class))
                .forEach(d -> log.info("checkCase: {}", d));
    }

    @Bean
    CommandLineRunner checkBooleanCase(JdbcTemplate jdbcTemplate) {
        return __ -> Try.of(() ->
                jdbcTemplate.queryForObject(
                        "WITH foo as (" +
                                SAMPLE_TAB +
                                ") SELECT v>0 from foo " +
                                "WHERE dd = now()::Date", Boolean.class))
                .forEach(d -> log.info("checkBooleanCase: {}", d));
    }

    @Bean
    CommandLineRunner shouldBeFalse(JdbcTemplate jdbcTemplate) {
        return __ -> Try.of(() ->
                jdbcTemplate.queryForObject(
                        "WITH foo as ("
                                +  SAMPLE_TAB
                                + ") SELECT CASE "
                                + " WHEN count(*)>0 THEN 1 ELSE 0 "
                                + "END FROM foo "
                                + "WHERE dd = (Date 'now' - INTERVAL '3 day')::Date", Boolean.class))
                .onFailure(ex -> log.error("jdbc call fail: ", ex))
                .forEach(d -> log.info("shouldBeFalse: {}", d));
    }
}
