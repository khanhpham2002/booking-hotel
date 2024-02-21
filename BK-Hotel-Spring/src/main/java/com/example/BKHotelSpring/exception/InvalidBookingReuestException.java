package com.example.BKHotelSpring.exception;

public class InvalidBookingReuestException extends RuntimeException{
    public InvalidBookingReuestException(String message){
        super(message);
    }

}
