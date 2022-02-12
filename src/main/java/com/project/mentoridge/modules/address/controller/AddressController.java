package com.project.mentoridge.modules.address.controller;

import com.project.mentoridge.modules.address.controller.request.DongRequest;
import com.project.mentoridge.modules.address.controller.request.SiGunGuRequest;
import com.project.mentoridge.modules.address.service.AddressService;
import com.project.mentoridge.modules.address.util.AddressUtils;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Api(tags = {"AddressController"})
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;

    @GetMapping(value = "/states", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getStates() {
        List<Map<String, String>> states = addressService.getStatesMap();
        return ResponseEntity.ok(states);
    }

    // TODO - CHECK : @ModelAttribute
    @GetMapping(value = "/siGunGus", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSiGunGus(@Valid SiGunGuRequest addressRequest) {
        List<Map<String, String>> siGunGus = addressService.getSigunGusMap(addressRequest.getState());
        return ResponseEntity.ok(siGunGus);
    }

    // TODO - CHECK : @ModelAttribute
    @GetMapping(value = "/dongs", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDongs(@Valid DongRequest dongRequest) {

        List<Map<String, String>> dongs = addressService.getDongsMap(dongRequest.getState(), AddressUtils.convertAddress(dongRequest.getSiGunGu()));
        return ResponseEntity.ok(dongs);
    }

}
