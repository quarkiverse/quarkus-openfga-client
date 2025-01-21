package io.quarkiverse.openfga.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ErrorCode {

    NO_ERROR("no_error"),

    VALIDATION_ERROR("validation_error"),

    AUTHORIZATION_MODEL_NOT_FOUND("authorization_model_not_found"),

    AUTHORIZATION_MODEL_RESOLUTION_TOO_COMPLEX("authorization_model_resolution_too_complex"),

    INVALID_WRITE_INPUT("invalid_write_input"),

    CANNOT_ALLOW_DUPLICATE_TUPLES_IN_ONE_REQUEST("cannot_allow_duplicate_tuples_in_one_request"),

    CANNOT_ALLOW_DUPLICATE_TYPES_IN_ONE_REQUEST("cannot_allow_duplicate_types_in_one_request"),

    CANNOT_ALLOW_MULTIPLE_REFERENCES_TO_ONE_RELATION("cannot_allow_multiple_references_to_one_relation"),

    INVALID_CONTINUATION_TOKEN("invalid_continuation_token"),

    INVALID_TUPLE_SET("invalid_tuple_set"),

    INVALID_CHECK_INPUT("invalid_check_input"),

    INVALID_EXPAND_INPUT("invalid_expand_input"),

    UNSUPPORTED_USER_SET("unsupported_user_set"),

    INVALID_OBJECT_FORMAT("invalid_object_format"),

    WRITE_FAILED_DUE_TO_INVALID_INPUT("write_failed_due_to_invalid_input"),

    AUTHORIZATION_MODEL_ASSERTIONS_NOT_FOUND("authorization_model_assertions_not_found"),

    LATEST_AUTHORIZATION_MODEL_NOT_FOUND("latest_authorization_model_not_found"),

    TYPE_NOT_FOUND("type_not_found"),

    RELATION_NOT_FOUND("relation_not_found"),

    EMPTY_RELATION_DEFINITION("empty_relation_definition"),

    INVALID_USER("invalid_user"),

    INVALID_TUPLE("invalid_tuple"),

    UNKNOWN_RELATION("unknown_relation"),

    STORE_ID_INVALID_LENGTH("store_id_invalid_length"),

    ASSERTIONS_TOO_MANY_ITEMS("assertions_too_many_items"),

    ID_TOO_LONG("id_too_long"),

    AUTHORIZATION_MODEL_ID_TOO_LONG("authorization_model_id_too_long"),

    TUPLE_KEY_VALUE_NOT_SPECIFIED("tuple_key_value_not_specified"),

    TUPLE_KEYS_TOO_MANY_OR_TOO_FEW_ITEMS("tuple_keys_too_many_or_too_few_items"),

    PAGE_SIZE_INVALID("page_size_invalid"),

    PARAM_MISSING_VALUE("param_missing_value"),

    DIFFERENCE_BASE_MISSING_VALUE("difference_base_missing_value"),

    SUBTRACT_BASE_MISSING_VALUE("subtract_base_missing_value"),

    OBJECT_TOO_LONG("object_too_long"),

    RELATION_TOO_LONG("relation_too_long"),

    TYPE_DEFINITIONS_TOO_FEW_ITEMS("type_definitions_too_few_items"),

    TYPE_INVALID_LENGTH("type_invalid_length"),

    TYPE_INVALID_PATTERN("type_invalid_pattern"),

    RELATIONS_TOO_FEW_ITEMS("relations_too_few_items"),

    RELATIONS_TOO_LONG("relations_too_long"),

    RELATIONS_INVALID_PATTERN("relations_invalid_pattern"),

    OBJECT_INVALID_PATTERN("object_invalid_pattern"),

    QUERY_STRING_TYPE_CONTINUATION_TOKEN_MISMATCH("query_string_type_continuation_token_mismatch"),

    EXCEEDED_ENTITY_LIMIT("exceeded_entity_limit"),

    INVALID_CONTEXTUAL_TUPLE("invalid_contextual_tuple"),

    DUPLICATE_CONTEXTUAL_TUPLE("duplicate_contextual_tuple"),

    INVALID_AUTHORIZATION_MODEL("invalid_authorization_model"),

    UNSUPPORTED_SCHEMA_VERSION("unsupported_schema_version"),

    CANCELLED("cancelled"),

    INVALID_START_TIME("invalid_start_time"),

    UNKNOWN("unknown"),

    ;

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    ErrorCode(String name) {
        this.value = name;
    }

    @JsonCreator
    public static ErrorCode fromValue(String value) {
        for (ErrorCode e : ErrorCode.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        return UNKNOWN;
    }
}
