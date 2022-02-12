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

    @Query(nativeQuery = true,
           value = "SELECT DISTINCT state FROM address")
    List<String> findStates();

    // TODO - state, si_gun, gu
    @Query(nativeQuery = true,
           value = "SELECT * FROM address WHERE state = :state GROUP BY state, si_gun, gu")
    List<Address> findSiGunGuByState(@Param("state") String state);

    @Query(nativeQuery = true,
           value = "SELECT dong_myun_li FROM address where state = :state and concat(si_gun, ' ', gu) like %:siGunGu%")
    List<String> findDongByStateAndSiGunGu(@Param("state") String state, @Param("siGunGu") String siGunGu);
}
