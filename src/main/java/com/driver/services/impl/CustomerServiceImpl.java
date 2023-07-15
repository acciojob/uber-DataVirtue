package com.driver.services.impl;

import com.driver.model.*;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;

import java.util.*;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function

		Optional<Customer> optionalCustomer = customerRepository2.findById(customerId);
		if(optionalCustomer.get()==null) {
			return;
		}

		customerRepository2.delete(optionalCustomer.get());

	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query
		List<Driver> driverList = driverRepository2.findAll();
		Collections.sort(driverList,(a, b)-> a.getDriverId() - b.getDriverId());
		Driver assignedDriver = null;
		for(Driver driver: driverList){
			if(driver.getCab().getAvailable()) {
				assignedDriver = driver;
				break;
			}
		}
		if(assignedDriver==null)
			throw new Exception("No cab available!");

		TripBooking tripBooking = new TripBooking();
		tripBooking.setFromLocation(fromLocation);
		tripBooking.setToLocation(toLocation);
		tripBooking.setDistanceInKm(distanceInKm);

		Optional<Customer> optionalCustomer = customerRepository2.findById(customerId);
		if(optionalCustomer.get()==null){
			return null;
		}
		Customer customer = optionalCustomer.get();

		tripBooking.setCustomer(customer);
		tripBooking.setDriver(assignedDriver);
		assignedDriver.getCab().setAvailable(false);

		TripBooking savedtripBooking = tripBookingRepository2.save(tripBooking);

		customer.getTripBookingList().add(savedtripBooking);
		assignedDriver.getTripBookingList().add(savedtripBooking);

		customerRepository2.save(customer);
		driverRepository2.save(assignedDriver);

		return savedtripBooking;

	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		Optional<TripBooking> optionalTripBooking = tripBookingRepository2.findById(tripId);

//		if(optionalTripBooking.get()==null)
//			return;

		TripBooking tripBooking = optionalTripBooking.get();

		Customer customer = tripBooking.getCustomer();
		Driver driver = tripBooking.getDriver();

		tripBooking.setStatus(TripStatus.CANCELED);

//		customer.getTripBookingList().remove(tripBooking.getTripBookingId());
//		driver.getTripBookingList().remove(tripBooking.getTripBookingId());

//		driver.getCab().setAvailable(true);

//		tripBookingRepository2.deleteById(tripId);

//		customerRepository2.save(customer);
//		driverRepository2.save(driver);
		tripBookingRepository2.save(tripBooking);


	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		Optional<TripBooking> optionalTripBooking = tripBookingRepository2.findById(tripId);

//		if(optionalTripBooking.get()==null)
//			return;

		TripBooking tripBooking = optionalTripBooking.get();

		tripBooking.setStatus(TripStatus.COMPLETED);
		Driver driver = tripBooking.getDriver();
		Customer customer = tripBooking.getCustomer();

		driver.getCab().setAvailable(true);

//		tripBookingRepository2.save(tripBooking);
		driverRepository2.save(driver);
		tripBookingRepository2.save(tripBooking);
		customerRepository2.save(customer);

	}
}
