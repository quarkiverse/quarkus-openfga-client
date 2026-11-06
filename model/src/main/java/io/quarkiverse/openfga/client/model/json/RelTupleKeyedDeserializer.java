package io.quarkiverse.openfga.client.model.json;

import io.quarkiverse.openfga.client.model.RelTupleDefinition;
import io.quarkiverse.openfga.client.model.RelTupleKey;
import io.quarkiverse.openfga.client.model.RelTupleKeyed;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.node.ObjectNode;

public class RelTupleKeyedDeserializer extends StdDeserializer<RelTupleKeyed> {

    public RelTupleKeyedDeserializer() {
        super(RelTupleKeyed.class);
    }

    @Override
    public RelTupleKeyed deserialize(JsonParser p, DeserializationContext ctxt) {
        var currentToken = p.currentToken();
        if (currentToken != JsonToken.START_OBJECT && currentToken != JsonToken.PROPERTY_NAME) {
            return (RelTupleKeyed) ctxt.handleUnexpectedToken(ctxt.constructType(RelTupleKeyed.class), JsonToken.START_OBJECT,
                    p, "Expected JSON Object");
        }
        var node = (ObjectNode) ctxt.readTree(p);
        if (node.has("condition")) {
            return ctxt.readTreeAsValue(node, RelTupleDefinition.class);
        } else {
            return ctxt.readTreeAsValue(node, RelTupleKey.class);
        }
    }
}
