package com.build.ecommerce.domain.admin.entity;

import com.build.ecommerce.core.persistence.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "ADMINS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Comment(value = "Admin information table", on = "TABLE")
public class Admin extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ADMIN_ID")
    private Long id;

    @Comment("관리자 ID, unique and not null")
    @Column(unique = true, nullable = false)
    private String email;

    @Comment("관리자 PW, not null")
    @Column(nullable = false)
    private String password;

    @Comment("관리자 이름, not null")
    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment(value = "admin role")
    private AdminRoleType role;

    @Builder
    private Admin(String email, String password, String name, AdminRoleType role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }
}

