package com.example.airportv3.exception;

public class FlightNotFoundException extends Exception{
    public FlightNotFoundException(String message) {
        super(message);
    }
}
