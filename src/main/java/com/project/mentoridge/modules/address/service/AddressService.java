package com.project.mentoridge.modules.address.service;

import com.project.mentoridge.modules.address.controller.response.SiGunGuResponse;
import com.project.mentoridge.modules.address.vo.Address;

import java.util.List;

public interface AddressService {

    List<String> getStates();

    List<Address> getSiGunGus(String state);
    List<SiGunGuResponse> getSiGunGuResponses(String state);
    List<String> getSigunGus(String state);

    List<String> getDongs(String state, String siGunGu);

}
