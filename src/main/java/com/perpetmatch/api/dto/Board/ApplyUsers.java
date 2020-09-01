package com.perpetmatch.api.dto.Board;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Basic;
import javax.persistence.FetchType;
import javax.persistence.Lob;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplyUsers {
    private Long id;
    private String nickname;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;
    @Lob @Basic(fetch = FetchType.EAGER)
    private String description;

}
