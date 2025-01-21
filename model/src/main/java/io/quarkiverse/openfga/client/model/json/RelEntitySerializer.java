package io.quarkiverse.openfga.client.model.json;

import java.io.IOException;

import javax.annotation.Nullable;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.ResolvableSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.type.TypeFactory;

import io.quarkiverse.openfga.client.model.RelEntity;

public class RelEntitySerializer extends StdSerializer<RelEntity> implements ContextualSerializer {

    private static RelEntitySerializer compactInstance = null;
    private static RelEntitySerializer objectInstance = null;

    @Nullable
    private final JsonSerializer<Object> delegate;

    public RelEntitySerializer() {
        this(null);
    }

    public RelEntitySerializer(@Nullable JsonSerializer<Object> delegate) {
        super(RelEntity.class);
        this.delegate = delegate;
    }

    @Override
    public void serialize(RelEntity value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (delegate != null) {
            delegate.serialize(value, gen, provider);
        } else {
            gen.writeString(value.toString());
        }
    }

    @Override
    public RelEntitySerializer createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        var format = findFormatOverrides(prov, property, handledType());
        if (format != null && format.hasShape()) {
            var shape = format.getShape();
            switch (shape) {
                case OBJECT:
                    return getObjectSerializer(prov);
                case SCALAR, STRING, NATURAL, ANY:
                    return getCompactSerializer(prov);
                default:
                    prov.reportBadDefinition(handledType(), String.format("Unsupported serialization shape: %s", shape));
            }
        }
        return this;
    }

    private static synchronized RelEntitySerializer getObjectSerializer(SerializerProvider prov) throws JsonMappingException {
        if (objectInstance == null) {
            var type = TypeFactory.defaultInstance().constructType(RelEntity.class);
            var beanDesc = prov.getConfig().introspect(type);
            var defaultSerializer = BeanSerializerFactory.instance.findBeanOrAddOnSerializer(prov, type, beanDesc, false);
            if (defaultSerializer instanceof ResolvableSerializer resolvable) {
                resolvable.resolve(prov);
            }
            objectInstance = new RelEntitySerializer(defaultSerializer);
        }
        return objectInstance;
    }

    private static synchronized RelEntitySerializer getCompactSerializer(SerializerProvider prov) {
        if (compactInstance == null) {
            compactInstance = new RelEntitySerializer(null);
        }
        return compactInstance;
    }
}
