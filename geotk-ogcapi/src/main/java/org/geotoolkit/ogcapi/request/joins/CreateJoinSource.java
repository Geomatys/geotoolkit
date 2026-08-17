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

import java.util.List;
import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * Parameters for {@code POST /collections/{collectionId}/joins}, sent as a
 * {@code multipart/form-data} request (spec section 8.6.1, Table 5).
 *
 * <blockquote><q>
 *     The HTTP POST operation at path {root}/collections/{collectionId}/joins creates a new
 *     join source.
 *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/22-026.html#_5c23e155-e422-4efe-a597-6da449a6f81b"> OGC API - Joins - Part 1: Core - 8.6. Join Source Creation</a></small></i>
 * </blockquote>
 *
 * <p>
 * Exactly one of {@link #getRightDatasetFile()} or {@link #getRightDatasetUrl()} must be set; the
 * server responds with HTTP 400 if both or neither are provided. This class does not enforce that
 * rule client-side.</p>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class CreateJoinSource extends RequestParameters {

    private String collectionId;
    private String collectionKey;
    private String rightDatasetFormat;
    private UploadFile rightDatasetFile;
    private String rightDatasetUrl;
    private String rightDatasetKey;
    private List<String> rightDatasetDataValueList;
    private String csvFileDelimiter;
    private Integer csvFileHeaderRowNumber;
    private Integer csvFileDataStartRowNumber;

    /**
     * @see #collectionId(java.lang.String)
     */
    public String getCollectionId() {
        return collectionId;
    }

    /**
     * @see #collectionId(java.lang.String)
     */
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    /**
     * <blockquote><q>
     *     The parameter collectionId SHALL be each id property in the collections response
     *     (JSONPath: $.collections[*].id).
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/22-026.html#_4a651dea-0917-4591-886b-31d2043091bc"> OGC API - Joins - Part 1: Core - 8.4. Collection's Key Fields</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>Must match the {@code id} of an existing collection (see {@code GET /collections}).</li>
     * </ul>
     */
    public CreateJoinSource collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * @see #collectionKey(java.lang.String)
     */
    public String getCollectionKey() {
        return collectionKey;
    }

    /**
     * @see #collectionKey(java.lang.String)
     */
    public void setCollectionKey(String collectionKey) {
        this.collectionKey = collectionKey;
    }

    /**
     * <blockquote><q>
     *     The value of the id attribute of the key field of the collection that will be used in
     *     the join operation.
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/22-026.html#_5c23e155-e422-4efe-a597-6da449a6f81b"> OGC API - Joins - Part 1: Core - 8.6. Join Source Creation</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; form field {@code collection-key}.</li>
     *     <li>If not provided, a default key field value of the collection is used in the join
     *     operation.</li>
     * </ul>
     */
    public CreateJoinSource collectionKey(String collectionKey) {
        setCollectionKey(collectionKey);
        return this;
    }

    /**
     * @see #rightDatasetFormat(java.lang.String)
     */
    public String getRightDatasetFormat() {
        return rightDatasetFormat;
    }

    /**
     * @see #rightDatasetFormat(java.lang.String)
     */
    public void setRightDatasetFormat(String rightDatasetFormat) {
        this.rightDatasetFormat = rightDatasetFormat;
    }

    /**
     * <blockquote><q>
     *     The format of the attribute dataset.
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/22-026.html#_5c23e155-e422-4efe-a597-6da449a6f81b"> OGC API - Joins - Part 1: Core - 8.6. Join Source Creation</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required; form field {@code right-dataset-format}.</li>
     *     <li>The core module contains support for the format
     *     {@code http://www.opengis.net/spec/ogcapi-joins-1/1.0/conf/input-csv}.</li>
     * </ul>
     */
    public CreateJoinSource rightDatasetFormat(String rightDatasetFormat) {
        setRightDatasetFormat(rightDatasetFormat);
        return this;
    }

    /**
     * @see #rightDatasetFile(org.geotoolkit.ogcapi.request.joins.UploadFile)
     */
    public UploadFile getRightDatasetFile() {
        return rightDatasetFile;
    }

    /**
     * @see #rightDatasetFile(org.geotoolkit.ogcapi.request.joins.UploadFile)
     */
    public void setRightDatasetFile(UploadFile rightDatasetFile) {
        this.rightDatasetFile = rightDatasetFile;
    }

    /**
     * <blockquote><q>
     *     The attribute dataset file (uploaded file).
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/22-026.html#_5c23e155-e422-4efe-a597-6da449a6f81b"> OGC API - Joins - Part 1: Core - 8.6. Join Source Creation</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; form field {@code right-dataset-file}.</li>
     *     <li>One of this field or {@link #rightDatasetUrl(java.lang.String)} is mandatory; the
     *     server responds with HTTP 400 if both or neither are provided.</li>
     * </ul>
     */
    public CreateJoinSource rightDatasetFile(UploadFile rightDatasetFile) {
        setRightDatasetFile(rightDatasetFile);
        return this;
    }

    /**
     * @see #rightDatasetUrl(java.lang.String)
     */
    public String getRightDatasetUrl() {
        return rightDatasetUrl;
    }

    /**
     * @see #rightDatasetUrl(java.lang.String)
     */
    public void setRightDatasetUrl(String rightDatasetUrl) {
        this.rightDatasetUrl = rightDatasetUrl;
    }

    /**
     * <blockquote><q>
     *     The attribute dataset URL.
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/22-026.html#_5c23e155-e422-4efe-a597-6da449a6f81b"> OGC API - Joins - Part 1: Core - 8.6. Join Source Creation</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; form field {@code right-dataset-url}.</li>
     *     <li>One of this field or {@link #rightDatasetFile(org.geotoolkit.ogcapi.request.joins.UploadFile)}
     *     is mandatory; the server responds with HTTP 400 if both or neither are provided.</li>
     * </ul>
     */
    public CreateJoinSource rightDatasetUrl(String rightDatasetUrl) {
        setRightDatasetUrl(rightDatasetUrl);
        return this;
    }

    /**
     * @see #rightDatasetKey(java.lang.String)
     */
    public String getRightDatasetKey() {
        return rightDatasetKey;
    }

    /**
     * @see #rightDatasetKey(java.lang.String)
     */
    public void setRightDatasetKey(String rightDatasetKey) {
        this.rightDatasetKey = rightDatasetKey;
    }

    /**
     * <blockquote><q>
     *     The key field in the attribute dataset that contains the key values.
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/22-026.html#_5c23e155-e422-4efe-a597-6da449a6f81b"> OGC API - Joins - Part 1: Core - 8.6. Join Source Creation</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required; form field {@code right-dataset-key}.</li>
     *     <li>For CSV format, this is the column number containing the key values (counting
     *     starts from 0).</li>
     * </ul>
     */
    public CreateJoinSource rightDatasetKey(String rightDatasetKey) {
        setRightDatasetKey(rightDatasetKey);
        return this;
    }

    /**
     * @see #rightDatasetDataValueList(java.util.List)
     */
    public List<String> getRightDatasetDataValueList() {
        return rightDatasetDataValueList;
    }

    /**
     * @see #rightDatasetDataValueList(java.util.List)
     */
    public void setRightDatasetDataValueList(List<String> rightDatasetDataValueList) {
        this.rightDatasetDataValueList = rightDatasetDataValueList;
    }

    /**
     * <blockquote><q>
     *     Fields in the attribute dataset that contain the attribute values that will be joined.
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/22-026.html#_5c23e155-e422-4efe-a597-6da449a6f81b"> OGC API - Joins - Part 1: Core - 8.6. Join Source Creation</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required; form field {@code right-dataset-data-value-list}, sent as a
     *     comma-separated string.</li>
     *     <li>For CSV format, the values are the column numbers to join (counting starts from 0).</li>
     * </ul>
     */
    public CreateJoinSource rightDatasetDataValueList(List<String> rightDatasetDataValueList) {
        setRightDatasetDataValueList(rightDatasetDataValueList);
        return this;
    }

    /**
     * @see #csvFileDelimiter(java.lang.String)
     */
    public String getCsvFileDelimiter() {
        return csvFileDelimiter;
    }

    /**
     * @see #csvFileDelimiter(java.lang.String)
     */
    public void setCsvFileDelimiter(String csvFileDelimiter) {
        this.csvFileDelimiter = csvFileDelimiter;
    }

    /**
     * <blockquote><q>
     *     The delimiter character used in the CSV file.
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/22-026.html#_5c23e155-e422-4efe-a597-6da449a6f81b"> OGC API - Joins - Part 1: Core - 8.6. Join Source Creation</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; form field {@code csv-file-delimiter}.</li>
     *     <li>Mandatory when {@link #rightDatasetFormat(java.lang.String)} is the CSV input
     *     format conformance class; not required for other formats.</li>
     * </ul>
     */
    public CreateJoinSource csvFileDelimiter(String csvFileDelimiter) {
        setCsvFileDelimiter(csvFileDelimiter);
        return this;
    }

    /**
     * @see #csvFileHeaderRowNumber(java.lang.Integer)
     */
    public Integer getCsvFileHeaderRowNumber() {
        return csvFileHeaderRowNumber;
    }

    /**
     * @see #csvFileHeaderRowNumber(java.lang.Integer)
     */
    public void setCsvFileHeaderRowNumber(Integer csvFileHeaderRowNumber) {
        this.csvFileHeaderRowNumber = csvFileHeaderRowNumber;
    }

    /**
     * <blockquote><q>
     *     If omitted, the header row is expected to be at the first row of the CSV file. The
     *     names of the joined attributes in the result dataset are the names of the columns from
     *     the CSV file header row.
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/22-026.html#_5c23e155-e422-4efe-a597-6da449a6f81b"> OGC API - Joins - Part 1: Core - 8.6. Join Source Creation</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; form field {@code csv-file-header-row-number}.</li>
     *     <li>Row number of the CSV file's header row (counting starts from 1).</li>
     * </ul>
     */
    public CreateJoinSource csvFileHeaderRowNumber(Integer csvFileHeaderRowNumber) {
        setCsvFileHeaderRowNumber(csvFileHeaderRowNumber);
        return this;
    }

    /**
     * @see #csvFileDataStartRowNumber(java.lang.Integer)
     */
    public Integer getCsvFileDataStartRowNumber() {
        return csvFileDataStartRowNumber;
    }

    /**
     * @see #csvFileDataStartRowNumber(java.lang.Integer)
     */
    public void setCsvFileDataStartRowNumber(Integer csvFileDataStartRowNumber) {
        this.csvFileDataStartRowNumber = csvFileDataStartRowNumber;
    }

    /**
     * <blockquote><q>
     *     If omitted, the data start row is expected to be at the second row of the CSV file.
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/22-026.html#_5c23e155-e422-4efe-a597-6da449a6f81b"> OGC API - Joins - Part 1: Core - 8.6. Join Source Creation</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; form field {@code csv-file-data-start-row-number}.</li>
     *     <li>Row number where data values start in the CSV file (counting starts from 1).</li>
     * </ul>
     */
    public CreateJoinSource csvFileDataStartRowNumber(Integer csvFileDataStartRowNumber) {
        setCsvFileDataStartRowNumber(csvFileDataStartRowNumber);
        return this;
    }
}
