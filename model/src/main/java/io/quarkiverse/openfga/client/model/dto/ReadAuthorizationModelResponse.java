package io.quarkiverse.openfga.client.model.dto;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkiverse.openfga.client.model.AuthorizationModel;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class ReadAuthorizationModelResponse {

    @JsonProperty("authorization_model")
    private final AuthorizationModel authorizationModel;

    @JsonCreator(mode = PROPERTIES)
    public ReadAuthorizationModelResponse(@JsonProperty("authorization_model") AuthorizationModel authorizationModel) {
        this.authorizationModel = Preconditions.parameterNonNull(authorizationModel, "authorizationModel");
    }

    @JsonProperty("authorization_model")
    public AuthorizationModel getAuthorizationModel() {
        return authorizationModel;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (ReadAuthorizationModelResponse) obj;
        return Objects.equals(this.authorizationModel, that.authorizationModel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorizationModel);
    }

    @Override
    public String toString() {
        return "ReadAuthorizationModelResponse[" +
                "authorizationModel=" + authorizationModel + ']';
    }

}
