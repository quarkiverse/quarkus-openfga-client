package io.quarkiverse.openfga.client.model.schema;

import io.quarkiverse.openfga.client.model.utils.Preconditions;

public class V1 {

    public record TupleToUserset(ObjectRelation tupleset, ObjectRelation computedUserset) {

        public TupleToUserset {
            Preconditions.parameterNonNull(tupleset, "tupleset");
            Preconditions.parameterNonNull(computedUserset, "computedUserset");
        }

    }

    public record Difference(Userset base, Userset subtract) {

        public Difference {
            Preconditions.parameterNonNull(base, "base");
            Preconditions.parameterNonNull(subtract, "subtract");
        }

    }

}
