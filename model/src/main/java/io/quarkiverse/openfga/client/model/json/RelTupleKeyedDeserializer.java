package io.quarkiverse.openfga.client.model.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.quarkiverse.openfga.client.model.RelTupleDefinition;
import io.quarkiverse.openfga.client.model.RelTupleKey;
import io.quarkiverse.openfga.client.model.RelTupleKeyed;

public class RelTupleKeyedDeserializer extends StdDeserializer<RelTupleKeyed> {

    public RelTupleKeyedDeserializer() {
        super(RelTupleKeyed.class);
    }

    @Override
    public RelTupleKeyed deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        var currentToken = p.currentToken();
        if (currentToken != JsonToken.START_OBJECT && currentToken != JsonToken.FIELD_NAME) {
            return (RelTupleKeyed) ctxt.handleUnexpectedToken(getClass(), JsonToken.START_OBJECT, p, "Expected JSON Object");
        }
        var node = p.getCodec().<ObjectNode> readTree(p);
        if (node.has("condition")) {
            return p.getCodec().treeToValue(node, RelTupleDefinition.class);
        } else {
            return p.getCodec().treeToValue(node, RelTupleKey.class);
        }
    }
}
