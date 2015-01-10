package org.codehaus.mojo.buildhelper;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.io.File;

/**
 * <p>
 * Artifact class.
 * </p>
 *
 * @author dtran
 * @version $Id$
 */
public class Artifact
{
    private File file;

    private String type = "jar";

    private String classifier;

    /**
     * <p>
     * Setter for the field <code>file</code>.
     * </p>
     *
     * @param localFile a {@link java.io.File} object.
     */
    public void setFile( File localFile )
    {
        this.file = localFile;
    }

    /**
     * <p>
     * Getter for the field <code>file</code>.
     * </p>
     *
     * @return a {@link java.io.File} object.
     */
    public File getFile()
    {
        return this.file;
    }

    /**
     * <p>
     * Setter for the field <code>type</code>.
     * </p>
     *
     * @param type a {@link java.lang.String} object.
     */
    public void setType( String type )
    {
        this.type = type;
    }

    /**
     * <p>
     * Getter for the field <code>type</code>.
     * </p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getType()
    {
        return this.type;
    }

    /**
     * <p>
     * Setter for the field <code>classifier</code>.
     * </p>
     *
     * @param classifier a {@link java.lang.String} object.
     */
    public void setClassifier( String classifier )
    {
        this.classifier = classifier;
    }

    /**
     * <p>
     * Getter for the field <code>classifier</code>.
     * </p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getClassifier()
    {
        return this.classifier;
    }
}
