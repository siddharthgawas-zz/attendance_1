package org.pccegoa.studentapp.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Siddharth on 3/20/2018.
 */

public  class AttendanceClientCreator {
    private static final String AUTHORITY = "https://student-app-api.herokuapp.com";

    public static AttendanceApiClient createApiClient()
    {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(AUTHORITY)
                .addConverterFactory(GsonConverterFactory.create()).build();
        AttendanceApiClient service = retrofit.create(AttendanceApiClient.class);
        return service;
    }
}
