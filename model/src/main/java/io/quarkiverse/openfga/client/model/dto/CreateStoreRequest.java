package io.quarkiverse.openfga.client.model.dto;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class CreateStoreRequest {

    public static final class Builder {

        @Nullable
        private String name;

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public CreateStoreRequest build() {
            return new CreateStoreRequest(
                    Preconditions.parameterNonBlank(name, "name"));
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private final String name;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    CreateStoreRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof CreateStoreRequest that))
            return false;
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
