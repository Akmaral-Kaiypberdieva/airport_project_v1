package com.example.airportv3.dto;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public class ResponseEntt<T> {
    private HttpStatus status;
    private HttpHeaders headers;
    private T body;

    public ResponseEntt() {
    }

    public ResponseEntt(HttpStatus status) {
        this.status = status;
    }

    public ResponseEntt(T body, HttpStatus status) {
        this.body = body;
        this.status = status;
    }

    public ResponseEntt(T body, HttpHeaders headers, HttpStatus status) {
        this.body = body;
        this.headers = headers;
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public ResponseEntt<T> setStatus(HttpStatus status) {
        this.status = status;
        return this;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public ResponseEntt<T> setHeaders(HttpHeaders headers) {
        this.headers = headers;
        return this;
    }

    public T getBody() {
        return body;
    }

    public ResponseEntt<T> setBody(T body) {
        this.body = body;
        return this;
    }

    public static <T> ResponseEntt<T> ok(T body) {
        return new ResponseEntt<>(body, HttpStatus.OK);
    }
}
