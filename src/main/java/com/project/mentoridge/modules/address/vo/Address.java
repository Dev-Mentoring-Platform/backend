package com.project.mentoridge.modules.address.vo;

import lombok.*;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.*;

@Getter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "address"
        //, indexes = @Index(name = "IDX_ADDRESS", columnList = "state, siGun, gu, dongMyunLi")
)
public class Address {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "address_id")
    private Long id;

    @Column(length = 50)
    private String state;

    @Column(length = 50)
    private String siGun;

    @Column(length = 50)
    private String gu;

    @Column(length = 50)
    private String dongMyunLi;

    @Builder(access = PUBLIC)
    private Address(String state, String siGun, String gu, String dongMyunLi) {
        // this.id = addressId;

        this.state = state;
        this.siGun = siGun;
        this.gu = gu;
        this.dongMyunLi = dongMyunLi;
    }

    public Address(String state, String siGun, String gu) {
        this.state = state;
        this.siGun = siGun;
        this.gu = gu;
    }
}
