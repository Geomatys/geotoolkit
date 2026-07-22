/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.ogcapi.request.joins;

/**
 * A file to be uploaded as a {@code multipart/form-data} part, for the Joins operations that
 * support the "Input File Upload" requirements class (spec section 11).
 *
 * @author Johann Sorel (Geomatys)
 */
public final class UploadFile {

    private String fileName;
    private String contentType;
    private byte[] content;

    public UploadFile() {
    }

    public UploadFile(String fileName, String contentType, byte[] content) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.content = content;
    }

    /**
     * Name of the uploaded file, sent in the part's {@code Content-Disposition} header.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @see #getFileName()
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @see #getFileName()
     */
    public UploadFile fileName(String fileName) {
        setFileName(fileName);
        return this;
    }

    /**
     * Media type of the uploaded file, sent as the part's {@code Content-Type} header. (optional)
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @see #getContentType()
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * @see #getContentType()
     */
    public UploadFile contentType(String contentType) {
        setContentType(contentType);
        return this;
    }

    /**
     * Content of the uploaded file, encoded with UTF-8 as required by the specification.
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * @see #getContent()
     */
    public void setContent(byte[] content) {
        this.content = content;
    }

    /**
     * @see #getContent()
     */
    public UploadFile content(byte[] content) {
        setContent(content);
        return this;
    }
}
