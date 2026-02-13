package io.quarkiverse.openfga.client.model.utils;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

public class ModelMapper {

    public static final ObjectMapper mapper = new JsonMapper()
            .registerModule(new JavaTimeModule())
            .registerModule(new Jdk8Module())
            .registerModule(new ParameterNamesModule())
            .setDefaultPropertyInclusion(JsonInclude.Value.construct(NON_NULL, NON_NULL));

}
