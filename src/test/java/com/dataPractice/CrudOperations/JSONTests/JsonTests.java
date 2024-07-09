package com.dataPractice.CrudOperations.JSONTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import com.dataPractice.CrudOperations.Entities.People;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

@JsonTest
public class JsonTests {

    @Autowired
    private JacksonTester<People> json;

    @Test
    void userJsonSerializationTest() throws IOException{
        People user = new People(1L, "Benjamin", "Triggiani", 27, "biking, running, gaming, watching tv, studying", "btriggiani");
        assertThat(json.write(user)).isStrictlyEqualToJson("/resources/expected.json");
        assertThat(json.write(user)).hasJsonPathNumberValue("@.userId");
        assertThat(json.write(user)).extractingJsonPathNumberValue("@.userId").isEqualTo(1);
        assertThat(json.write(user)).hasJsonPathStringValue("@.firstName");
        assertThat(json.write(user)).extractingJsonPathStringValue("@.firstName").isEqualTo("Benjamin");
        assertThat(json.write(user)).hasJsonPathStringValue("@.lastName");
        assertThat(json.write(user)).extractingJsonPathStringValue("@.lastName").isEqualTo("Triggiani");
        assertThat(json.write(user)).hasJsonPathNumberValue("@.age");
        assertThat(json.write(user)).extractingJsonPathNumberValue("@.age").isEqualTo(27);
        assertThat(json.write(user)).hasJsonPathStringValue("@.hobbies");
        assertThat(json.write(user)).extractingJsonPathStringValue("@.hobbies").isEqualTo("biking, running, gaming, watching tv, studying");
        assertThat(json.write(user)).hasJsonPathStringValue("@.owner");
        assertThat(json.write(user)).extractingJsonPathStringValue("@.owner").isEqualTo("btriggiani");
    }

    @Test
    void userJsonDeserializationTest() throws IOException{
        String expected = """
                {
                    "userId": 1,
                    "firstName": "Benjamin",
                    "lastName": "Triggiani",
                    "age": 27,
                    "hobbies": "biking, running, gaming, watching tv, studying",
                    "owner": "btriggiani"
                }""";

        assertThat(json.parse(expected)).isEqualTo(new People(1L, "Benjamin", "Triggiani", 27, "biking, running, gaming, watching tv, studying", "btriggiani"));
    }
}
