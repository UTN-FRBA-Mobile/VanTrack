package utn.proy2k18.vantrack.reservations;

import org.joda.time.DateTime;

import utn.proy2k18.vantrack.mainFunctionality.search.Trip;


public class Reservation {

    private DateTime reservationDate;
    private Trip bookedTrip;

    public Reservation(DateTime date, Trip trip) {
        this.reservationDate = date;
        this.bookedTrip = trip;
    }

    public Trip getBookedTrip() { return bookedTrip; }

    public String getTripCompanyName() {
        return bookedTrip.getCompanyName();
    }

    public DateTime getReservationDate() {
        return reservationDate;
    }

    public String getTripOrigin() {
        return bookedTrip.getOrigin();
    }

    public String getTripDestination() {
        return bookedTrip.getDestination();
    }

    public String getTripFormattedDate() { return bookedTrip.getCalendarDate(); }

    public String getTripStrTime() { return bookedTrip.getStrTime(); }

    public String getReservationFormattedDate(){
        return this.reservationDate.toLocalDate().toString();
    }


}