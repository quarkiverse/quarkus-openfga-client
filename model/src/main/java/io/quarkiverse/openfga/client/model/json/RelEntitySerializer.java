package io.quarkiverse.openfga.client.model.json;

import io.quarkiverse.openfga.client.model.RelEntity;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.ser.std.StdSerializer;

public class RelEntitySerializer extends StdSerializer<RelEntity> {

    private static final RelEntitySerializer compactInstance = new RelEntitySerializer(false);
    private static final RelEntitySerializer objectInstance = new RelEntitySerializer(true);

    private final boolean asObject;

    public RelEntitySerializer() {
        this(false);
    }

    private RelEntitySerializer(boolean asObject) {
        super(RelEntity.class);
        this.asObject = asObject;
    }

    @Override
    public void serialize(RelEntity value, JsonGenerator gen, SerializationContext ctxt) {
        if (asObject) {
            gen.writeStartObject();
            gen.writeStringProperty("type", value.getType());
            gen.writeStringProperty("id", value.getId());
            gen.writeEndObject();
        } else {
            gen.writeString(value.toString());
        }
    }

    @Override
    public ValueSerializer<?> createContextual(SerializationContext ctxt, BeanProperty property) {
        var format = findFormatOverrides(ctxt, property, handledType());
        if (format != null && format.hasShape()) {
            var shape = format.getShape();
            switch (shape) {
                case OBJECT:
                    return objectInstance;
                case SCALAR, STRING, NATURAL, ANY:
                    return compactInstance;
                default:
                    return ctxt.reportBadDefinition(handledType(),
                            String.format("Unsupported serialization shape: %s", shape));
            }
        }
        return this;
    }
}
