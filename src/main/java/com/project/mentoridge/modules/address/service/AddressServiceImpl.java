package com.project.mentoridge.modules.address.service;

import com.project.mentoridge.modules.address.controller.response.SiGunGuResponse;
import com.project.mentoridge.modules.address.mapstruct.AddressMapstruct;
import com.project.mentoridge.modules.address.repository.AddressRepository;
import com.project.mentoridge.modules.address.vo.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AddressServiceImpl implements AddressService {

    private final String LABEL = "label";
    private final String VALUE = "value";

    private final AddressRepository addressRepository;
    private final AddressMapstruct addressMapstruct;

    @Override
    public List<String> getStates() {
        return addressRepository.findStates();
    }

    @Override
    public List<Map<String, String>> getStatesMap() {

        return getStates().stream().map(state -> {
            Map<String, String> map = new HashMap<>();
            map.put(LABEL, state);
            map.put(VALUE, state);
            return map;
        }).collect(Collectors.toList());
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
    public List<Map<String, String>> getSigunGusMap(String state) {

        return getSiGunGuResponses(state).stream().map(siGunGu -> {
            Map<String, String> map = new HashMap<>();
            if (siGunGu.getGu().length() > 0 && siGunGu.getSiGun().length() > 0) {
                map.put(LABEL, siGunGu.getSiGun() + " " + siGunGu.getGu());
                map.put(VALUE, siGunGu.getSiGun() + " " + siGunGu.getGu());
            } else if (siGunGu.getGu().length() == 0) {
                map.put(LABEL, siGunGu.getSiGun());
                map.put(VALUE, siGunGu.getSiGun());
            } else {
                map.put(LABEL, siGunGu.getGu());
                map.put(VALUE, siGunGu.getGu());
            }
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public List<String> getDongs(String state, String SiGunGu) {
        return addressRepository.findDongByStateAndSiGunGu(state, SiGunGu);
    }

    @Override
    public List<Map<String, String>> getDongsMap(String state, String siGunGu) {

        return getDongs(state, siGunGu).stream().map(dong -> {
            Map<String, String> map = new HashMap<>();
            map.put(LABEL, dong);
            map.put(VALUE, dong);
            return map;
        }).collect(Collectors.toList());
    }

}
