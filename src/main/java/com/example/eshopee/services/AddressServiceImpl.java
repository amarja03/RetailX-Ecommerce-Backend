package com.example.eshopee.services;

import java.util.List;
import java.util.stream.Collectors;

import com.example.eshopee.repositories.AddressRepo;
import com.example.eshopee.repositories.UserRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.eshopee.entites.Address;
import com.example.eshopee.entites.User;
import com.example.eshopee.exceptions.APIException;
import com.example.eshopee.exceptions.ResourceNotFoundException;
import com.example.eshopee.payloads.AddressDTO;

import jakarta.transaction.Transactional;

@Transactional
@Service
public class AddressServiceImpl implements AddressService {

	@Autowired
	private AddressRepo addressRepo;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public AddressDTO createAddress(AddressDTO addressDTO) {

		String country = addressDTO.getCountry();
		String state = addressDTO.getState();
		String city = addressDTO.getCity();
		String pincode = addressDTO.getPincode();
		String street = addressDTO.getStreet();
		String buildingName = addressDTO.getBuildingName();

		Address addressFromDB = addressRepo.findByCountryAndStateAndCityAndPincodeAndStreetAndBuildingName(country,
				state, city, pincode, street, buildingName);

		if (addressFromDB != null) {
			throw new APIException("Address already exists with addressId: " + addressFromDB.getAddressId());
		}

		Address address = modelMapper.map(addressDTO, Address.class);

		Address savedAddress = addressRepo.save(address);

		return modelMapper.map(savedAddress, AddressDTO.class);
	}

	@Override
	public List<AddressDTO> getAddresses() {
		System.out.println("In address service");
		List<Address> addresses = addressRepo.findAll();
		System.out.println(addresses);
		List<AddressDTO> addressDTOs = addresses.stream().map(address -> modelMapper.map(address, AddressDTO.class))
				.collect(Collectors.toList());

		return addressDTOs;
	}

	@Override
	public AddressDTO getAddress(Long addressId) {
		Address address = addressRepo.findById(addressId)
				.orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

		return modelMapper.map(address, AddressDTO.class);
	}

	@Override
	public AddressDTO updateAddress(Long addressId, Address address) {
		Address addressFromDB = addressRepo.findByCountryAndStateAndCityAndPincodeAndStreetAndBuildingName(
				address.getCountry(), address.getState(), address.getCity(), address.getPincode(), address.getStreet(),
				address.getBuildingName());

		if (addressFromDB == null) {
			addressFromDB = addressRepo.findById(addressId)
					.orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

			addressFromDB.setCountry(address.getCountry());
			addressFromDB.setState(address.getState());
			addressFromDB.setCity(address.getCity());
			addressFromDB.setPincode(address.getPincode());
			addressFromDB.setStreet(address.getStreet());
			addressFromDB.setBuildingName(address.getBuildingName());

			Address updatedAddress = addressRepo.save(addressFromDB);

			return modelMapper.map(updatedAddress, AddressDTO.class);
		} else {
			List<User> users = userRepo.findByAddress(addressId);
			final Address a = addressFromDB;

			users.forEach(user -> user.getAddresses().add(a));

			deleteAddress(addressId);

			return modelMapper.map(addressFromDB, AddressDTO.class);
		}
	}

	@Override
	public String deleteAddress(Long addressId) {
		Address addressFromDB = addressRepo.findById(addressId)
				.orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

		List<User> users = userRepo.findByAddress(addressId);

		users.forEach(user -> {
			user.getAddresses().remove(addressFromDB);

			userRepo.save(user);
		});

		addressRepo.deleteById(addressId);

		return "Address deleted successfully with addressId: " + addressId;
	}
	/*
	1. deleteAddress(Long addressId) method is implemented which deletes the address from the database.
	2. updateAddress(Long addressId, Address address) method is implemented which updates the address in the database.
	3. getAddress(Long addressId) method is implemented which returns the address from the database.
	4. getAddresses() method is implemented which returns all the addresses from the database.
	5. createAddress(AddressDTO addressDTO) method is implemented which creates the address in the database.

	 */

}


