package org.codehaus.mojo.buildhelper.versioning;

public interface Versioning
{

    boolean isSnapshot();
    
    int getMajor();

    int getMinor();

    int getPatch();

    String getAsOSGiVersion();

    int getBuildNumber();

    String getQualifier();

}
