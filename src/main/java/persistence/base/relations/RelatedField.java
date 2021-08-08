package persistence.base.relations;

import lombok.Data;
import lombok.NonNull;
import persistence.base.serialization.Field;

@Data
public class RelatedField {
  @NonNull
  private RelationType relationType;
  @NonNull
  private String sourceModel;
  @NonNull
  private String foreignKeyFieldOnSource;
  @NonNull
  private Field sourceModelRefField;
  @NonNull
  private String targetModel;
  @NonNull
  private String foreignKeyFieldOnTarget;
  @NonNull
  private Field targetModelRefField;
}