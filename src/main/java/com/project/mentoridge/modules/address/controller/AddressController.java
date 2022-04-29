package com.project.mentoridge.modules.address.controller;

import com.project.mentoridge.modules.address.controller.request.DongRequest;
import com.project.mentoridge.modules.address.controller.request.SiGunGuRequest;
import com.project.mentoridge.modules.address.service.AddressService;
import com.project.mentoridge.modules.address.util.AddressUtils;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Api(tags = {"AddressController"})
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;

    @GetMapping(value = "/states")
    public ResponseEntity<?> getStates() {
        List<String> states = addressService.getStates();
        return ResponseEntity.ok(states);
    }

    // TODO - CHECK : @ModelAttribute
    @GetMapping(value = "/siGunGus")
    public ResponseEntity<?> getSiGunGus(@Valid SiGunGuRequest addressRequest) {
        List<String> siGunGus = addressService.getSigunGus(addressRequest.getState());
        return ResponseEntity.ok(siGunGus);
    }

    // TODO - CHECK : @ModelAttribute
    @GetMapping(value = "/dongs")
    public ResponseEntity<?> getDongs(@Valid DongRequest dongRequest) {
        List<String> dongs = addressService.getDongs(dongRequest.getState(), AddressUtils.convertAddress(dongRequest.getSiGunGu()));
        return ResponseEntity.ok(dongs);
    }

}
