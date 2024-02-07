package org.bitc.petpalserver.controller;

import lombok.RequiredArgsConstructor;
import org.bitc.petpalserver.model.Hospital;
import org.bitc.petpalserver.ropository.HospitalRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalRepository hospitalRepository;

    @GetMapping("/hospitals")
    public Map<String, List<Hospital>> getMap() {
        Map<String, List<Hospital>> map = new HashMap<>();
        map.put("hospitals", hospitalRepository.findAll());
        return map;
    }

}
