package io.quarkiverse.openfga.client.model.dto;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.*;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class ReadRequest {

    public static final class TupleKeyFilter {

        public static final class Builder {

            @Nullable
            private RelTyped typeOrObject;
            @Nullable
            private String relation;
            @Nullable
            private RelUser user;

            private Builder() {
            }

            public Builder typeOrObject(@Nullable RelTyped typeOrObject) {
                this.typeOrObject = typeOrObject;
                return this;
            }

            public Builder relation(@Nullable String relation) {
                this.relation = relation;
                return this;
            }

            public Builder user(@Nullable RelUser user) {
                this.user = user;
                return this;
            }

            public TupleKeyFilter build() {
                if (typeOrObject instanceof RelObjectType type) {
                    var typeObject = RelObject.typeOnly(type.getType());
                    var user = Preconditions.parameterNonNull(this.user, "user",
                            ignore -> "User must be specified when object is a type only");
                    return new TupleKeyFilter(typeObject, relation, user);
                } else if (typeOrObject instanceof RelObject object) {
                    return new TupleKeyFilter(object, relation, user);
                } else if (typeOrObject == null) {
                    return new TupleKeyFilter(null, relation, user);
                } else {
                    throw new IllegalArgumentException("typeOrObject is invalid: " + typeOrObject);
                }
            }
        }

        public static Builder builder() {
            return new Builder();
        }

        @Nullable
        private final RelObject object;
        @Nullable
        private final String relation;
        @Nullable
        private final RelUser user;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        TupleKeyFilter(@JsonProperty("object") @Nullable RelObject object,
                @JsonProperty("relation") @Nullable String relation,
                @JsonProperty("user") @Nullable RelUser user) {
            this.object = object;
            this.relation = relation;
            this.user = user;
        }

        @Nullable
        public RelObject getObject() {
            return object;
        }

        @Nullable
        public String getRelation() {
            return relation;
        }

        @Nullable
        public RelUser getUser() {
            return user;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (!(obj instanceof TupleKeyFilter that))
                return false;
            return Objects.equals(this.object, that.object) &&
                    Objects.equals(this.relation, that.relation) &&
                    Objects.equals(this.user, that.user);
        }

        @Override
        public int hashCode() {
            return Objects.hash(object, relation, user);
        }

        @Override
        public String toString() {
            return "PartialTupleKey[" +
                    "object=" + object + ", " +
                    "relation=" + relation + ", " +
                    "user=" + user + ']';
        }

    }

    public static final class Builder {

        @Nullable
        private TupleKeyFilter tupleKey;
        @Nullable
        private String authorizationModelId;
        @Nullable
        private Integer pageSize;
        @Nullable
        private String continuationToken;

        private Builder() {
        }

        public Builder tupleKey(@Nullable TupleKeyFilter tupleKey) {
            // If the tupleKey is null or has all null fields, then it is considered null
            if (tupleKey != null && tupleKey.object == null && tupleKey.relation == null && tupleKey.user == null) {
                tupleKey = null;
            }
            this.tupleKey = tupleKey;
            return this;
        }

        public Builder authorizationModelId(@Nullable String authorizationModelId) {
            this.authorizationModelId = authorizationModelId;
            return this;
        }

        public Builder pageSize(@Nullable Integer pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public Builder continuationToken(@Nullable String continuationToken) {
            this.continuationToken = continuationToken;
            return this;
        }

        public ReadRequest build() {
            return new ReadRequest(tupleKey, authorizationModelId, pageSize, continuationToken);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Nullable
    private final TupleKeyFilter tupleKey;
    @Nullable
    private final String authorizationModelId;
    @Nullable
    private final Integer pageSize;
    @Nullable
    private final String continuationToken;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    ReadRequest(@JsonProperty("tuple_key") @Nullable TupleKeyFilter tupleKey,
            @JsonProperty("authorization_model_id") @Nullable String authorizationModelId,
            @JsonProperty("page_size") @Nullable Integer pageSize,
            @JsonProperty("continuation_token") @Nullable String continuationToken) {
        this.tupleKey = tupleKey;
        this.authorizationModelId = authorizationModelId;
        this.pageSize = pageSize;
        this.continuationToken = continuationToken;
    }

    @JsonProperty("tuple_key")
    @Nullable
    public TupleKeyFilter getTupleKey() {
        return tupleKey;
    }

    @JsonProperty("authorization_model_id")
    @Nullable
    public String getAuthorizationModelId() {
        return authorizationModelId;
    }

    @JsonProperty("page_size")
    @Nullable
    public Integer getPageSize() {
        return pageSize;
    }

    @JsonProperty("continuation_token")
    @Nullable
    public String getContinuationToken() {
        return continuationToken;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof ReadRequest that))
            return false;
        return Objects.equals(tupleKey, that.tupleKey)
                && Objects.equals(authorizationModelId, that.authorizationModelId)
                && Objects.equals(pageSize, that.pageSize) && Objects.equals(continuationToken, that.continuationToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tupleKey, authorizationModelId, pageSize, continuationToken);
    }

    @Override
    public String toString() {
        return "ReadRequest[" +
                "tupleKey=" + tupleKey + ", " +
                "authorizationModelId=" + authorizationModelId + ", " +
                "pageSize=" + pageSize + ", " +
                "continuationToken=" + continuationToken + ']';
    }
}
