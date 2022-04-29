package com.project.mentoridge.modules.address.service;

import com.project.mentoridge.modules.address.controller.response.SiGunGuResponse;
import com.project.mentoridge.modules.address.mapstruct.AddressMapstruct;
import com.project.mentoridge.modules.address.repository.AddressRepository;
import com.project.mentoridge.modules.address.vo.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AddressServiceImpl implements AddressService {

    private final String VALUE = "value";

    private final AddressRepository addressRepository;
    private final AddressMapstruct addressMapstruct;

    @Override
    public List<String> getStates() {
        return addressRepository.findStates();
    }

    @Override
    public List<Address> getSiGunGus(String state) {
        return addressRepository.findSiGunGuByState(state);
    }

    @Override
    public List<SiGunGuResponse> getSiGunGuResponses(String state) {
        List<Address> siGunGus = getSiGunGus(state);
        return addressMapstruct.addressListToSiGunGuResponseList(siGunGus);
    }

    @Override
    public List<String> getSigunGus(String state) {

        return getSiGunGuResponses(state).stream().map(siGunGu -> {
            if ((siGunGu.getGu() != null && siGunGu.getGu().length() > 0) && (siGunGu.getSiGun() != null &&siGunGu.getSiGun().length() > 0)) {
                return siGunGu.getSiGun() + " " + siGunGu.getGu();
            } else if (siGunGu.getGu() != null && siGunGu.getGu().length() == 0) {
                return siGunGu.getSiGun();
            } else {
                return siGunGu.getGu();
            }
        }).collect(Collectors.toList());
    }

    @Override
    public List<String> getDongs(String state, String SiGunGu) {
        return addressRepository.findDongByStateAndSiGunGu(state, SiGunGu);
    }

}
