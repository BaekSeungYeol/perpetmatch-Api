package com.perpetmatch.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenTest {

    private String accessToken;
    private String tokenType;
}
