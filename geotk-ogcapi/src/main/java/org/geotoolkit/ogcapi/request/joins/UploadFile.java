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
 * <blockquote><q>
 *     If the input data file is uploaded with the query, it SHALL contain the header:
 *     Content-Disposition: form-data; filename="[right dataset file's name]";
 *     name="right-dataset-file";
 *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/22-026.html#_5c23e155-e422-4efe-a597-6da449a6f81b"> OGC API - Joins - Part 1: Core - 8.6. Join Source Creation</a></small></i>
 * </blockquote>
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
     * @see #fileName(java.lang.String)
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @see #fileName(java.lang.String)
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * <blockquote><q>
     *     If the input data file is uploaded with the query, it SHALL contain the header:
     *     Content-Disposition: form-data; filename="[right dataset file's name]";
     *     name="right-dataset-file";
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/22-026.html#_5c23e155-e422-4efe-a597-6da449a6f81b"> OGC API - Joins - Part 1: Core - 8.6. Join Source Creation</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required; sent as the part's {@code Content-Disposition} {@code filename} parameter.</li>
     * </ul>
     */
    public UploadFile fileName(String fileName) {
        setFileName(fileName);
        return this;
    }

    /**
     * @see #contentType(java.lang.String)
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @see #contentType(java.lang.String)
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Media type of the uploaded file, sent as the part's {@code Content-Type} header.
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Not defined by OGC API - Joins - Part 1: Core (draft), which does not mandate a
     *     per-part {@code Content-Type} header for the uploaded file at the time of writing;
     *     this is a server-specific extension (no verbatim spec text available for this field).</li>
     * </ul>
     */
    public UploadFile contentType(String contentType) {
        setContentType(contentType);
        return this;
    }

    /**
     * @see #content(byte[])
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * @see #content(byte[])
     */
    public void setContent(byte[] content) {
        this.content = content;
    }

    /**
     * <blockquote><q>
     *     The input data file SHALL be encoded with the UTF-8 encoding.
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/22-026.html#_5c23e155-e422-4efe-a597-6da449a6f81b"> OGC API - Joins - Part 1: Core - 8.6. Join Source Creation</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required; the uploaded file's raw bytes.</li>
     *     <li>Must be UTF-8 encoded.</li>
     * </ul>
     */
    public UploadFile content(byte[] content) {
        setContent(content);
        return this;
    }
}
