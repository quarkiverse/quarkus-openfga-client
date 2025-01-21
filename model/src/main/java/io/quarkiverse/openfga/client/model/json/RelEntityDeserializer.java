package io.quarkiverse.openfga.client.model.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.quarkiverse.openfga.client.model.RelEntity;
import io.quarkiverse.openfga.client.model.RelObject;
import io.quarkiverse.openfga.client.model.RelUser;

public class RelEntityDeserializer extends StdDeserializer<RelEntity> {

    public RelEntityDeserializer() {
        super(RelEntity.class);
    }

    @Override
    public RelEntity deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        var currentToken = p.currentToken();
        if (currentToken == JsonToken.VALUE_STRING) {
            if (p.getText().contains("#")) {
                return RelUser.valueOf(p.getText());
            } else {
                return RelObject.valueOf(p.getText());
            }
        } else if (currentToken == JsonToken.START_OBJECT) {
            var object = p.getCodec().readValue(p, ObjectNode.class);
            if (object.has("relation")) {
                return ctxt.readTreeAsValue(object, RelUser.class);
            } else {
                return ctxt.readTreeAsValue(object, RelObject.class);
            }
        } else {
            return (RelEntity) ctxt.handleUnexpectedToken(RelEntity.class, p);
        }
    }
}
