package io.quarkiverse.openfga.client.model.json;

import io.quarkiverse.openfga.client.model.RelEntity;
import io.quarkiverse.openfga.client.model.RelObject;
import io.quarkiverse.openfga.client.model.RelUser;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.node.ObjectNode;

public class RelEntityDeserializer extends StdDeserializer<RelEntity> {

    public RelEntityDeserializer() {
        super(RelEntity.class);
    }

    @Override
    public RelEntity deserialize(JsonParser p, DeserializationContext ctxt) {
        var currentToken = p.currentToken();
        if (currentToken == JsonToken.VALUE_STRING) {
            if (p.getString().contains("#")) {
                return RelUser.valueOf(p.getString());
            } else {
                return RelObject.valueOf(p.getString());
            }
        } else if (currentToken == JsonToken.START_OBJECT) {
            var object = (ObjectNode) ctxt.readTree(p);
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
