package io.quarkiverse.openfga.client.model.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import io.quarkiverse.openfga.client.model.dto.WriteRequest;

/**
 * Serializes a write request while preserving its compatibility-oriented Java shape.
 */
public final class WriteRequestSerializer extends StdSerializer<WriteRequest> {

    /** Creates a write request serializer. */
    public WriteRequestSerializer() {
        super(WriteRequest.class);
    }

    @Override
    public void serialize(WriteRequest request, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeStartObject();
        if (request.getWrites() != null) {
            generator.writeObjectFieldStart("writes");
            generator.writeFieldName("tuple_keys");
            provider.defaultSerializeValue(request.getWrites().tupleKeys(), generator);
            if (request.getOnDuplicate() != null) {
                generator.writeObjectField("on_duplicate", request.getOnDuplicate());
            }
            generator.writeEndObject();
        }
        if (request.getDeletes() != null) {
            generator.writeObjectFieldStart("deletes");
            generator.writeFieldName("tuple_keys");
            provider.defaultSerializeValue(
                    request.getDeletes().tupleKeys().stream().map(tupleKey -> tupleKey.key()).toList(), generator);
            if (request.getOnMissing() != null) {
                generator.writeObjectField("on_missing", request.getOnMissing());
            }
            generator.writeEndObject();
        }
        if (request.getAuthorizationModelId() != null) {
            generator.writeStringField("authorization_model_id", request.getAuthorizationModelId());
        }
        generator.writeEndObject();
    }
}
