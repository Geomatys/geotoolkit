/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.ows.xml;

import java.util.List;
import org.opengis.util.CodeList;


/**
 * Describes the type of an exception.
 *
 * @author Guilhem Legal
 *
 * @todo Rename as {@code ExceptionCode} and move to {@link org.opengis.webservice}.
 * @module
 */
public class OWSExceptionCode extends CodeList<OWSExceptionCode> {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 7234996844680200818L;

    /**
     * Invalid format.
     */
    public static final OWSExceptionCode INVALID_FORMAT;

    /**
     * Invalid request.
     */
    public static final OWSExceptionCode INVALID_REQUEST;

    /**
     * Current update sequence.
     */
    public static final OWSExceptionCode CURRENT_UPDATE_SEQUENCE;

    /**
     * Invalid update sequence.
     */
    public static final OWSExceptionCode INVALID_UPDATE_SEQUENCE;

    /**
     * Missing parameter value.
     */
    public static final OWSExceptionCode MISSING_PARAMETER_VALUE;

    /**
     * Invalid parameter value.
     */
    public static final OWSExceptionCode INVALID_PARAMETER_VALUE;

    /**
     * Invalid parameter value.
     */
    public static final OWSExceptionCode INVALID_VALUE;

    /**
     * Operation not supported.
     */
    public static final OWSExceptionCode OPERATION_NOT_SUPPORTED;

    /**
     * Version negotiation failed.
     */
    public static final OWSExceptionCode VERSION_NEGOTIATION_FAILED;

    /**
     * No applicable code.
     */
    public static final OWSExceptionCode NO_APPLICABLE_CODE;

    /**
     * Invalid CRS.
     */
    public static final OWSExceptionCode INVALID_CRS;

    /**
     * Layer not defined.
     */
    public static final OWSExceptionCode LAYER_NOT_DEFINED;

    /**
     * Style not defined.
     */
    public static final OWSExceptionCode STYLE_NOT_DEFINED;

    /**
     * Layer not queryable.
     */
    public static final OWSExceptionCode LAYER_NOT_QUERYABLE;

    /**
     * Invalid point.
     */
    public static final OWSExceptionCode INVALID_POINT;

    /**
     * Missing dimension value.
     */
    public static final OWSExceptionCode MISSING_DIMENSION_VALUE;

    /**
     * Invalid dimension value.
     */
    public static final OWSExceptionCode INVALID_DIMENSION_VALUE;

    /**
     * Not Enough Storage.
     */
    public static final OWSExceptionCode NOT_ENOUGH_STORAGE;

    /**
     * Server Busy.
     */
    public static final OWSExceptionCode SERVER_BUSY;

    /**
     * File Size Exceeded.
     */
    public static final OWSExceptionCode FILE_SIZE_EXCEEDED;

    /**
     * Storage Not Supported.
     */
    public static final OWSExceptionCode STORAGE_NOT_SUPPORTED;

    /**
     * Tile out of range.
     */
    public static final OWSExceptionCode TILE_OUT_OF_RANGE;

    public static final OWSExceptionCode DUPLICATE_STORED_QUERY_ID_VALUE;

    /**
     * All code list values created in the currently running <abbr>JVM</abbr>.
     */
    private static final List<OWSExceptionCode> VALUES = initialValues(
        // Inline assignments for getting compiler error if a field is missing or duplicated.
        INVALID_FORMAT                  = new OWSExceptionCode("InvalidFormat"),
        INVALID_REQUEST                 = new OWSExceptionCode("InvalidRequest"),
        CURRENT_UPDATE_SEQUENCE         = new OWSExceptionCode("CurrentUpdateSequence"),
        INVALID_UPDATE_SEQUENCE         = new OWSExceptionCode("InvalidUpdateSequence"),
        MISSING_PARAMETER_VALUE         = new OWSExceptionCode("MissingParameterValue"),
        INVALID_PARAMETER_VALUE         = new OWSExceptionCode("InvalidParameterValue"),
        INVALID_VALUE                   = new OWSExceptionCode("InvalidValue"),
        OPERATION_NOT_SUPPORTED         = new OWSExceptionCode("OperationNotSupported"),
        VERSION_NEGOTIATION_FAILED      = new OWSExceptionCode("VersionNegotiationFailed"),
        NO_APPLICABLE_CODE              = new OWSExceptionCode("NoApplicableCode"),
        INVALID_CRS                     = new OWSExceptionCode("InvalidCRS"),
        LAYER_NOT_DEFINED               = new OWSExceptionCode("LayerNotDefined"),
        STYLE_NOT_DEFINED               = new OWSExceptionCode("StyleNotDefined"),
        LAYER_NOT_QUERYABLE             = new OWSExceptionCode("LayerNotQueryable"),
        INVALID_POINT                   = new OWSExceptionCode("InvalidPoint"),
        MISSING_DIMENSION_VALUE         = new OWSExceptionCode("MissingDimensionValue"),
        INVALID_DIMENSION_VALUE         = new OWSExceptionCode("InvalidDimensionValue"),
        NOT_ENOUGH_STORAGE              = new OWSExceptionCode("NotEnoughStorage"),
        SERVER_BUSY                     = new OWSExceptionCode("ServerBusy"),
        FILE_SIZE_EXCEEDED              = new OWSExceptionCode("FileSizeExceeded"),
        STORAGE_NOT_SUPPORTED           = new OWSExceptionCode("StorageNotSupported"),
        TILE_OUT_OF_RANGE               = new OWSExceptionCode("TileOutOfRange"),
        DUPLICATE_STORED_QUERY_ID_VALUE = new OWSExceptionCode("DuplicateStoredQueryIdValue"));

    /**
     * Constructs an enum with the given name.
     *
     * @param name The enum name. This name must not be in use by an other enum of this type.
     */
    private OWSExceptionCode(final String name) {
        super(name);
    }

    /**
     * Returns the list of exception codes.
     */
    @Override
    public OWSExceptionCode[] family() {
        return VALUES.toArray(OWSExceptionCode[]::new);
    }

    /**
     * Returns the exception code that matches the given string, or returns a
     * new one if none match it.
     */
    public static OWSExceptionCode valueOf(final String code) {
        return valueOf(VALUES, code, OWSExceptionCode::new);
    }

    public static OWSExceptionCode valueIfExists(final String code) {
        return valueOf(VALUES, code, null);
    }
}

