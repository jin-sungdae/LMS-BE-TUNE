package com.savelms.core.team.domain.entity;

import com.savelms.core.BaseEntity;
import com.savelms.core.team.TeamEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "TEAM")
@Entity
@Builder
public class Team extends BaseEntity {

    //********************************* static final 상수 필드 *********************************/

    /********************************* PK 필드 *********************************/

    /**
     * 기본 키
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TEAM_ID")
    private Long id;

    /********************************* PK가 아닌 필드 *********************************/

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamEnum value;

    /********************************* 비영속 필드 *********************************/

    /********************************* 연관관계 매핑 *********************************/



    /********************************* 비니지스 로직 *********************************/


}
