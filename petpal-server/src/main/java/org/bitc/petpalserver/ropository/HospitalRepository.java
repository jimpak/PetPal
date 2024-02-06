package org.bitc.petpalserver.ropository;

import org.bitc.petpalserver.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {
}
