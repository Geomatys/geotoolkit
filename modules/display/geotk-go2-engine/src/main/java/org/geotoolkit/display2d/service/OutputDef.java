/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.display2d.service;

/**
 * Output definition, several parameters are available
 * to configure the ImageIO writer, like compression or progressive.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class OutputDef {

    private Object output;
    private String mime;
    private Float compression = null;
    private Boolean progressive = null;
    private String compressionType = null;


    public OutputDef(String mime, Object output) {
        if(output == null){
            throw new NullPointerException("Output must not be null");
        }
        if(mime == null){
            throw new NullPointerException("Mime type must not be null");
        }
        this.output = output;
        this.mime = mime;
    }

    public OutputDef(String mime, Object output, Float compression) {
        if(output == null){
            throw new NullPointerException("Output must not be null");
        }
        if(mime == null){
            throw new NullPointerException("Mime type must not be null");
        }
        this.output = output;
        this.mime = mime;
        this.compression = compression;
    }

    public Object getOutput() {
        return output;
    }

    public void setOutput(Object output) {

        if(output == null){
            throw new NullPointerException("Output must not be null");
        }

        this.output = output;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {

        if(mime == null){
            throw new NullPointerException("Mime type must not be null");
        }

        this.mime = mime;
    }

    public void setCompression(Float compression) {

        if(compression != null){
            if( compression <0 || compression>1.001){
                throw new IllegalArgumentException("Compression level must be between 0 and 1");
            }
        }

        this.compression = compression;
    }

    public Float getCompression() {
        return compression;
    }

    public String getCompressionType() {
        return compressionType;
    }

    public void setCompressionType(String compressionType) {
        this.compressionType = compressionType;
    }

    public void setProgressive(Boolean progressive) {
        this.progressive = progressive;
    }

    public Boolean getProgressive() {
        return progressive;
    }

    @Override
    public String toString() {
        return "OutputDef[mime=" + mime + ", output=" + output.toString() +
                ", compressionType="+compressionType+", compressionLevel="+compression+", progressive="+progressive+"]";
    }
}
