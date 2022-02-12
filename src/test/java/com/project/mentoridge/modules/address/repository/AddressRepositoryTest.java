package com.project.mentoridge.modules.address.repository;

import com.project.mentoridge.modules.address.vo.Address;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class AddressRepositoryTest {

    @Autowired
    AddressRepository addressRepository;

//    @BeforeEach
//    void init() {
//
//        addressRepository.deleteAllInBatch();
//
//        List<Address> addresses = new ArrayList<>();
//        addresses.add(getAddress("서울특별시", "", "종로구", "효자동"));
//        addresses.add(getAddress("서울특별시", "", "광진구", "능동"));
//        addresses.add(getAddress("부산광역시", "기장군", "", "내리"));
//        addresses.add(getAddress("부산광역시", "", "금정구", "금사동"));
//        addresses.add(getAddress("부산광역시", "", "수영구", "민락동"));
//        addresses.add(getAddress("대구광역시", "", "동구", "대림동"));
//        addresses.add(getAddress("전라남도", "여수시", "", "종화동"));
//        addresses.add(getAddress("전라북도", "남원시", "", "동충동"));
//        addresses.add(getAddress("경상북도", "영주시", "", "영주동"));
//        addresses.add(getAddress("경상남도", "진주시", "", "망경동"));
//        addresses.add(getAddress("충청남도", "공주시", "", "반죽동"));
//        addressRepository.saveAll(addresses);
//    }

    @Test
    void 주소목록() {

        // Given
        // When
        // Then
        List<Address> targetStateList = addressRepository.findAllByState("부산광역시");
        // assertThat(targetStateList).hasSize(3);
        System.out.println(targetStateList.size());
        List<Address> targetGuList = addressRepository.findAllByStateAndGu("부산광역시", "금정구");
        // assertThat(targetGuList).hasSize(1);
        System.out.println(targetGuList.size());
        List<Address> targetSigunList =addressRepository.findAllByStateAndSiGun("부산광역시", "기장군");
        // assertThat(targetSigunList).hasSize(1);
        System.out.println(targetSigunList.size());
    }

    @Test
    void 시군구조회() {

        // Given
        // When
        final String targetState = "부산광역시";
        List<Address> siGunGuByState = addressRepository.findSiGunGuByState(targetState);

        Set<String> siGunSet = siGunGuByState.stream()
                                             .map(address -> address.getSiGun())
                                             .collect(Collectors.toSet());
        Set<String> guSet = siGunGuByState.stream()
                                          .map(address -> address.getGu())
                                          .collect(Collectors.toSet());

        siGunGuByState.forEach(address -> {
            String state = address.getState();
            String siGun = address.getSiGun();
            String gu = address.getGu();

            assertThat(state).isEqualTo(targetState);
            siGunSet.remove(siGun);
            guSet.remove(gu);
        });

        // Then
        assertThat(siGunSet).isNullOrEmpty();
        assertThat(guSet).isNullOrEmpty();
    }

    // @Test
    void 동조회() {
    }

}