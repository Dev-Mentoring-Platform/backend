package com.project.mentoridge.modules.address.mapstruct;

import com.project.mentoridge.config.mapstruct.MapstructConfig;
import com.project.mentoridge.modules.address.controller.response.AddressResponse;
import com.project.mentoridge.modules.address.controller.response.DongResponse;
import com.project.mentoridge.modules.address.controller.response.SiGunGuResponse;
import com.project.mentoridge.modules.address.vo.Address;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(config = MapstructConfig.class)
public interface AddressMapstruct {

    @Mappings({
//            @Mapping(target = "siGunGu", expression = "java()")
    })
    AddressResponse addressToAddressResponse(Address address);

    @IterableMapping(elementTargetType = AddressResponse.class)
    List<AddressResponse> addressListToAddressResponseList(List<Address> states);

    @Mappings({})
    SiGunGuResponse addressToSiGunGuResponse(Address address);

    @IterableMapping(elementTargetType = SiGunGuResponse.class)
    List<SiGunGuResponse> addressListToSiGunGuResponseList(List<Address> addressList);

    @Mappings({})
    DongResponse addressToDongResponse(Address address);

    @IterableMapping(elementTargetType = DongResponse.class)
    List<DongResponse> addressListToDongResponseList(List<Address> addressList);
}
