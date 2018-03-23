package org.pccegoa.studentapp.utility;

import org.pccegoa.studentapp.api.AttendanceRecord;

/**
 * Created by Siddharth on 3/23/2018.
 */

public class AttendancePredictor {
    private float mCutOffPercentage;

    public AttendancePredictor(float mCutOffPercentage) {
        this.mCutOffPercentage = mCutOffPercentage;
    }

    public int classesToAttend(int p, int a, int d)
    {
        float x = (mCutOffPercentage*a)/(1-mCutOffPercentage) - (p+d);
        return (int)Math.ceil(x);
    }

    public int classesToBunk(int p, int a, int d)
    {
        float x = ((1-mCutOffPercentage)/mCutOffPercentage)*(p+d) - a;
        return (int)Math.floor(x);
    }

    public static void main(String[] args)
    {
        float c = 0.75f;
        AttendancePredictor predictor = new AttendancePredictor(c);
        int  p = 5;
        int d = 2;
        int a = 2;
        System.out.println(predictor.classesToAttend(p,a,d));
        System.out.println(predictor.classesToBunk(p,a,d));
    }
}
