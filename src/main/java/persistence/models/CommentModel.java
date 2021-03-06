package persistence.models;

/*
  This file was generated by generate_model.py
  Any changes you will make to it will be lost if you run the
  script again.
 
  DO NOT MODIFY!
 */

import domain.Comment;
import domain.Review;
import persistence.models.ReviewModel;
import domain.User;
import persistence.models.UserModel;


import lombok.Data;
import lombok.NonNull;
import persistence.base.MappableModel;
import persistence.base.relations.RelatedField;
import persistence.base.relations.RelationType;
import persistence.base.serialization.Field;
import persistence.base.serialization.FieldType;
import persistence.base.serialization.FieldsMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

@Data
public class CommentModel implements MappableModel<Comment> {
    private final String name = "Comment";

    private Integer id;
    @NonNull
    private Comment data;

    public CommentModel(Comment data) {
        this.data = data;
    }

    public void setData(@NonNull Comment data) {
        this.data = data;
    }

    @Override
    public FieldsMap map() {
        var map = new HashMap<String, Field>();
        if (this.id != null) {
            map.put("id", new Field("id", FieldType.Integer, this.id.toString()));
        }
        map.put("text", new Field("text", FieldType.String, this.data.getText().toString()));
        map.put("reviewId", new Field("reviewId", FieldType.Integer, this.data.getReviewId().toString()));
        map.put("commenterName", new Field("commenterName", FieldType.String, this.data.getCommenterName().toString()));

        return new FieldsMap(map, "Comment");
    }

    @Override
    public Comment unmap(FieldsMap map) {
        assert map.getName().equals("Comment");
        var text = map.getMap().get("text").getValue();
        var reviewId = Integer.valueOf(map.getMap().get("reviewId").getValue());
        var commenterName = map.getMap().get("commenterName").getValue();
        try {
            this.data = new Comment(text, reviewId, commenterName);
        } catch (Exception ignored) {
            this.data = null;
        }

        var id = map.getMap().get("id");
        if (id != null) {
            this.id = Integer.valueOf(id.getValue());
        }
        return this.data;
    }

    @Override
    public Comment unmapIfSet(Comment exitingData, FieldsMap map) {
        assert map.getName().equals("Comment");
        if (!map.getMap().containsKey("text")) { map.getMap().put("text", new Field("text", FieldType.String, exitingData.getText().toString())); }
        if (!map.getMap().containsKey("reviewId")) { map.getMap().put("reviewId", new Field("reviewId", FieldType.Integer, exitingData.getReviewId().toString())); }
        if (!map.getMap().containsKey("commenterName")) { map.getMap().put("commenterName", new Field("commenterName", FieldType.String, exitingData.getCommenterName().toString())); }

        return this.unmap(map);
    }

    @Override
    public Collection<Field> getFields() {
        var fields = new ArrayList<Field>();
        fields.add(new Field("id", FieldType.Integer));
        fields.add(new Field("text", FieldType.String));
        fields.add(new Field("reviewId", FieldType.Integer));
        fields.add(new Field("review", FieldType.Reference));
        fields.add(new Field("commenterName", FieldType.String));
        fields.add(new Field("commenter", FieldType.Reference));

        return fields;
    }

    @Override
    public void loadRelationField(String field, Object object) {
        if ("review".equals(field)){         this.getData().setReview((Review) object); }
        if ("commenter".equals(field)){         this.getData().setCommenter((User) object); }

    }

    @Override
    public RelatedField[] getRelatedFields() {
        return new RelatedField[]{
            new RelatedField(
                RelationType.ONE_OWNS_MANY,
                "Review",
                "id",
                new Field("comments", FieldType.Reference),
                "Comment",
                "reviewId",
                new Field("review", FieldType.Reference)
            ),
        };
    }
}