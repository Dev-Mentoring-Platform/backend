package com.project.mentoridge.modules.address.embeddable;

import lombok.*;

import javax.persistence.Embeddable;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Address {

    private String state;
    private String siGunGu;
    private String dongMyunLi;

    @Override
    public String toString() {
        return state + " " + siGunGu + " " + dongMyunLi;
    }

    @Builder(access = AccessLevel.PRIVATE)
    private Address(String state, String siGunGu, String dongMyunLi) {
        this.state = state;
        this.siGunGu = siGunGu;
        this.dongMyunLi = dongMyunLi;
    }

    public static Address of(String state, String siGunGu, String dongMyunLi) {
        return Address.builder()
                .state(state)
                .siGunGu(siGunGu)
                .dongMyunLi(dongMyunLi)
                .build();
    }
}
