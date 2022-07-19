package com.project.mentoridge.modules.address.service;

import com.project.mentoridge.configuration.annotation.ServiceTest;
import com.project.mentoridge.modules.address.controller.response.SiGunGuResponse;
import com.project.mentoridge.modules.address.repository.AddressRepository;
import com.project.mentoridge.modules.address.vo.Address;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ServiceTest
class AddressServiceIntegrationTest {

    @Autowired
    AddressService addressService;
    @Autowired
    AddressRepository addressRepository;

    private Address address1;
    private Address address2;
    private Address address3;

    @BeforeAll
    void init() {

        address1 = addressRepository.save(Address.builder()
                .state("서울특별시")
                .gu("종로구")
                .dongMyunLi("청운동")
                .build());
        address2 = addressRepository.save(Address.builder()
                .state("서울특별시")
                .gu("광진구")
                .dongMyunLi("능동")
                .build());
        address3 = addressRepository.save(Address.builder()
                .state("부산광역시")
                .gu("영도구")
                .dongMyunLi("봉래동")
                .build());
    }

    @Test
    void get_states() {

        // given
        // when
        List<String> states = addressService.getStates();
        // then
        assertThat(states).hasSize(2);
        assertThat(states).contains("서울특별시", "부산광역시");
    }

    @Test
    void get_siGunGus() {

        // given
        // when
        List<Address> addresses = addressService.getSiGunGus("서울특별시");
        // then
        assertThat(addresses).hasSize(2);
        assertThat(addresses.stream().filter(address -> address.getState().equals("서울특별시")).count()).isEqualTo(2);
        assertThat(addresses.stream().filter(address -> address.getGu().equals("종로구")).count()).isEqualTo(1);
        assertThat(addresses.stream().filter(address -> address.getGu().equals("광진구")).count()).isEqualTo(1);
    }

    @Test
    void get_siGunGuResponses() {

        // given
        // when
        List<SiGunGuResponse> responses = addressService.getSiGunGuResponses("서울특별시");

        // then
        assertThat(responses).hasSize(2);

        SiGunGuResponse siGunGuResponse1 = new SiGunGuResponse(address1.getState(), address1.getGu(), address1.getDongMyunLi());
        SiGunGuResponse siGunGuResponse2 = new SiGunGuResponse(address2.getState(), address2.getGu(), address2.getDongMyunLi());
        assertThat(responses).contains(siGunGuResponse1, siGunGuResponse2);
    }

    @Test
    void get_siGunGus_in_string() {

        // given
        // when
        List<String> responses = addressService.getSigunGus("서울특별시");

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses).contains("서울특별시 광진구 능동", "서울특별시 종로구 청운동");
    }

    @Test
    void get_dongs() {

        // given
        // when
        List<String> responses = addressService.getDongs("부산광역시", "영도구");
        // then
        assertThat(responses).hasSize(1);
        assertThat(responses).containsExactly("봉래동");
    }

}