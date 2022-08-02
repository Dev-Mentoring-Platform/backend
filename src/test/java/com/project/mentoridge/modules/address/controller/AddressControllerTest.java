package com.project.mentoridge.modules.address.controller;

import com.project.mentoridge.modules.address.service.AddressService;
import com.project.mentoridge.modules.base.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AddressController.class,
        properties = {"spring.config.location=classpath:application-test.yml"})
class AddressControllerTest extends AbstractControllerTest {

    private final static String BASE_URL = "/api/addresses";

    @MockBean
    AddressService addressService;

/*
    @BeforeEach
    @Override
    protected void init() {
        super.init();
    }*/

    @Test
    void get_states() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/states"))
                .andDo(print())
                .andExpect(status().isOk());
        verify(addressService).getStates();
    }

    @Test
    void get_siGunGus() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/siGunGus")
                        .param("state", "서울특별시"))
                .andDo(print())
                .andExpect(status().isOk());
        verify(addressService).getSigunGus(eq("서울특별시"));
    }

    @Test
    void get_siGunGus_with_no_state() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/siGunGus")
                        .param("state", ""))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verifyNoInteractions(addressService);
    }

    @Test
    void get_dongs() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/dongs")
                        .param("state", "서울특별시")
                        .param("siGunGu", "광진구"))
                .andDo(print())
                .andExpect(status().isOk());
        verify(addressService).getDongs(eq("서울특별시"), eq("광진구"));
    }

    @Test
    void get_dongs_with_no_siGunGu() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/dongs")
                        .param("state", "서울특별시")
                        .param("siGunGu", ""))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verifyNoInteractions(addressService);
    }
}