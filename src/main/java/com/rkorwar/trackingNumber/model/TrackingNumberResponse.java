package com.rkorwar.trackingNumber.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public class TrackingNumberResponse {

    private String tracking_number;
    private String created_at;

}
