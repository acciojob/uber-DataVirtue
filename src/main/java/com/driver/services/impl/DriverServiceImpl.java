package com.driver.services.impl;

import com.driver.model.Cab;
import com.driver.model.TripBooking;
import com.driver.repository.CabRepository;
import com.driver.repository.TripBookingRepository;
import com.driver.services.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.model.Driver;
import com.driver.repository.DriverRepository;

import java.util.List;
import java.util.Optional;

@Service
public class DriverServiceImpl implements DriverService {

	@Autowired
	DriverRepository driverRepository3;

	@Autowired
	TripBookingRepository tripBookingRepository;

	@Autowired
	CabRepository cabRepository3;

	@Override
	public void register(String mobile, String password){
		//Save a driver in the database having given details and a cab with ratePerKm as 10 and availability as True by default.
		Driver driver = new Driver();
		driver.setMobile(mobile);
		driver.setPassword(password);

		Cab cab = new Cab();
		cab.setDriver(driver);
		cab.setPerKmRate(10);
		cab.setAvailable(true);

		Cab savedCab = cabRepository3.save(cab);
		driver.setCab(savedCab);

		driverRepository3.save(driver);

	}

	@Override
	public void removeDriver(int driverId){
		// Delete driver without using deleteById function
		Optional<Driver> optionalDriver = driverRepository3.findById(driverId);

		if(optionalDriver.get()==null)
			return;
		Driver driver = optionalDriver.get();

		List<TripBooking> tripBookingList = driver.getTripBookingList();

		for(TripBooking tripBooking: driver.getTripBookingList()){
			tripBooking.setDriver(null);
			tripBookingRepository.save(tripBooking);
		}
		cabRepository3.delete(driver.getCab());
		driverRepository3.delete(driver);

	}

	@Override
	public void updateStatus(int driverId){
		//Set the status of respective car to unavailable
		Optional<Driver> optionalDriver = driverRepository3.findById(driverId);

		if(optionalDriver.get()==null)
			return;
		Driver driver = optionalDriver.get();
		driver.getCab().setAvailable(false);
		cabRepository3.save(driver.getCab());
		driverRepository3.save(driver);

	}
}
