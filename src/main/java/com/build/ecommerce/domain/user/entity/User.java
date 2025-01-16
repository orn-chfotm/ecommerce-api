package com.build.ecommerce.domain.user.entity;

import com.build.ecommerce.core.util.BaseEntity;
import com.build.ecommerce.domain.address.entity.AddressEntity;
import com.build.ecommerce.domain.order.entity.Order;
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
public class User extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    @Comment(value = "이메일 주소(ID), unique and not null")
    @Column(unique = true, nullable = false)
    private String email;

    @Comment("비밀번호, not null")
    @Column(nullable = false)
    private String password;

    @Comment("회원 이름, not null")
    @Column(nullable = false)
    private String name;

    @Comment("성별, not null")
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Gender gender;

    @Comment("생년월일, not null")
    @Column(nullable = false)
    private LocalDate birthDate;

    @Comment("사용자 배송지 주소")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "USER_ID")
    private List<AddressEntity> addressEntityList = new ArrayList<>();

    @Comment("주문 내역")
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private List<Order> orders = new ArrayList<>();

    @Builder
    public User(String email, String password, String name, Gender gender, LocalDate birthDate) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
    }

    public void addAddr(AddressEntity addressEntity) {
        this.addressEntityList.add(addressEntity);
    }
}
