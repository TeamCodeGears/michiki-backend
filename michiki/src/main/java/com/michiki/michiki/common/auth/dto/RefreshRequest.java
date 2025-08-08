package com.michiki.michiki.common.auth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RefreshRequest {

    @NotNull
    private String refreshToken;
}
