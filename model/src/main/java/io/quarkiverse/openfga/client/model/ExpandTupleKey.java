package io.quarkiverse.openfga.client.model;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class ExpandTupleKey {

    private final String relation;

    private final String object;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    ExpandTupleKey(String relation, String object) {
        this.relation = Preconditions.parameterNonNull(relation, "relation");
        this.object = Preconditions.parameterNonNull(object, "object");
    }

    public static ExpandTupleKey of(String relation, String object) {
        return new ExpandTupleKey(relation, object);
    }

    public static final class Builder {
        private String relation;
        private String object;

        public Builder() {
        }

        public Builder relation(String relation) {
            this.relation = relation;
            return this;
        }

        public Builder object(String object) {
            this.object = object;
            return this;
        }

        public ExpandTupleKey build() {
            return new ExpandTupleKey(relation, object);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getRelation() {
        return relation;
    }

    public String getObject() {
        return object;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (ExpandTupleKey) obj;
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
