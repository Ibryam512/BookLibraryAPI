package com.example.common;

import java.util.List;

public sealed interface Result<T> permits Result.Success, Result.Failure {

    record Success<T>(T value) implements Result<T> {}
    record Failure<T>(List<String> errors, int statusCode) implements Result<T> {}

    static <T> Result<T> ok(T value) { return new Success<>(value); }
    static <T> Result<T> fail(List<String> errors, int statusCode) { return new Failure<>(errors, statusCode); }
}
