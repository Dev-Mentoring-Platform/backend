package com.project.mentoridge.modules.address.service;

import com.project.mentoridge.modules.address.controller.response.SiGunGuResponse;
import com.project.mentoridge.modules.address.mapstruct.AddressMapstruct;
import com.project.mentoridge.modules.address.repository.AddressRepository;
import com.project.mentoridge.modules.address.vo.Address;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @InjectMocks
    AddressServiceImpl addressService;
    @Mock
    AddressRepository addressRepository;
    @Mock
    AddressMapstruct addressMapstruct;

    @Test
    void getStates() {

        // given
        when(addressRepository.findStates()).thenReturn(Arrays.asList("서울특별시", "경기도"));
        // when
        List<String> result = addressService.getStates();
        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly("서울특별시", "경기도");
    }

    @Test
    void getSiGunGus() {

        // given
        Address address1 = Address.builder()
                .state("서울특별시")
                .siGun(null)
                .gu("강남구")
                .dongMyunLi("청담동")
                .build();
        Address address2 = Address.builder()
                .state("서울특별시")
                .siGun(null)
                .gu("광진구")
                .dongMyunLi("중곡동")
                .build();
        when(addressRepository.findSiGunGuByState("서울특별시")).thenReturn(Arrays.asList(address1, address2));
        // when
        List<Address> result = addressService.getSiGunGus("서울특별시");
        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDongMyunLi()).isEqualTo("청담동");
        assertThat(result.get(1).getDongMyunLi()).isEqualTo("중곡동");
    }

    @Test
    void getSiGunGuResponses() {

        // given
        List<Address> siGunGus = new ArrayList<>();
        Address address1 = Address.builder()
                .state("서울특별시")
                .siGun(null)
                .gu("강남구")
                .dongMyunLi("청담동")
                .build();
        Address address2 = Address.builder()
                .state("서울특별시")
                .siGun(null)
                .gu("광진구")
                .dongMyunLi("중곡동")
                .build();
        siGunGus.add(address1);
        siGunGus.add(address2);
        when(addressRepository.findSiGunGuByState("서울특별시")).thenReturn(siGunGus);

        List<SiGunGuResponse> mappedList = new ArrayList<>();
        SiGunGuResponse mapped1 = new SiGunGuResponse(address1.getState(), address1.getSiGun(), address1.getGu());
        SiGunGuResponse mapped2 = new SiGunGuResponse(address2.getState(), address2.getSiGun(), address2.getGu());
        mappedList.add(mapped1);
        mappedList.add(mapped2);
        when(addressMapstruct.addressListToSiGunGuResponseList(siGunGus)).thenReturn(mappedList);

        // when
        List<SiGunGuResponse> result = addressService.getSiGunGuResponses("서울특별시");
        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getState()).isEqualTo("서울특별시");
        assertThat(result.get(0).getSiGun()).isEqualTo(null);
        assertThat(result.get(0).getGu()).isEqualTo("강남구");
        assertThat(result.get(1).getState()).isEqualTo("서울특별시");
        assertThat(result.get(1).getSiGun()).isEqualTo(null);
        assertThat(result.get(1).getGu()).isEqualTo("광진구");
    }

    @Test
    void getSigunGus() {

        // given
        List<Address> siGunGus = new ArrayList<>();
        Address address1 = Address.builder()
                .state("서울특별시")
                .siGun(null)
                .gu("강남구")
                .dongMyunLi("청담동")
                .build();
        Address address2 = Address.builder()
                .state("서울특별시")
                .siGun(null)
                .gu("광진구")
                .dongMyunLi("중곡동")
                .build();
        siGunGus.add(address1);
        siGunGus.add(address2);
        when(addressRepository.findSiGunGuByState("서울특별시")).thenReturn(siGunGus);

        List<SiGunGuResponse> siGunGuResponses = new ArrayList<>();
        siGunGuResponses.add(new SiGunGuResponse("서울특별시", null, "강남구"));
        siGunGuResponses.add(new SiGunGuResponse("서울특별시", null, "광진구"));
        when(addressMapstruct.addressListToSiGunGuResponseList(siGunGus)).thenReturn(siGunGuResponses);

        // when
        List<String> result = addressService.getSigunGus("서울특별시");
        // then
        // verify(addressMapstruct).addressListToSiGunGuResponseList(eq(siGunGus));
        assertThat(result).containsExactly("강남구", "광진구");
    }

    @Test
    void getDongs() {

        // given
        when(addressRepository.findDongByStateAndSiGunGu("서울특별시", "강남구")).thenReturn(Arrays.asList("청담동", "압구정동"));
        // when
        List<String> result = addressService.getDongs("서울특별시", "강남구");
        // then
        assertThat(result).containsExactly("청담동", "압구정동");
    }
}