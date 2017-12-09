package com.money.manager.util;

import com.money.manager.exception.CustomException;

@FunctionalInterface
public interface QueryFunction<T, R> {
    R apply(T t) throws CustomException;
}

