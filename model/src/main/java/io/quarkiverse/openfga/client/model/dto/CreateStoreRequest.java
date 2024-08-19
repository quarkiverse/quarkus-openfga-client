package io.quarkiverse.openfga.client.model.dto;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class CreateStoreRequest {

    private final String name;

    @JsonCreator(mode = PROPERTIES)
    CreateStoreRequest(String name) {
        this.name = Preconditions.parameterNonNull(name, "name");
    }

    public static CreateStoreRequest of(String name) {
        return new CreateStoreRequest(name);
    }

    public static final class Builder {
        private String name;

        Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public CreateStoreRequest build() {
            return new CreateStoreRequest(name);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (CreateStoreRequest) obj;
        return Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "CreateStoreRequest[" +
                "name=" + name + ']';
    }

}
