package persistence.base;

import persistence.base.models.Model;
import persistence.base.relations.RelatedField;
import persistence.base.serialization.Mappable;

public interface MappableModel<T> extends Model<T>, Mappable<T> {
    default RelatedField[] getRelatedFields() {
        return new RelatedField[]{};
    }

    default void loadRelationField(String field, Object object) {
    }
}
