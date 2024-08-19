package io.quarkiverse.openfga.client.model.nodes;

import java.util.Objects;

import javax.annotation.Nullable;

import io.quarkiverse.openfga.client.model.ObjectRelation;
import io.quarkiverse.openfga.client.model.Userset;
import io.quarkiverse.openfga.client.model.utils.Preconditions;

public class V1 {

    public static final class TupleToUserset {

        private final ObjectRelation tupleset;

        private final ObjectRelation computedUserset;

        public TupleToUserset(ObjectRelation tupleset, ObjectRelation computedUserset) {
            this.tupleset = Preconditions.parameterNonNull(tupleset, "tupleset");
            this.computedUserset = Preconditions.parameterNonNull(computedUserset, "computedUserset");
        }

        public ObjectRelation getTupleset() {
            return tupleset;
        }

        public ObjectRelation getComputedUserset() {
            return computedUserset;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj == this)
                return true;
            if (obj == null || obj.getClass() != this.getClass())
                return false;
            var that = (TupleToUserset) obj;
            return Objects.equals(this.tupleset, that.tupleset) &&
                    Objects.equals(this.computedUserset, that.computedUserset);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tupleset, computedUserset);
        }

        @Override
        public String toString() {
            return "TupleToUserset[" +
                    "tupleset=" + tupleset + ", " +
                    "computedUserset=" + computedUserset + ']';
        }

    }

    public static final class Difference {

        private final Userset base;

        private final Userset subtract;

        public Difference(Userset base, Userset subtract) {
            this.base = Preconditions.parameterNonNull(base, "base");
            this.subtract = Preconditions.parameterNonNull(subtract, "subtract");
        }

        public Userset getBase() {
            return base;
        }

        public Userset getSubtract() {
            return subtract;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj == this)
                return true;
            if (obj == null || obj.getClass() != this.getClass())
                return false;
            var that = (Difference) obj;
            return Objects.equals(this.base, that.base) &&
                    Objects.equals(this.subtract, that.subtract);
        }

        @Override
        public int hashCode() {
            return Objects.hash(base, subtract);
        }

        @Override
        public String toString() {
            return "Difference[" +
                    "base=" + base + ", " +
                    "subtract=" + subtract + ']';
        }

    }

}
