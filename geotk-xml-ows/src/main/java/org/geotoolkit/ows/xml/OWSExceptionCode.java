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
    public static final OWSExceptionCode INVALID_FORMAT = valueOf("InvalidFormat");

    /**
     * Invalid request.
     */
    public static final OWSExceptionCode INVALID_REQUEST = valueOf("InvalidRequest");

    /**
     * Current update sequence.
     */
    public static final OWSExceptionCode CURRENT_UPDATE_SEQUENCE = valueOf("CurrentUpdateSequence");

    /**
     * Invalid update sequence.
     */
    public static final OWSExceptionCode INVALID_UPDATE_SEQUENCE = valueOf("InvalidUpdateSequence");

    /**
     * Missing parameter value.
     */
    public static final OWSExceptionCode MISSING_PARAMETER_VALUE = valueOf("MissingParameterValue");

    /**
     * Invalid parameter value.
     */
    public static final OWSExceptionCode INVALID_PARAMETER_VALUE = valueOf("InvalidParameterValue");

    /**
     * Invalid parameter value.
     */
    public static final OWSExceptionCode INVALID_VALUE = valueOf("InvalidValue");

    /**
     * Operation not supported.
     */
    public static final OWSExceptionCode OPERATION_NOT_SUPPORTED = valueOf("OperationNotSupported");

    /**
     * Version negotiation failed.
     */
    public static final OWSExceptionCode VERSION_NEGOTIATION_FAILED = valueOf("VersionNegotiationFailed");

    /**
     * No applicable code.
     */
    public static final OWSExceptionCode NO_APPLICABLE_CODE = valueOf("NoApplicableCode");

    /**
     * Invalid CRS.
     */
    public static final OWSExceptionCode INVALID_CRS = valueOf("InvalidCRS");

    /**
     * Layer not defined.
     */
    public static final OWSExceptionCode LAYER_NOT_DEFINED = valueOf("LayerNotDefined");

    /**
     * Style not defined.
     */
    public static final OWSExceptionCode STYLE_NOT_DEFINED = valueOf("StyleNotDefined");

    /**
     * Layer not queryable.
     */
    public static final OWSExceptionCode LAYER_NOT_QUERYABLE = valueOf("LayerNotQueryable");

    /**
     * Invalid point.
     */
    public static final OWSExceptionCode INVALID_POINT = valueOf("InvalidPoint");

    /**
     * Missing dimension value.
     */
    public static final OWSExceptionCode MISSING_DIMENSION_VALUE = valueOf("MissingDimensionValue");

    /**
     * Invalid dimension value.
     */
    public static final OWSExceptionCode INVALID_DIMENSION_VALUE = valueOf("InvalidDimensionValue");

    /**
     * Not Enough Storage.
     */
    public static final OWSExceptionCode NOT_ENOUGH_STORAGE = valueOf("NotEnoughStorage");

    /**
     * Server Busy.
     */
    public static final OWSExceptionCode SERVER_BUSY = valueOf("ServerBusy");

    /**
     * File Size Exceeded.
     */
    public static final OWSExceptionCode FILE_SIZE_EXCEEDED = valueOf("FileSizeExceeded");

    /**
     * Storage Not Supported.
     */
    public static final OWSExceptionCode STORAGE_NOT_SUPPORTED = valueOf("StorageNotSupported");

    /**
     * Tile out of range.
     */
    public static final OWSExceptionCode TILE_OUT_OF_RANGE = valueOf("TileOutOfRange");

    public static final OWSExceptionCode DUPLICATE_STORED_QUERY_ID_VALUE = valueOf("DuplicateStoredQueryIdValue");

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
        return values(OWSExceptionCode.class);
    }

    /**
     * Returns the exception code that matches the given string, or returns a
     * new one if none match it.
     */
    public static OWSExceptionCode valueOf(final String code) {
        return valueOf(OWSExceptionCode.class, code, OWSExceptionCode::new).get();
    }
}

