package org.bitc.petpalapp.ui.hospital

data class HospitalModel (
    var hno: Long,
    var gugun: String, // 구
    var animal_hospital: String, // 병원이름
    var approval: String, // 창업일
    var road_address: String, // 주소
    var tel: String, // 전화번호
    var lat: Double, // 위도
    var lon: Double, // 경도
    var basic_date: String // ????
)
