package org.codehaus.mojo.buildhelper.versioning;

public interface Versioning
{

    int getMajor();

    int getMinor();

    int getPatch();

    String getAsOSGiVersion();

    long getBuildNumber();

    String getQualifier();

}
