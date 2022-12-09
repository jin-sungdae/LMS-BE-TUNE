package com.savelms.core.user.role.domain.entity;

import com.savelms.core.BaseEntity;
import com.savelms.core.user.domain.entity.User;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "USER_ROLE")
@Entity
@Builder(access = AccessLevel.PRIVATE)
public class UserRole extends BaseEntity implements Serializable    {

    //********************************* static final 상수 필드 *********************************/


    /********************************* PK 필드 *********************************/

    /**
     * 기본 키
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ROLE_ID")
    private Long id;

    /********************************* PK가 아닌 필드 *********************************/

    private String reason;

    @Column(nullable = false)
    private Boolean currentlyUsed;
    /********************************* 비영속 필드 *********************************/


    /********************************* 연관관계 매핑 *********************************/


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="USER_ID", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="ROLE_ID", nullable = false)
    private Role role;

    /********************************* 연관관계 편의 메서드 *********************************/
    public void setUserAndAddUserRoleToUser(User user) {
        user.getUserRoles().add(this);
        this.user = user;
    }
    /********************************* 생성 메서드 *********************************/
    public static UserRole createUserRole(User user, Role role, String reason, Boolean currentlyUsed) {
        UserRole userRole = UserRole.builder()
            .role(role)
            .reason(reason)
            .currentlyUsed(currentlyUsed)
            .build();
        userRole.setUserAndAddUserRoleToUser(user);
        return userRole;
    }



    /********************************* 비니지스 로직 *********************************/

    public void notCurrentlyUsed() {
        this.currentlyUsed = false;
    }

}