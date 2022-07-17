package com.project.mentoridge.modules.address.service;

import com.project.mentoridge.modules.address.mapstruct.AddressMapstruct;
import com.project.mentoridge.modules.address.repository.AddressRepository;
import com.project.mentoridge.modules.address.vo.Address;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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
        // when
        addressService.getStates();
        // then
        verify(addressRepository).findStates();
    }

    @Test
    void getSiGunGus() {

        // given
        // when
        addressService.getSiGunGus("서울특별시");
        // then
        verify(addressRepository).findSiGunGuByState(eq("서울특별시"));
    }

    @Test
    void getSiGunGuResponses() {

        // given
        List<Address> siGunGus = new ArrayList<>();
        Address address1 = mock(Address.class);
        Address address2 = mock(Address.class);
        siGunGus.add(address1);
        siGunGus.add(address2);
        when(addressRepository.findSiGunGuByState("서울특별시")).thenReturn(siGunGus);
        // when
        addressService.getSiGunGuResponses("서울특별시");
        // then
        verify(addressMapstruct).addressListToSiGunGuResponseList(eq(siGunGus));
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
        // when
        List<String> result = addressService.getSigunGus("서울특별시");
        // then
        verify(addressMapstruct).addressListToSiGunGuResponseList(eq(siGunGus));
        assertThat(result).containsExactly("강남구", "광진구");
    }

    @Test
    void getDongs() {

        // given
        // when
        addressService.getDongs("서울특별시", "강남구");
        // then
        verify(addressRepository).findDongByStateAndSiGunGu(eq("서울특별시"), eq("강남구"));
    }
}