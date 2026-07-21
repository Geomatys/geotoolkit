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
     * Form field {@code left-dataset-format}. The format of the left dataset, e.g.
     * {@code http://www.opengis.net/spec/ogcapi-joins-1/1.0/conf/input-geojson}. (required)
     */
    public String getLeftDatasetFormat() {
        return leftDatasetFormat;
    }

    /**
     * @see #getLeftDatasetFormat()
     */
    public void setLeftDatasetFormat(String leftDatasetFormat) {
        this.leftDatasetFormat = leftDatasetFormat;
    }

    /**
     * @see #getLeftDatasetFormat()
     */
    public JoinFiles leftDatasetFormat(String leftDatasetFormat) {
        setLeftDatasetFormat(leftDatasetFormat);
        return this;
    }

    /**
     * Form field {@code left-dataset-file}. The left dataset file, uploaded to the server. One of
     * this field or {@link #getLeftDatasetUrl()} is mandatory. (optional)
     */
    public UploadFile getLeftDatasetFile() {
        return leftDatasetFile;
    }

    /**
     * @see #getLeftDatasetFile()
     */
    public void setLeftDatasetFile(UploadFile leftDatasetFile) {
        this.leftDatasetFile = leftDatasetFile;
    }

    /**
     * @see #getLeftDatasetFile()
     */
    public JoinFiles leftDatasetFile(UploadFile leftDatasetFile) {
        setLeftDatasetFile(leftDatasetFile);
        return this;
    }

    /**
     * Form field {@code left-dataset-url}. A URL link to the left dataset file. One of this field
     * or {@link #getLeftDatasetFile()} is mandatory. (optional)
     */
    public String getLeftDatasetUrl() {
        return leftDatasetUrl;
    }

    /**
     * @see #getLeftDatasetUrl()
     */
    public void setLeftDatasetUrl(String leftDatasetUrl) {
        this.leftDatasetUrl = leftDatasetUrl;
    }

    /**
     * @see #getLeftDatasetUrl()
     */
    public JoinFiles leftDatasetUrl(String leftDatasetUrl) {
        setLeftDatasetUrl(leftDatasetUrl);
        return this;
    }

    /**
     * Form field {@code left-dataset-key}. The key field in the left dataset that contains the key
     * values. For GeoJSON format, this is the JSONPath string to the field containing the key
     * values. (required)
     */
    public String getLeftDatasetKey() {
        return leftDatasetKey;
    }

    /**
     * @see #getLeftDatasetKey()
     */
    public void setLeftDatasetKey(String leftDatasetKey) {
        this.leftDatasetKey = leftDatasetKey;
    }

    /**
     * @see #getLeftDatasetKey()
     */
    public JoinFiles leftDatasetKey(String leftDatasetKey) {
        setLeftDatasetKey(leftDatasetKey);
        return this;
    }

    /**
     * Form field {@code right-dataset-format}. The format of the right dataset, e.g.
     * {@code http://www.opengis.net/spec/ogcapi-joins-1/1.0/conf/input-csv}. (required)
     */
    public String getRightDatasetFormat() {
        return rightDatasetFormat;
    }

    /**
     * @see #getRightDatasetFormat()
     */
    public void setRightDatasetFormat(String rightDatasetFormat) {
        this.rightDatasetFormat = rightDatasetFormat;
    }

    /**
     * @see #getRightDatasetFormat()
     */
    public JoinFiles rightDatasetFormat(String rightDatasetFormat) {
        setRightDatasetFormat(rightDatasetFormat);
        return this;
    }

    /**
     * Form field {@code right-dataset-file}. The right dataset file, uploaded to the server. One
     * of this field or {@link #getRightDatasetUrl()} is mandatory. (optional)
     */
    public UploadFile getRightDatasetFile() {
        return rightDatasetFile;
    }

    /**
     * @see #getRightDatasetFile()
     */
    public void setRightDatasetFile(UploadFile rightDatasetFile) {
        this.rightDatasetFile = rightDatasetFile;
    }

    /**
     * @see #getRightDatasetFile()
     */
    public JoinFiles rightDatasetFile(UploadFile rightDatasetFile) {
        setRightDatasetFile(rightDatasetFile);
        return this;
    }

    /**
     * Form field {@code right-dataset-url}. A URL link to the right dataset file. One of this
     * field or {@link #getRightDatasetFile()} is mandatory. (optional)
     */
    public String getRightDatasetUrl() {
        return rightDatasetUrl;
    }

    /**
     * @see #getRightDatasetUrl()
     */
    public void setRightDatasetUrl(String rightDatasetUrl) {
        this.rightDatasetUrl = rightDatasetUrl;
    }

    /**
     * @see #getRightDatasetUrl()
     */
    public JoinFiles rightDatasetUrl(String rightDatasetUrl) {
        setRightDatasetUrl(rightDatasetUrl);
        return this;
    }

    /**
     * Form field {@code right-dataset-key}. The key field in the right dataset that contains the
     * key values. For CSV format, this is the column number containing the key values (counting
     * starts from 0). (required)
     */
    public String getRightDatasetKey() {
        return rightDatasetKey;
    }

    /**
     * @see #getRightDatasetKey()
     */
    public void setRightDatasetKey(String rightDatasetKey) {
        this.rightDatasetKey = rightDatasetKey;
    }

    /**
     * @see #getRightDatasetKey()
     */
    public JoinFiles rightDatasetKey(String rightDatasetKey) {
        setRightDatasetKey(rightDatasetKey);
        return this;
    }

    /**
     * Form field {@code right-dataset-data-value-list}. Fields in the right dataset that contain
     * the attribute values to be joined with the left dataset. For CSV format, the values are the
     * column numbers to join (counting starts from 0). Sent as a comma-separated string.
     * (required)
     */
    public List<String> getRightDatasetDataValueList() {
        return rightDatasetDataValueList;
    }

    /**
     * @see #getRightDatasetDataValueList()
     */
    public void setRightDatasetDataValueList(List<String> rightDatasetDataValueList) {
        this.rightDatasetDataValueList = rightDatasetDataValueList;
    }

    /**
     * @see #getRightDatasetDataValueList()
     */
    public JoinFiles rightDatasetDataValueList(List<String> rightDatasetDataValueList) {
        setRightDatasetDataValueList(rightDatasetDataValueList);
        return this;
    }

    /**
     * Form field {@code csv-file-delimiter}. The delimiter character used in the CSV file.
     * Mandatory when {@link #getRightDatasetFormat()} is the CSV input format conformance class.
     * (optional)
     */
    public String getCsvFileDelimiter() {
        return csvFileDelimiter;
    }

    /**
     * @see #getCsvFileDelimiter()
     */
    public void setCsvFileDelimiter(String csvFileDelimiter) {
        this.csvFileDelimiter = csvFileDelimiter;
    }

    /**
     * @see #getCsvFileDelimiter()
     */
    public JoinFiles csvFileDelimiter(String csvFileDelimiter) {
        setCsvFileDelimiter(csvFileDelimiter);
        return this;
    }

    /**
     * Form field {@code csv-file-header-row-number}. The row number of the CSV file's header row
     * (counting starts from 1). If omitted, the header row is expected at the first row. (optional)
     */
    public Integer getCsvFileHeaderRowNumber() {
        return csvFileHeaderRowNumber;
    }

    /**
     * @see #getCsvFileHeaderRowNumber()
     */
    public void setCsvFileHeaderRowNumber(Integer csvFileHeaderRowNumber) {
        this.csvFileHeaderRowNumber = csvFileHeaderRowNumber;
    }

    /**
     * @see #getCsvFileHeaderRowNumber()
     */
    public JoinFiles csvFileHeaderRowNumber(Integer csvFileHeaderRowNumber) {
        setCsvFileHeaderRowNumber(csvFileHeaderRowNumber);
        return this;
    }

    /**
     * Form field {@code csv-file-data-start-row-number}. The row number where data values start in
     * the CSV file (counting starts from 1). If omitted, the data start row is expected at the
     * second row. (optional)
     */
    public Integer getCsvFileDataStartRowNumber() {
        return csvFileDataStartRowNumber;
    }

    /**
     * @see #getCsvFileDataStartRowNumber()
     */
    public void setCsvFileDataStartRowNumber(Integer csvFileDataStartRowNumber) {
        this.csvFileDataStartRowNumber = csvFileDataStartRowNumber;
    }

    /**
     * @see #getCsvFileDataStartRowNumber()
     */
    public JoinFiles csvFileDataStartRowNumber(Integer csvFileDataStartRowNumber) {
        setCsvFileDataStartRowNumber(csvFileDataStartRowNumber);
        return this;
    }
}
