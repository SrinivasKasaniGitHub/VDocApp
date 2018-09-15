package com.quickboxdemo.adapters;

/**
 * Created by Srinivas on 8/21/2018.
 */

public class SecondarySymptoms {
    private String secondarySymptom;

    public String getSecondarySymptom ()
    {
        return secondarySymptom;
    }

    public void setSecondarySymptom (String secondarySymptom)
    {
        this.secondarySymptom = secondarySymptom;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [secondarySymptom = "+secondarySymptom+"]";
    }

}
