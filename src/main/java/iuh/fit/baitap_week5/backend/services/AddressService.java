package iuh.fit.baitap_week5.backend.services;

import iuh.fit.baitap_week5.backend.models.Address;
import iuh.fit.baitap_week5.backend.reponsitories.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    public Address save(Address address) {
        return addressRepository.save(address);
    }

}
