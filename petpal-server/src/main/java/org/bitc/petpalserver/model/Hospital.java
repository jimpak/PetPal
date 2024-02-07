package org.bitc.petpalserver.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Hospital {
    @Id private Long hno;
    private String gugun; // 구
    private String animal_hospital; // 동물병원 이름
    private String approval; // 창업일
    private String road_address; // 주소
    private String tel; // 전화번호
    private Double lat; // 위도
    private Double lon; // 경도
    private String basic_date; // ????
}