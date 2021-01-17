package com.perpetmatch.Zone.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoneRepository extends JpaRepository<Zone,Long> {
    Zone findByProvince(String province);
}
