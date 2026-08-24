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
 * Parameters for {@code POST /filejoin}, sent as a {@code multipart/form-data} request (spec
 * section 10.2.1, Table 6).
 *
 * <blockquote><q>
 *     The HTTP POST operation at path {root}/filejoin joins data from an input data file
 *     directly with another input data file.
 *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/22-026.html#_c5166ebc-28d0-4ab2-95cc-ed875abe9daf"> OGC API - Joins - Part 1: Core - 10.2. File Joining</a></small></i>
 * </blockquote>
 *
 * <p>
 * For each dataset, exactly one of the {@code *File} or {@code *Url} fields must be set; the
 * server responds with HTTP 400 if both or neither are provided for a given dataset. This class
 * does not enforce that rule client-side.</p>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class JoinFiles extends RequestParameters {

    private String leftDatasetFormat;
    private UploadFile leftDatasetFile;
    private String leftDatasetUrl;
    private String leftDatasetKey;
    private String rightDatasetFormat;
    private UploadFile rightDatasetFile;
    private String rightDatasetUrl;
    private String rightDatasetKey;
    private List<String> rightDatasetDataValueList;
    private String csvFileDelimiter;
    private Integer csvFileHeaderRowNumber;
    private Integer csvFileDataStartRowNumber;

    /**
     * @see #leftDatasetFormat(java.lang.String)
     */
    public String getLeftDatasetFormat() {
        return leftDatasetFormat;
    }

    /**
     * @see #leftDatasetFormat(java.lang.String)
     */
    public void setLeftDatasetFormat(String leftDatasetFormat) {
        this.leftDatasetFormat = leftDatasetFormat;
    }

    /**
     * <blockquote><q>
     *     The format of the left dataset.
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/22-026.html#_c5166ebc-28d0-4ab2-95cc-ed875abe9daf"> OGC API - Joins - Part 1: Core - 10.2. File Joining</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required; form field {@code left-dataset-format}.</li>
     *     <li>The core module contains support for the format
     *     {@code http://www.opengis.net/spec/ogcapi-joins-1/1.0/conf/input-geojson}.</li>
     * </ul>
     */
    public JoinFiles leftDatasetFormat(String leftDatasetFormat) {
        setLeftDatasetFormat(leftDatasetFormat);
        return this;
    }

    /**
     * @see #leftDatasetFile(org.geotoolkit.ogcapi.request.joins.UploadFile)
     */
    public UploadFile getLeftDatasetFile() {
        return leftDatasetFile;
    }

    /**
     * @see #leftDatasetFile(org.geotoolkit.ogcapi.request.joins.UploadFile)
     */
    public void setLeftDatasetFile(UploadFile leftDatasetFile) {
        this.leftDatasetFile = leftDatasetFile;
    }

    /**
     * <blockquote><q>
     *     The left dataset file (uploaded file)
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/22-026.html#_c5166ebc-28d0-4ab2-95cc-ed875abe9daf"> OGC API - Joins - Part 1: Core - 10.2. File Joining</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; form field {@code left-dataset-file}.</li>
     *     <li>One of this field or {@link #leftDatasetUrl(java.lang.String)} is mandatory; the
     *     server responds with HTTP 400 if both or neither are provided.</li>
     * </ul>
     */
    public JoinFiles leftDatasetFile(UploadFile leftDatasetFile) {
        setLeftDatasetFile(leftDatasetFile);
        return this;
    }

    /**
     * @see #leftDatasetUrl(java.lang.String)
     */
    public String getLeftDatasetUrl() {
        return leftDatasetUrl;
    }

    /**
     * @see #leftDatasetUrl(java.lang.String)
     */
    public void setLeftDatasetUrl(String leftDatasetUrl) {
        this.leftDatasetUrl = leftDatasetUrl;
    }

    /**
     * <blockquote><q>
     *     A URL link to the left dataset file
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/22-026.html#_c5166ebc-28d0-4ab2-95cc-ed875abe9daf"> OGC API - Joins - Part 1: Core - 10.2. File Joining</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; form field {@code left-dataset-url}.</li>
     *     <li>One of this field or {@link #leftDatasetFile(org.geotoolkit.ogcapi.request.joins.UploadFile)}
     *     is mandatory; the server responds with HTTP 400 if both or neither are provided.</li>
     * </ul>
     */
    public JoinFiles leftDatasetUrl(String leftDatasetUrl) {
        setLeftDatasetUrl(leftDatasetUrl);
        return this;
    }

    /**
     * @see #leftDatasetKey(java.lang.String)
     */
    public String getLeftDatasetKey() {
        return leftDatasetKey;
    }

    /**
     * @see #leftDatasetKey(java.lang.String)
     */
    public void setLeftDatasetKey(String leftDatasetKey) {
        this.leftDatasetKey = leftDatasetKey;
    }

    /**
     * <blockquote><q>
     *     The key field in the left dataset file that contains the key values.
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/22-026.html#_c5166ebc-28d0-4ab2-95cc-ed875abe9daf"> OGC API - Joins - Part 1: Core - 10.2. File Joining</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required; form field {@code left-dataset-key}.</li>
     *     <li>For GeoJSON format, this is the JSONPath string to the field containing the key
     *     values.</li>
     * </ul>
     */
    public JoinFiles leftDatasetKey(String leftDatasetKey) {
        setLeftDatasetKey(leftDatasetKey);
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
     *     The format of the right dataset.
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/22-026.html#_c5166ebc-28d0-4ab2-95cc-ed875abe9daf"> OGC API - Joins - Part 1: Core - 10.2. File Joining</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required; form field {@code right-dataset-format}.</li>
     *     <li>The core module contains support for the format
     *     {@code http://www.opengis.net/spec/ogcapi-joins-1/1.0/conf/input-csv}.</li>
     * </ul>
     */
    public JoinFiles rightDatasetFormat(String rightDatasetFormat) {
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
     *     The right dataset file (uploaded file).
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/22-026.html#_c5166ebc-28d0-4ab2-95cc-ed875abe9daf"> OGC API - Joins - Part 1: Core - 10.2. File Joining</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; form field {@code right-dataset-file}.</li>
     *     <li>One of this field or {@link #rightDatasetUrl(java.lang.String)} is mandatory; the
     *     server responds with HTTP 400 if both or neither are provided.</li>
     * </ul>
     */
    public JoinFiles rightDatasetFile(UploadFile rightDatasetFile) {
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
     *     The right dataset URL.
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/22-026.html#_c5166ebc-28d0-4ab2-95cc-ed875abe9daf"> OGC API - Joins - Part 1: Core - 10.2. File Joining</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; form field {@code right-dataset-url}.</li>
     *     <li>One of this field or {@link #rightDatasetFile(org.geotoolkit.ogcapi.request.joins.UploadFile)}
     *     is mandatory; the server responds with HTTP 400 if both or neither are provided.</li>
     * </ul>
     */
    public JoinFiles rightDatasetUrl(String rightDatasetUrl) {
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
     *     The key field in the right dataset that contains the key values.
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/22-026.html#_c5166ebc-28d0-4ab2-95cc-ed875abe9daf"> OGC API - Joins - Part 1: Core - 10.2. File Joining</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required; form field {@code right-dataset-key}.</li>
     *     <li>For CSV format, this is the column number containing the key values (counting
     *     starts from 0).</li>
     * </ul>
     */
    public JoinFiles rightDatasetKey(String rightDatasetKey) {
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
     *     Fields in the right dataset that contain the attribute values that will be joined with
     *     the left dataset.
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/22-026.html#_c5166ebc-28d0-4ab2-95cc-ed875abe9daf"> OGC API - Joins - Part 1: Core - 10.2. File Joining</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required; form field {@code right-dataset-data-value-list}, sent as a
     *     comma-separated string.</li>
     *     <li>For CSV format, the values are the column numbers to join (counting starts from 0).</li>
     * </ul>
     */
    public JoinFiles rightDatasetDataValueList(List<String> rightDatasetDataValueList) {
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
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/22-026.html#_c5166ebc-28d0-4ab2-95cc-ed875abe9daf"> OGC API - Joins - Part 1: Core - 10.2. File Joining</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; form field {@code csv-file-delimiter}.</li>
     *     <li>Mandatory when {@link #rightDatasetFormat(java.lang.String)} is the CSV input
     *     format conformance class; not required for other formats.</li>
     * </ul>
     */
    public JoinFiles csvFileDelimiter(String csvFileDelimiter) {
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
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/22-026.html#_c5166ebc-28d0-4ab2-95cc-ed875abe9daf"> OGC API - Joins - Part 1: Core - 10.2. File Joining</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; form field {@code csv-file-header-row-number}.</li>
     *     <li>Row number of the CSV file's header row (counting starts from 1).</li>
     * </ul>
     */
    public JoinFiles csvFileHeaderRowNumber(Integer csvFileHeaderRowNumber) {
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
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/22-026.html#_c5166ebc-28d0-4ab2-95cc-ed875abe9daf"> OGC API - Joins - Part 1: Core - 10.2. File Joining</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; form field {@code csv-file-data-start-row-number}.</li>
     *     <li>Row number where data values start in the CSV file (counting starts from 1).</li>
     * </ul>
     */
    public JoinFiles csvFileDataStartRowNumber(Integer csvFileDataStartRowNumber) {
        setCsvFileDataStartRowNumber(csvFileDataStartRowNumber);
        return this;
    }
}
