package persistence.models;

/*
  This file was generated by generate_model.py
  Any changes you will make to it will be lost if you run the
  script again.
 
  DO NOT MODIFY!
 */

import domain.ReadingTracker;
import persistence.base.MappableModel;
import persistence.base.models.ModelFactory;

public class ReadingTrackerModelFactory implements ModelFactory<ReadingTracker> {

    @Override
    public String getModelName() {
        return "ReadingTracker";
    }

    @Override
    public MappableModel<ReadingTracker> build(ReadingTracker data) {
        return new ReadingTrackerModel(data);
    }
}