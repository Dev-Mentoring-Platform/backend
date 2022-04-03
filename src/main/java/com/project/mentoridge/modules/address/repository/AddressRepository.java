package com.project.mentoridge.modules.address.repository;

import com.project.mentoridge.modules.address.vo.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findAllByState(String state);

    List<Address> findAllByStateAndGu(String state, String gu);

    List<Address> findAllByStateAndSiGun(String state, String sigun);

    @Query(value = "select distinct state from address", nativeQuery = true)
    List<String> findStates();

    // @Query(value = "select distinct state, si_gun, gu from address where state = :state", nativeQuery = true)
    @Query("select distinct new Address(a.state, a.siGun, a.gu) from Address a where a.state = :state")
    List<Address> findSiGunGuByState(@Param("state") String state);

    @Query(value = "select dong_myun_li from address where state = :state and concat(si_gun, ' ', gu) like %:siGunGu%", nativeQuery = true)
    List<String> findDongByStateAndSiGunGu(@Param("state") String state, @Param("siGunGu") String siGunGu);
}
