package io.quarkiverse.openfga.client.model;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

/**
 * A partial tuple key that defines only a relationship to an object, without a specific user.
 */
@JsonDeserialize
public final class RelPartialTupleKey implements RelPartialTupleKeyed {

    public static final class Builder {

        @Nullable
        private RelObject object;
        @Nullable
        private String relation;

        private Builder() {
        }

        public Builder object(RelObject object) {
            this.object = object;
            return this;
        }

        public Builder relation(String relation) {
            this.relation = relation;
            return this;
        }

        public RelPartialTupleKey build() {
            return new RelPartialTupleKey(
                    Preconditions.parameterNonNull(object, "object"),
                    Preconditions.parameterNonNull(relation, "relation"));
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private final RelObject object;
    private final String relation;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    RelPartialTupleKey(RelObject object, String relation) {
        this.object = object;
        this.relation = relation;
    }

    @Override
    public RelObject getObject() {
        return object;
    }

    @Override
    public String getRelation() {
        return relation;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (RelPartialTupleKey) obj;
        return Objects.equals(this.relation, that.relation) &&
                Objects.equals(this.object, that.object);
    }

    @Override
    public int hashCode() {
        return Objects.hash(relation, object);
    }

    @Override
    public String toString() {
        return "TupleKey[" +
                "relation=" + relation + ", " +
                "object=" + object + ", " + ']';
    }

}
