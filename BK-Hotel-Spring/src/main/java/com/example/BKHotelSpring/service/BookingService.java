package com.example.BKHotelSpring.service;

import com.example.BKHotelSpring.exception.InvalidBookingReuestException;
import com.example.BKHotelSpring.model.BookedRoom;
import com.example.BKHotelSpring.model.Room;
import com.example.BKHotelSpring.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService implements IBookingService {

    private final BookingRepository bookingRepository;
    private final IRoomService roomService;
    @Override
    public List<BookedRoom> getAllBookingsByRoomId(Long roomId) {

        return bookingRepository.findByRoomId(roomId);
    }

    @Override
    public void cancelBooking(Long bookingId) {
        bookingRepository.deleteById(bookingId);
    }

    @Override
    public String saveBooking(Long roomId, BookedRoom bookingRequest) {
        if(bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())){
            throw new InvalidBookingReuestException("Check-in date must come before check-out date");
        }
        Room room = roomService.getRoomById(roomId).get();
        List<BookedRoom> existingBookings = room.getBookings();
        boolean roomIsAvailable = roomIsAvailable(bookingRequest, existingBookings);
        if(roomIsAvailable){
            room.addBooking(bookingRequest);
            bookingRepository.save(bookingRequest);
        }else{
            throw new InvalidBookingReuestException("Sorry, this room is not available for the selected dates;");
        }

        return bookingRequest.getBookingConfirmationCode();
    }



    @Override
    public BookedRoom findByBookingConfirmationCode(String confirmationCode) {
        return bookingRepository.findByBookingConfirmationCode(confirmationCode);
    }

    @Override
    public List<BookedRoom> getAllBooling() {
        return bookingRepository.findAll();
    }

    private boolean roomIsAvailable(BookedRoom bookingRequest, List<BookedRoom> existingBookings) {
        for (BookedRoom existingBooking : existingBookings) {
            if (bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckInDate()) ||
                    bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckOutDate())) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }
}
