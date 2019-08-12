package aisystem.demo.jdbc.jdbcDemo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

@Slf4j
public class CodeReview {

    private Address a1 = new Address("dd1", "21/3");
    private Address a2 = new Address("dd2", "21/4");
    private Person manager = new Person("John", "alekkkk", Collections.singletonList(a1));
    private List<Address> addressList = new ArrayList<Address>() {{
        add(a1);
        add(a2);
    }};
    private Person p1 = new Person("alek", "alekkkk", addressList);
    private List<Person> persons = new ArrayList<Person>() {{
        add(p1);
    }};
    private Departament departament = new Departament(1L, "dep1", manager, persons);


    @Test
    public void letManagerOptional() {
        //when
        final String managerName = Optional.ofNullable(departament)
                .map(d -> d.getManager().getName())
                .get();

        //then
        assertEquals(managerName, "John");
    }

    @Test
    public  void letTakeAddresses(){
        List<Address> addressList = new ArrayList<>();
        //when
        final List<Address> address = departament.personList.stream()
                .map(p -> p.addresses.stream()
                        .map(a -> {
                            addressList.add(a);
                            return addressList;
                        })
                        .findFirst()
                        .get()
                )
                .findAny()
                .get();


       address.forEach(a -> log.info(a.getStreet()));
    }


}


@Data
@Builder
@AllArgsConstructor
class Departament {

    Long id;
    String departamentName;
    Person manager;

    List<Person> personList;
}

@Data
@Builder
@AllArgsConstructor
class Person {
    String name;
    String surname;
    List<Address> addresses;
}


@Data
@Builder
@AllArgsConstructor
class Address {
    String street;
    String number;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class Building {
    Long id;
    String name;
}