/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2021, Geomatys
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
package org.geotoolkit.observation;

import org.geotoolkit.observation.model.ResultMode;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class ObservationFilterFlags {

    /**
     * Used for Observation template (datastream) retrieval.
     * if set to false, the feature of interest will not be included in the template.
     */
    public static final String INCLUDE_FOI_IN_TEMPLATE = "includeFoiInTemplate";

    /**
     * Used for Observation template (datastream) retrieval.
     * if set to false, the sampling time will not be included in the template.
     */
    public static final String INCLUDE_TIME_IN_TEMPLATE = "includeTimeInTemplate";

    /**
     * Used for Complex observations retrieval.
     * If set to true, the measure identifier will be added in the datablock for each measure.
     */
    public static final String INCLUDE_ID_IN_DATABLOCK = "includeIDInDataBlock";

    /**
     * Used for Phenomenon retrieval.
     * If set to true, the composite phenomenons will be decomposed and their components will be returned.
     */
    public static final String NO_COMPOSITE_PHENOMENON = "noCompositePhenomenon";

    /**
     *  Used for Complex observations retrieval.
     * Specify the result extraction {@link ResultMode}
     */
    public static final String RESULT_MODE = "resultMode";

    /**
     * Used for Complex observations retrieval.
     * If set to true, a new observation will be created for each measure.
     */
    public static final String SEPARATED_OBSERVATION = "separatedObservation";

    /**
     * Used in result retrieval.
     * Specify a number a values for decimation purpose.
     */
    public static final String DECIMATION_SIZE = "decimSize";

    /**
     * The SOS version.
     */
    public static final String VERSION = "version";

    /**
     * The page size.
     */
    public static final String PAGE_LIMIT = "limit";

    /**
     * The page offset.
     */
    public static final String PAGE_OFFSET = "offset";
}
