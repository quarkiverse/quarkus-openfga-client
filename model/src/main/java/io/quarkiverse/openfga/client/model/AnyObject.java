package io.quarkiverse.openfga.client.model;

public final class AnyObject {

    private final String type;

    private final String id;

    AnyObject(String type, String id) {
        this.type = type;
        this.id = id;
    }

    public static AnyObject of(String type, String id) {
        return new AnyObject(type, id);
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (AnyObject) obj;
        return java.util.Objects.equals(this.type, that.type) &&
                java.util.Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(type, id);
    }

    @Override
    public String toString() {
        return "Object[" +
                "type=" + type + ", " +
                "id=" + id + ']';
    }
}
