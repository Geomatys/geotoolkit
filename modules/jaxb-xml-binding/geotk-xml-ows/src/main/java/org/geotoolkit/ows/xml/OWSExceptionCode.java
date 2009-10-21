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

import java.util.ArrayList;
import java.util.List;
import org.opengis.util.CodeList;


/**
 * Describes the type of an exception.
 *
 * @author Guilhem Legal
 *
 * @todo Rename as {@code ExceptionCode} and move to {@link org.opengis.webservice}.
 * @module pending
 */
public class OWSExceptionCode extends CodeList<OWSExceptionCode> {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 7234996844680200818L;

    /**
     * List of all enumerations of this type.
     * Must be declared before any enum declaration.
     */
    private static final List<OWSExceptionCode> VALUES = new ArrayList<OWSExceptionCode>(16);

    /**
     * Invalid format.
     */
    public static final OWSExceptionCode INVALID_FORMAT = new OWSExceptionCode("InvalidFormat");
    
    /**
     * Invalid format.
     */
    public static final OWSExceptionCode INVALID_REQUEST = new OWSExceptionCode("InvalidRequest");

    /**
     * Current update sequence.
     */
    public static final OWSExceptionCode CURRENT_UPDATE_SEQUENCE = new OWSExceptionCode("CurrentUpdateSequence");

    /**
     * Invalid update sequence.
     */
    public static final OWSExceptionCode INVALID_UPDATE_SEQUENCE = new OWSExceptionCode("InvalidUpdateSequence");

    /**
     * Missing parameter value.
     */
    public static final OWSExceptionCode MISSING_PARAMETER_VALUE = new OWSExceptionCode("MissingParameterValue");

    /**
     * Invalid parameter value.
     */
    public static final OWSExceptionCode INVALID_PARAMETER_VALUE = new OWSExceptionCode("InvalidParameterValue");

    /**
     * Operation not supported.
     */
    public static final OWSExceptionCode OPERATION_NOT_SUPPORTED = new OWSExceptionCode("OperationNotSupported");

    /**
     * Version negotiation failed.
     */
    public static final OWSExceptionCode VERSION_NEGOTIATION_FAILED = new OWSExceptionCode("VersionNegotiationFailed");

    /**
     * No applicable code.
     */
    public static final OWSExceptionCode NO_APPLICABLE_CODE = new OWSExceptionCode("NoApplicableCode");

    /**
     * Invalid CRS.
     */
    public static final OWSExceptionCode INVALID_CRS = new OWSExceptionCode("InvalidCRS");

    /**
     * Layer not defined.
     */
    public static final OWSExceptionCode LAYER_NOT_DEFINED = new OWSExceptionCode("LayerNotDefined");

    /**
     * Style not defined.
     */
    public static final OWSExceptionCode STYLE_NOT_DEFINED = new OWSExceptionCode("StyleNotDefined");

    /**
     * Layer not queryable.
     */
    public static final OWSExceptionCode LAYER_NOT_QUERYABLE = new OWSExceptionCode("LayerNotQueryable");

    /**
     * Invalid point.
     */
    public static final OWSExceptionCode INVALID_POINT = new OWSExceptionCode("InvalidPoint");

    /**
     * Missing dimension value.
     */
    public static final OWSExceptionCode MISSING_DIMENSION_VALUE = new OWSExceptionCode("MissingDimensionValue");

    /**
     * Invalid dimension value.
     */
    public static final OWSExceptionCode INVALID_DIMENSION_VALUE = new OWSExceptionCode("InvalidDimensionValue");

    /**
     * Constructs an enum with the given name. The new enum is
     * automatically added to the list returned by {@link #values}.
     *
     * @param name The enum name. This name must not be in use by an other enum of this type.
     */
    private OWSExceptionCode(final String name) {
        super(name, VALUES);
    }

    /**
     * Returns the list of exception codes.
     */
    public static OWSExceptionCode[] values() {
        synchronized (VALUES) {
            return VALUES.toArray(new OWSExceptionCode[VALUES.size()]);
        }
    }

    /**
     * Returns the list of exception codes.
     */
    @Override
    public OWSExceptionCode[] family() {
        return values();
    }

    /**
     * Returns the exception code that matches the given string, or returns a
     * new one if none match it.
     */
    public static OWSExceptionCode valueOf(String code) {
        return valueOf(OWSExceptionCode.class, code);
    }
}

