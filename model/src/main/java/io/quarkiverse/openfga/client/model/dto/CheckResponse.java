package io.quarkiverse.openfga.client.model.dto;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

public final class CheckResponse {

    private final boolean allowed;

    @Nullable
    private final String resolution;

    @JsonCreator(mode = PROPERTIES)
    public CheckResponse(boolean allowed, @Nullable String resolution) {
        this.allowed = allowed;
        this.resolution = resolution;
    }

    public boolean getAllowed() {
        return allowed;
    }

    @Nullable
    public String getResolution() {
        return resolution;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (CheckResponse) obj;
        return this.allowed == that.allowed &&
                Objects.equals(this.resolution, that.resolution);
    }

    @Override
    public int hashCode() {
        return Objects.hash(allowed, resolution);
    }

    @Override
    public String toString() {
        return "CheckResponse[" +
                "allowed=" + allowed + ", " +
                "resolution=" + resolution + ']';
    }

}
