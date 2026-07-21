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
     * Identifier of the collection to create the join source for. (required)
     */
    public String getCollectionId() {
        return collectionId;
    }

    /**
     * @see #getCollectionId()
     */
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    /**
     * @see #getCollectionId()
     */
    public CreateJoinSource collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * Form field {@code collection-key}. The value of the {@code id} attribute of the collection's
     * key field to use in the join operation. If not provided, the collection's default key field
     * is used. (optional)
     */
    public String getCollectionKey() {
        return collectionKey;
    }

    /**
     * @see #getCollectionKey()
     */
    public void setCollectionKey(String collectionKey) {
        this.collectionKey = collectionKey;
    }

    /**
     * @see #getCollectionKey()
     */
    public CreateJoinSource collectionKey(String collectionKey) {
        setCollectionKey(collectionKey);
        return this;
    }

    /**
     * Form field {@code right-dataset-format}. The format of the attribute dataset, e.g.
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
    public CreateJoinSource rightDatasetFormat(String rightDatasetFormat) {
        setRightDatasetFormat(rightDatasetFormat);
        return this;
    }

    /**
     * Form field {@code right-dataset-file}. The attribute dataset file, uploaded to the server.
     * One of this field or {@link #getRightDatasetUrl()} is mandatory. (optional)
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
    public CreateJoinSource rightDatasetFile(UploadFile rightDatasetFile) {
        setRightDatasetFile(rightDatasetFile);
        return this;
    }

    /**
     * Form field {@code right-dataset-url}. A URL link to the attribute dataset file. One of this
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
    public CreateJoinSource rightDatasetUrl(String rightDatasetUrl) {
        setRightDatasetUrl(rightDatasetUrl);
        return this;
    }

    /**
     * Form field {@code right-dataset-key}. The key field in the attribute dataset that contains
     * the key values. For CSV format, this is the column number containing the key values
     * (counting starts from 0). (required)
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
    public CreateJoinSource rightDatasetKey(String rightDatasetKey) {
        setRightDatasetKey(rightDatasetKey);
        return this;
    }

    /**
     * Form field {@code right-dataset-data-value-list}. Fields in the attribute dataset that
     * contain the attribute values to be joined. For CSV format, the values are the column numbers
     * to join (counting starts from 0). Sent as a comma-separated string. (required)
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
    public CreateJoinSource rightDatasetDataValueList(List<String> rightDatasetDataValueList) {
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
    public CreateJoinSource csvFileDelimiter(String csvFileDelimiter) {
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
    public CreateJoinSource csvFileHeaderRowNumber(Integer csvFileHeaderRowNumber) {
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
    public CreateJoinSource csvFileDataStartRowNumber(Integer csvFileDataStartRowNumber) {
        setCsvFileDataStartRowNumber(csvFileDataStartRowNumber);
        return this;
    }
}
