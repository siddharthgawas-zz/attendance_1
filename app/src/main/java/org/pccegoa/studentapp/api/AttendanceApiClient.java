package org.pccegoa.studentapp.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Siddharth on 3/20/2018.
 */

public interface AttendanceApiClient {
    String APPLICATION_KEY= "test-key";

    @Headers("X-Application-Key: "+APPLICATION_KEY)
    @GET("get-attendance-percentile/{year}/{semester}/{userID}/")
    Call<AttendancePercentile> getAttendancePercentile(@Header("Authorization") String authorization,
                                                       @Path("year") int year,
                                                       @Path("semester") int semester,
                                                       @Path("userID") int userID);
    @Headers("X-Application-Key: "+APPLICATION_KEY)
    @GET("get-attendance/{year}/{semester}/{rollNo}/")
    Call<AttendanceList> getAttendancRecordsFromTo(@Header("Authorization") String authorization,
                                             @Path("year") int year,
                                             @Path("semester") int semester,
                                             @Path("rollNo") String rollNo,
                                             @Query("from_date") String fromDate,
                                             @Query("to_date") String toDate);
}
