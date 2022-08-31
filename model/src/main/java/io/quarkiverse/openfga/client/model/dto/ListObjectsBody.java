package io.quarkiverse.openfga.client.model.dto;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.ContextualTupleKeys;

public final class ListObjectsBody {
    @JsonProperty("authorization_model_id")
    @Nullable
    private final String authorizationModelId;
    @Nullable
    private final String type;
    @Nullable
    private final String relation;
    @Nullable
    private final String user;
    @JsonProperty("contextual_tuples")
    @Nullable
    private final ContextualTupleKeys contextualTupleKeys;

    @JsonCreator(mode = PROPERTIES)
    public ListObjectsBody(@JsonProperty("authorization_model_id") @Nullable String authorizationModelId,
            @Nullable String type, @Nullable String relation, @Nullable String user,
            @Nullable @JsonProperty("contextual_tuples") ContextualTupleKeys contextualTupleKeys) {
        this.authorizationModelId = authorizationModelId;
        this.type = type;
        this.relation = relation;
        this.user = user;
        this.contextualTupleKeys = contextualTupleKeys;
    }

    @JsonProperty("authorization_model_id")
    @Nullable
    public String getAuthorizationModelId() {
        return authorizationModelId;
    }

    @Nullable
    public String getType() {
        return type;
    }

    @Nullable
    public String getRelation() {
        return relation;
    }

    @Nullable
    public String getUser() {
        return user;
    }

    @JsonProperty("contextual_tuples")
    @Nullable
    public ContextualTupleKeys getContextualTupleKeys() {
        return contextualTupleKeys;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (ListObjectsBody) obj;
        return Objects.equals(this.authorizationModelId, that.authorizationModelId) &&
                Objects.equals(this.type, that.type) &&
                Objects.equals(this.relation, that.relation) &&
                Objects.equals(this.user, that.user) &&
                Objects.equals(this.contextualTupleKeys, that.contextualTupleKeys);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorizationModelId, type, relation, user, contextualTupleKeys);
    }

    @Override
    public String toString() {
        return "ListObjectsBody[" +
                "authorizationModelId=" + authorizationModelId + ", " +
                "type=" + type + ", " +
                "relation=" + relation + ", " +
                "user=" + user + ", " +
                "contextualTupleKeys=" + contextualTupleKeys + ']';
    }

}
