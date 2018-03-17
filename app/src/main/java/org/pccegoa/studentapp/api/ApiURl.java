package org.pccegoa.studentapp.api;

import android.net.Uri;

/**
 * Created by siddharth on 26/1/18.
 */

public final class ApiURl {
    private final static String AUTHORITY = "student-app-api.herokuapp.com";
    public final static Uri BASE_URI = Uri.EMPTY.buildUpon().scheme("https")
            .encodedAuthority(AUTHORITY).build();
    public final static Uri LOGIN_URI = BASE_URI.buildUpon().appendEncodedPath("login/").build();
}
