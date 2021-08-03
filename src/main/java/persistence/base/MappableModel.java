package persistence.base;

import persistence.base.exceptions.InvalidStorageReferenceException;
import persistence.base.exceptions.NullStorageReferenceException;
import persistence.base.exceptions.UnknownModelException;

public interface MappableModel<T> extends Model<T>, Mappable<T> {
    default void save() throws UnknownModelException, InvalidStorageReferenceException {
        if (this.getId() == null) {
            var newModel = this.getAssociatedRepository().create(this.getData());
            this.setId(newModel.getId());
        } else {
            this.getAssociatedRepository().update(this, this.map());
        }
    }

    default void delete() throws NullStorageReferenceException, InvalidStorageReferenceException {
        if (this.getId() == null) {
            throw new NullStorageReferenceException(this.getName());
        }

        this.getAssociatedRepository().delete(this);
    }

    default void reload() throws NullStorageReferenceException, InvalidStorageReferenceException, UnknownModelException {
        if (this.getId() == null) {
            throw new NullStorageReferenceException(this.getName());
        }

        var newModel = this.getAssociatedRepository().findById(this.getId());
        if (newModel.isEmpty()) {
            throw new InvalidStorageReferenceException(this.getName(), this.getId());
        }

        this.setData(newModel.get().getData());
    }
}
