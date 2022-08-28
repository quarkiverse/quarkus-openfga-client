package io.quarkiverse.openfga.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TupleOperation {
    @JsonProperty("TUPLE_OPERATION_WRITE")
    WRITE,
    @JsonProperty("TUPLE_OPERATION_DELETE")
    DELETE,
}
