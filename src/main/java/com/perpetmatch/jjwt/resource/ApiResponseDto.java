package com.perpetmatch.jjwt.resource;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Getter
public class ApiResponseDto<T> {
    public static final ApiResponseDto DEFAULT_OK = new ApiResponseDto<>(ApiResponseCode.OK);
    public static final ApiResponseDto DEFAULT_UNAUTHORIZED = new ApiResponseDto<>(ApiResponseCode.UNAUTHORIZED);
    public static final ApiResponseDto Not_Found = new ApiResponseDto<>(ApiResponseCode.NOT_FOUND);
    private ApiResponseCode code;
    private String message;
    private T data;

    private ApiResponseDto(ApiResponseCode status) {
        this.bindStatus(status);
    }
    private ApiResponseDto(ApiResponseCode status, T data) {
        this.bindStatus(status);
        this.data = data;
    }



    private void bindStatus(ApiResponseCode status) {
        this.code = status;
        this.message = status.getMessage();
    }

    public static <T> ApiResponseDto<T> createOK(T data) {
        return new ApiResponseDto<>(ApiResponseCode.OK, data);
    }
    public static <T> ApiResponseDto<T> createOK() {
        return new ApiResponseDto<>(ApiResponseCode.OK);
    }
    public static <T> ApiResponseDto<T> badRequest() { return new ApiResponseDto<>(ApiResponseCode.BAD_PARAMETER);}
}
