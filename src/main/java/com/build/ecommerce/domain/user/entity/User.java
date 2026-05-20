package com.build.ecommerce.domain.user.entity;

import com.build.ecommerce.core.persistence.BaseTimeEntity;
import com.build.ecommerce.domain.address.entity.Address;
import com.build.ecommerce.domain.order.entity.Order;
import com.build.ecommerce.domain.product.entity.ProductWish;
import com.build.ecommerce.domain.user.enums.GenderType;
import com.build.ecommerce.domain.user.enums.UserRoleType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "USERS")
@Comment(value = "User Information Table", on = "TABLE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    @Column(unique = true, nullable = false)
    @Comment(value = "이메일 주소(ID), unique and not null")
    private String email;

    @Column(nullable = false)
    @Comment("비밀번호, not null")
    private String password;

    @Column(nullable = false)
    @Comment("회원 이름, not null")
    private String name;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    @Comment("성별, not null")
    private GenderType gender;

    @Column(nullable = false)
    @Comment("생년월일, not null")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment(value = "user role")
    private UserRoleType role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("사용자 배송지 주소")
    private List<Address> addressList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    @Comment("주문 내역")
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("찜 리스트")
    private List<ProductWish> productWish = new ArrayList<>();

    @Builder
    public User(String email, String password, String name, GenderType gender, LocalDate birthDate, UserRoleType role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
        this.role = role;
    }

    public void addAddress(Address address) {
        this.addressList.add(address);
        address.changeUser(this);
    }

    public void removeAddress(Address address) {
        this.addressList.remove(address);
        address.changeUser(null);
    }
}
