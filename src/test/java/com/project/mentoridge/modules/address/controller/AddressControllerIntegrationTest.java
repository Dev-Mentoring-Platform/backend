package com.project.mentoridge.modules.address.controller;

import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.modules.address.repository.AddressRepository;
import com.project.mentoridge.modules.address.vo.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.ArrayList;
import java.util.List;

import static com.project.mentoridge.config.init.TestDataBuilder.getAddress;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
public class AddressControllerIntegrationTest {

    private final String BASE_URL = "/api/addresses";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private AddressRepository addressRepository;

    @BeforeEach
    void init(WebApplicationContext webAppContext) {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();

        addressRepository.deleteAllInBatch();

        List<Address> addresses = new ArrayList<>();
        addresses.add(getAddress("서울특별시", "", "종로구", "효자동"));
        addresses.add(getAddress("서울특별시", "", "광진구", "능동"));
        addresses.add(getAddress("부산광역시", "기장군", "", "내리"));
        addresses.add(getAddress("부산광역시", "", "금정구", "금사동"));
        addresses.add(getAddress("부산광역시", "", "수영구", "민락동"));
        addresses.add(getAddress("대구광역시", "", "동구", "대림동"));
        addresses.add(getAddress("전라남도", "여수시", "", "종화동"));
        addresses.add(getAddress("전라북도", "남원시", "", "동충동"));
        addresses.add(getAddress("경상북도", "영주시", "", "영주동"));
        addresses.add(getAddress("경상남도", "진주시", "", "망경동"));
        addresses.add(getAddress("충청남도", "공주시", "", "반죽동"));
        addressRepository.saveAll(addresses);
    }

    @Test
    void 시도_by_state() throws Exception {

        // given
        // when
        // then
        String response = mockMvc.perform(get(BASE_URL + "/states"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        System.out.println(response);
    }

    @Test
    void 시군구조회_by_state() throws Exception {

        // given
        // when
        // then
        String response = mockMvc.perform(get(BASE_URL + "/siGunGus")
                        .param("state", "부산광역시"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        System.out.println(response);
    }

    @Test
    void 시군구조회_without_state() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/siGunGus"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 동조회_by_state_siGun_gu() throws Exception {

        // given
        // when
        // then
        String response = mockMvc.perform(get(BASE_URL + "/dongs")
                .param("state", "부산광역시")
                .param("siGun", "")
                .param("gu", "금정구"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        System.out.println(response);
    }

    @Test
    void 동조회_with_invalid_input() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/dongs"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
