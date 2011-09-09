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

import javax.imageio.spi.ImageWriterSpi;
import static org.geotoolkit.util.ArgumentChecks.*;

/**
 * Output definition, several parameters are available
 * to configure the ImageIO writer, like compression or progressive.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class OutputDef {

    private ImageWriterSpi spi;
    private Object output;
    private String mime;
    private Float compression = null;
    private Boolean progressive = null;
    private String compressionType = null;


    public OutputDef(final String mime, final Object output) {
        this(mime,output,null);
    }

    public OutputDef(final String mime, final Object output, final Float compression) {
        ensureNonNull("output", output);
        ensureNonNull("mime", mime);
        this.output = output;
        this.mime = mime;
        this.compression = compression;
    }

    public Object getOutput() {
        return output;
    }

    public void setOutput(final Object output) {
        ensureNonNull("output", output);
        this.output = output;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(final String mime) {
        ensureNonNull("mime", mime);
        this.mime = mime;
    }

    public void setCompression(final Float compression) {

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

    public void setCompressionType(final String compressionType) {
        this.compressionType = compressionType;
    }

    public void setProgressive(final Boolean progressive) {
        this.progressive = progressive;
    }

    public Boolean getProgressive() {
        return progressive;
    }

    public ImageWriterSpi getSpi() {
        return spi;
    }

    public void setSpi(ImageWriterSpi spi) {
        this.spi = spi;
    }

    @Override
    public String toString() {
        return "OutputDef[mime=" + mime + ", output=" + output.toString() +
                ", compressionType="+compressionType+", compressionLevel="+compression+", progressive="+progressive+"]";
    }
}
