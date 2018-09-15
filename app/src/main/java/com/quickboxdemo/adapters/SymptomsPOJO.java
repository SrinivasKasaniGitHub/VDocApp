package com.quickboxdemo.adapters;

/**
 * Created by Srinivas on 8/21/2018.
 */

public class SymptomsPOJO {

    private SecondarySymptoms[] secondarySymptoms;

    private String primarySymptom;

    public SecondarySymptoms[] getSecondarySymptoms ()
    {
        return secondarySymptoms;
    }

    public void setSecondarySymptoms (SecondarySymptoms[] secondarySymptoms)
    {
        this.secondarySymptoms = secondarySymptoms;
    }

    public String getPrimarySymptom ()
    {
        return primarySymptom;
    }

    public void setPrimarySymptom (String primarySymptom)
    {
        this.primarySymptom = primarySymptom;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [secondarySymptoms = "+secondarySymptoms+", primarySymptom = "+primarySymptom+"]";
    }
}
