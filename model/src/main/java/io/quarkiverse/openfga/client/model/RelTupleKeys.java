package io.quarkiverse.openfga.client.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.quarkiverse.openfga.client.model.utils.ModelMapper;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public final class RelTupleKeys {

    public static RelTupleKeys of(RelTupleKeyed... tupleKeys) {
        return new RelTupleKeys(List.of(tupleKeys));
    }

    public static RelTupleKeys of(@Nullable Collection<? extends RelTupleKeyed> tupleKeys) {
        if (tupleKeys == null) {
            return new RelTupleKeys(List.of());
        }
        return new RelTupleKeys(List.copyOf(tupleKeys));
    }

    public static RelTupleKeys concat(@Nullable RelTupleKeys value, @Nullable RelTupleKeys other) {
        if (value == null) {
            return other;
        }
        if (other == null) {
            return value;
        }
        return concat(value, other.tupleKeys);
    }

    public static RelTupleKeys concat(@Nullable RelTupleKeys value, @Nullable Collection<? extends RelTupleKeyed> other) {
        if (value == null) {
            return RelTupleKeys.of(other);
        }
        if (other == null) {
            return value;
        }
        return RelTupleKeys.of(Stream.of(value.tupleKeys, other).flatMap(Collection::stream).toList());
    }

    public static RelTupleKeys parse(InputStream stream) throws IOException {
        return ModelMapper.mapper.readValue(stream, RelTupleKeys.class);
    }

    public static RelTupleKeys parse(Reader reader) throws IOException {
        return ModelMapper.mapper.readValue(reader, RelTupleKeys.class);
    }

    public static RelTupleKeys parse(String json) throws IOException {
        return ModelMapper.mapper.readValue(json, RelTupleKeys.class);
    }

    public static RelTupleKeys parse(byte[] bytes) throws IOException {
        return ModelMapper.mapper.readValue(bytes, RelTupleKeys.class);
    }

    public static RelTupleKeys parseList(InputStream stream) throws IOException {
        Collection<RelTupleKeyed> tupleKeys = ModelMapper.mapper.readValue(stream, new TypeReference<>() {
        });
        return RelTupleKeys.of(tupleKeys);
    }

    public static RelTupleKeys parseList(Reader reader) throws IOException {
        Collection<RelTupleKeyed> tupleKeys = ModelMapper.mapper.readValue(reader, new TypeReference<>() {
        });
        return RelTupleKeys.of(tupleKeys);
    }

    public static RelTupleKeys parseList(String json) throws IOException {
        Collection<RelTupleKeyed> tupleKeys = ModelMapper.mapper.readValue(json, new TypeReference<>() {
        });
        return RelTupleKeys.of(tupleKeys);
    }

    public static RelTupleKeys parseList(byte[] bytes) throws IOException {
        Collection<RelTupleKeyed> tupleKeys = ModelMapper.mapper.readValue(bytes, new TypeReference<>() {
        });
        return RelTupleKeys.of(tupleKeys);
    }

    private final Collection<RelTupleKeyed> tupleKeys;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    RelTupleKeys(@JsonProperty("tuple_keys") Collection<RelTupleKeyed> tupleKeys) {
        if (tupleKeys.size() > 20) {
            throw new IllegalStateException("tupleKeys must have at most 20 items");
        }
        this.tupleKeys = Preconditions.parameterNonNull(tupleKeys, "tupleKeys");
    }

    @JsonProperty("tuple_keys")
    @JsonSerialize(typing = JsonSerialize.Typing.STATIC)
    public Collection<RelTupleKeyed> getTupleKeys() {
        return tupleKeys;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof RelTupleKeys that))
            return false;
        return Objects.equals(this.tupleKeys, that.tupleKeys);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tupleKeys);
    }

    @Override
    public String toString() {
        return "TupleKeys[" +
                "tupleKeys=" + tupleKeys + ']';
    }

}
