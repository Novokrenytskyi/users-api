package com.clearsolutions.usersapi.mapper;

import java.util.List;

public interface Mapper <E, D> {
     D toDto(E entity);
     List<D> toDto(List<E> entityList);
     E toEntity(D dto);
}
