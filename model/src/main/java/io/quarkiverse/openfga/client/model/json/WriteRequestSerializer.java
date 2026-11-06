package io.quarkiverse.openfga.client.model.json;

import io.quarkiverse.openfga.client.model.dto.WriteRequest;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

/**
 * Serializes a write request while preserving its compatibility-oriented Java shape.
 */
public final class WriteRequestSerializer extends StdSerializer<WriteRequest> {

    /** Creates a write request serializer. */
    public WriteRequestSerializer() {
        super(WriteRequest.class);
    }

    @Override
    public void serialize(WriteRequest request, JsonGenerator generator, SerializationContext context) {
        generator.writeStartObject();
        if (request.getWrites() != null) {
            generator.writeObjectPropertyStart("writes");
            generator.writeName("tuple_keys");
            context.writeValue(generator, request.getWrites().tupleKeys());
            if (request.getOnDuplicate() != null) {
                generator.writePOJOProperty("on_duplicate", request.getOnDuplicate());
            }
            generator.writeEndObject();
        }
        if (request.getDeletes() != null) {
            generator.writeObjectPropertyStart("deletes");
            generator.writeName("tuple_keys");
            context.writeValue(generator,
                    request.getDeletes().tupleKeys().stream().map(tupleKey -> tupleKey.key()).toList());
            if (request.getOnMissing() != null) {
                generator.writePOJOProperty("on_missing", request.getOnMissing());
            }
            generator.writeEndObject();
        }
        if (request.getAuthorizationModelId() != null) {
            generator.writeStringProperty("authorization_model_id", request.getAuthorizationModelId());
        }
        generator.writeEndObject();
    }
}
