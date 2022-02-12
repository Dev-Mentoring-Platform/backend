package com.project.mentoridge.modules.address.util;

import com.project.mentoridge.modules.address.embeddable.Address;
import org.apache.commons.lang3.StringUtils;

import static com.project.mentoridge.utils.CommonUtil.EMPTY_STRING;
import static com.project.mentoridge.utils.CommonUtil.SPACE;

public class AddressUtils {

    private AddressUtils() {}

    public static String convertAddress(String address) {
        if (StringUtils.isBlank(address)) {
            return EMPTY_STRING;
        }
        return address;
    }

    public static Address convertStringToEmbeddableAddress(String address) {
        if (StringUtils.isBlank(address)) {
            return null;
        }

        String[] split = address.split(SPACE);
        if (split.length == 2) {
            return Address.of(split[0], split[1], null);
        } else if (split.length == 3) {
            return Address.of(split[0], split[1], split[2]);
        }
        return null;
    }

    public static String convertEmbeddableToStringAddress(Address address) {
        if (address == null) {
            return null;
        }
        return address.toString();
    }

}
