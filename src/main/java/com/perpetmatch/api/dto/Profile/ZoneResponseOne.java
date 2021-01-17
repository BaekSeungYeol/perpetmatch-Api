package com.perpetmatch.api.dto.Profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZoneResponseOne {
    List<String> zones;
    List<String> allZones;
}
