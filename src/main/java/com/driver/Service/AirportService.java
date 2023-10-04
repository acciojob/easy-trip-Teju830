package com.driver.Service;

import com.driver.Repository.AirportRepository;
import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class AirportService {
    private AirportRepository airportRepository = new AirportRepository();
    public void addAirport(Airport airport) {
        airportRepository.addAirport(airport);
    }

    public String getLargestAirportName() {
        List<Airport> list = airportRepository.getAllAirports();
        list.sort((a, b) -> {
            if (a.getNoOfTerminals() == b.getNoOfTerminals())
                return a.getAirportName().compareTo(b.getAirportName());

            return b.getNoOfTerminals() - a.getNoOfTerminals();
        });

        if (list.isEmpty())
            return null;

        return list.get(0).getAirportName();
    }

    public double getShortestDurationOfPossibleBetweenTwoCities(City fromCity, City toCity) {
        List<Flight> list = airportRepository.getAllFlights();

        double shortestDuration = Double.MAX_VALUE;

        for (Flight flight : list)
            if (flight.getFromCity() == fromCity && flight.getToCity() == toCity)
                if (flight.getDuration() < shortestDuration)
                    shortestDuration = flight.getDuration();

        if (shortestDuration == Double.MAX_VALUE) return -1;

        return shortestDuration;
    }

    public int getNumberOfPeopleOn(Date date, String airportName) {
        int count = 0;
        Airport airport = airportRepository.getAirportByName(airportName);
        if (airport == null)
            return count;

        List<Flight> list = airportRepository.getAllFlights();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

        for (Flight flight : list)
            if (fmt.format(flight.getFlightDate()).equals(fmt.format(date)) && (flight.getFromCity() == airport.getCity() || flight.getToCity() == airport.getCity()))
                count += airportRepository.getPassengersByFlight(flight.getFlightId()).size();

        return count;
    }

    public int calculateFlightFare(Integer flightId) {
        return 3000 + 50 * airportRepository.getPassengersByFlight(flightId).size();
    }

    public String bookATicket(Integer flightId, Integer passengerId) {
        Flight flight = airportRepository.getFlightById(flightId);
        Passenger passenger = airportRepository.getPassengerById(passengerId);

        if (flight == null || passenger == null )
            return "FAILURE";

        List<Integer> passengerList = airportRepository.getPassengersByFlight(flightId);
        if (passengerList.contains(passengerId) || passengerList.size() == flight.getMaxCapacity())
            return "FAILURE";

        airportRepository.bookTicket(flightId, passengerId);

        return "SUCCESS";
    }

    public String cancelATicket(Integer flightId, Integer passengerId) {
        Flight flight = airportRepository.getFlightById(flightId);
        Passenger passenger = airportRepository.getPassengerById(passengerId);

        if (flight == null || passenger == null)
            return "FAILURE";

        List<Integer> passengerList = airportRepository.getPassengersByFlight(flightId);
        if (!passengerList.contains(passengerId))
            return "FAILURE";

        airportRepository.cancelTicket(flightId, passengerId);

        return "SUCCESS";
    }

    public int countOfBookingsDoneByPassengerAllCombined(Integer passengerId) {
        int count = 0;
        List<Flight> flights = airportRepository.getAllFlights();

        for (Flight flight : flights)
            if (airportRepository.getPassengersByFlight(flight.getFlightId()).contains(passengerId))
                ++count;

        return count;
    }

    public String addFlight(Flight flight) {
        airportRepository.addFlight(flight);
        return "SUCCESS";
    }

    public String getAirportNameFromFlightId(Integer flightId) {
        Flight flight = airportRepository.getFlightById(flightId);
        if (flight == null)
            return null;

        List<Airport> airportList = airportRepository.getAllAirports();
        for (Airport airport : airportList)
            if (airport.getCity() == flight.getFromCity())
                return airport.getAirportName();

        return null;
    }

    public int calculateRevenueOfAFlight(Integer flightId) {
        int size =  airportRepository.getPassengersByFlight(flightId).size();

        return 3000 * size + 50 * size * (size - 1) / 2;
    }

    public String addPassenger(Passenger passenger) {
        airportRepository.addPassenger(passenger.getPassengerId(), passenger);
        return "SUCCESS";
    }
}
