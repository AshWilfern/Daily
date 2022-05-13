package com.services;

import com.entity.DailyEntity;

import java.util.List;

public interface EntityService<T extends DailyEntity> {

    List<T> getEntities();

    T getEntityById(long id);

    void saveEntity(T entity);

    void deleteEntity(long id);
}
