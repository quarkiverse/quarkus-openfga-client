package io.quarkiverse.openfga.client.model;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FGAException extends Exception {

    public enum Code {
        UNKNOWN_ERROR,

        @JsonProperty("no_error")
        NO_ERROR,

        @JsonProperty("validation_error")
        VALIDATION_ERROR,

        @JsonProperty("authorization_model_not_found")
        AUTHORIZATION_MODEL_NOT_FOUND,

        @JsonProperty("authorization_model_resolution_too_complex")
        AUTHORIZATION_MODEL_RESOLUTION_TOO_COMPLEX,

        @JsonProperty("invalid_write_input")
        INVALID_WRITE_INPUT,

        @JsonProperty("cannot_allow_duplicate_tuples_in_one_request")
        CANNOT_ALLOW_DUPLICATE_TUPLES_IN_ONE_REQUEST,

        @JsonProperty("cannot_allow_duplicate_types_in_one_request")
        CANNOT_ALLOW_DUPLICATE_TYPES_IN_ONE_REQUEST,

        @JsonProperty("cannot_allow_multiple_references_to_one_relation")
        CANNOT_ALLOW_MULTIPLE_REFERENCES_TO_ONE_RELATION,

        @JsonProperty("invalid_continuation_token")
        INVALID_CONTINUATION_TOKEN,

        @JsonProperty("invalid_tuple_set")
        INVALID_TUPLE_SET,

        @JsonProperty("invalid_check_input")
        INVALID_CHECK_INPUT,

        @JsonProperty("invalid_expand_input")
        INVALID_EXPAND_INPUT,

        @JsonProperty("unsupported_user_set")
        UNSUPPORTED_USER_SET,

        @JsonProperty("invalid_object_format")
        INVALID_OBJECT_FORMAT,

        @JsonProperty("write_failed_due_to_invalid_input")
        WRITE_FAILED_DUE_TO_INVALID_INPUT,

        @JsonProperty("authorization_model_assertions_not_found")
        AUTHORIZATION_MODEL_ASSERTIONS_NOT_FOUND,

        @JsonProperty("latest_authorization_model_not_found")
        LATEST_AUTHORIZATION_MODEL_NOT_FOUND,

        @JsonProperty("type_not_found")
        TYPE_NOT_FOUND,

        @JsonProperty("relation_not_found")
        RELATION_NOT_FOUND,

        @JsonProperty("empty_relation_definition")
        EMPTY_RELATION_DEFINITION,

        @JsonProperty("invalid_user")
        INVALID_USER,

        @JsonProperty("invalid_tuple")
        INVALID_TUPLE,

        @JsonProperty("unknown_relation")
        UNKNOWN_RELATION,

        @JsonProperty("store_id_invalid_length")
        STORE_ID_INVALID_LENGTH,

        @JsonProperty("assertions_too_many_items")
        ASSERTIONS_TOO_MANY_ITEMS,

        @JsonProperty("id_too_long")
        ID_TOO_LONG,

        @JsonProperty("authorization_model_id_too_long")
        AUTHORIZATION_MODEL_ID_TOO_LONG,

        @JsonProperty("tuple_key_value_not_specified")
        TUPLE_KEY_VALUE_NOT_SPECIFIED,

        @JsonProperty("tuple_keys_too_many_or_too_few_items")
        TUPLE_KEYS_TOO_MANY_OR_TOO_FEW_ITEMS,

        @JsonProperty("page_size_invalid")
        PAGE_SIZE_INVALID,

        @JsonProperty("param_missing_value")
        PARAM_MISSING_VALUE,

        @JsonProperty("difference_base_missing_value")
        DIFFERENCE_BASE_MISSING_VALUE,

        @JsonProperty("subtract_base_missing_value")
        SUBTRACT_BASE_MISSING_VALUE,

        @JsonProperty("object_too_long")
        OBJECT_TOO_LONG,

        @JsonProperty("relation_too_long")
        RELATION_TOO_LONG,

        @JsonProperty("type_definitions_too_few_items")
        TYPE_DEFINITIONS_TOO_FEW_ITEMS,

        @JsonProperty("type_invalid_length")
        TYPE_INVALID_LENGTH,

        @JsonProperty("type_invalid_pattern")
        TYPE_INVALID_PATTERN,

        @JsonProperty("relations_too_few_items")
        RELATIONS_TOO_FEW_ITEMS,

        @JsonProperty("relations_too_long")
        RELATIONS_TOO_LONG,

        @JsonProperty("relations_invalid_pattern")
        RELATIONS_INVALID_PATTERN,

        @JsonProperty("object_invalid_pattern")
        OBJECT_INVALID_PATTERN,

        @JsonProperty("query_string_type_continuation_token_mismatch")
        QUERY_STRING_TYPE_CONTINUATION_TOKEN_MISMATCH,

        @JsonProperty("exceeded_entity_limit")
        EXCEEDED_ENTITY_LIMIT,

        @JsonProperty("invalid_contextual_tuple")
        INVALID_CONTEXTUAL_TUPLE,

        @JsonProperty("duplicate_contextual_tuple")
        DUPLICATE_CONTEXTUAL_TUPLE
    }

    private final Code code;

    @JsonCreator(mode = PROPERTIES)
    public FGAException(@JsonProperty("code") Code code, @JsonProperty("message") @Nullable String message) {
        super(message);
        this.code = code;
    }

    public Code getCode() {
        return code;
    }
}
