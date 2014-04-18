/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.sos.netcdf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.geotoolkit.sos.xml.SOSXmlFactory;
import org.geotoolkit.swe.xml.AbstractDataComponent;
import org.geotoolkit.swe.xml.AbstractDataRecord;
import org.geotoolkit.swe.xml.AnyScalar;
import org.geotoolkit.swe.xml.Phenomenon;
import org.geotoolkit.swe.xml.Quantity;
import org.geotoolkit.swe.xml.TextBlock;
import org.geotoolkit.swe.xml.UomProperty;
import org.geotoolkit.swe.xml.v101.CompositePhenomenonType;
import org.geotoolkit.swe.xml.v101.PhenomenonType;

/**
 *
 * @author guilhem
 */
public class OMUtils {
    
    public static final Map<String, TextBlock> TEXT_ENCODING = new HashMap<>();
    static {
        TEXT_ENCODING.put("1.0.0", SOSXmlFactory.buildTextBlock("1.0.0", "text-1", ",", "@@", "."));
        TEXT_ENCODING.put("2.0.0", SOSXmlFactory.buildTextBlock("2.0.0", "text-1", ",", "@@", "."));
    }

    public static final Map<String, AnyScalar> PRESSION_FIELD = new HashMap<>();
    static {
        final UomProperty uomv100            = SOSXmlFactory.buildUomProperty("1.0.0", "dbar", "--to be completed--");
        final AbstractDataComponent compv100 = SOSXmlFactory.buildQuantity("1.0.0", "http://mmisw.org/ont/cf/parameter/sea_water_pressure", uomv100, null);
        final AnyScalar pressionv100         = SOSXmlFactory.buildAnyScalar("1.0.0", null, "Zlevel", compv100);
        PRESSION_FIELD.put("1.0.0", pressionv100);

        final UomProperty uomv200            = SOSXmlFactory.buildUomProperty("2.0.0", "dbar", "--to be completed--");
        final AbstractDataComponent compv200 = SOSXmlFactory.buildQuantity("2.0.0", "http://mmisw.org/ont/cf/parameter/sea_water_pressure", uomv200, null);
        final AnyScalar pressionv200         = SOSXmlFactory.buildAnyScalar("2.0.0", null, "Zlevel", compv200);
        PRESSION_FIELD.put("2.0.0", pressionv200);
    }

    public static final Map<String, AnyScalar> TIME_FIELD = new HashMap<>();
    static {
        final UomProperty uomv100            = SOSXmlFactory.buildUomProperty("1.0.0", "gregorian", "http://www.opengis.net/def/uom/ISO-8601/0/Gregorian");
        final AbstractDataComponent compv100 = SOSXmlFactory.buildTime("1.0.0", "http://www.opengis.net/def/property/OGC/0/SamplingTime", uomv100);
        final AnyScalar timev100             = SOSXmlFactory.buildAnyScalar("1.0.0", null, "time", compv100);
        TIME_FIELD.put("1.0.0", timev100);

        final UomProperty uomv200            = SOSXmlFactory.buildUomProperty("2.0.0", "gregorian", "http://www.opengis.net/def/uom/ISO-8601/0/Gregorian");
        final AbstractDataComponent compv200 = SOSXmlFactory.buildTime("2.0.0", "http://www.opengis.net/def/property/OGC/0/SamplingTime", uomv200);
        final AnyScalar timev200             = SOSXmlFactory.buildAnyScalar("2.0.0", null, "time", compv200);
        TIME_FIELD.put("2.0.0", timev200);
    }

    public static final Map<String, AnyScalar> LATITUDE_FIELD = new HashMap<>();
    static {
        final UomProperty uomv100            = SOSXmlFactory.buildUomProperty("1.0.0", "deg", null);
        final AbstractDataComponent compv100 = SOSXmlFactory.buildQuantity("1.0.0", "http://mmisw.org/ont/cf/parameter/latitude", uomv100, null);
        final AnyScalar latv100              = SOSXmlFactory.buildAnyScalar("1.0.0", null, "lat", compv100);
        LATITUDE_FIELD.put("1.0.0", latv100);

        final UomProperty uomv200            = SOSXmlFactory.buildUomProperty("2.0.0", "deg", null);
        final AbstractDataComponent compv200 = SOSXmlFactory.buildQuantity("2.0.0", "http://mmisw.org/ont/cf/parameter/latitude", uomv200, null);
        final AnyScalar latv200              = SOSXmlFactory.buildAnyScalar("2.0.0", null, "lat", compv200);
        LATITUDE_FIELD.put("2.0.0", latv200);
    }

    public static final Map<String, AnyScalar> LONGITUDE_FIELD = new HashMap<>();
    static {
        final UomProperty uomv100            = SOSXmlFactory.buildUomProperty("1.0.0", "deg", null);
        final AbstractDataComponent compv100 = SOSXmlFactory.buildQuantity("1.0.0", "http://mmisw.org/ont/cf/parameter/longitude", uomv100, null);
        final AnyScalar lonv100              = SOSXmlFactory.buildAnyScalar("1.0.0", null, "lon", compv100);
        LONGITUDE_FIELD.put("1.0.0", lonv100);

        final UomProperty uomv200            = SOSXmlFactory.buildUomProperty("2.0.0", "deg", null);
        final AbstractDataComponent compv200 = SOSXmlFactory.buildQuantity("2.0.0", "http://mmisw.org/ont/cf/parameter/longitude", uomv200, null);
        final AnyScalar lonv200              = SOSXmlFactory.buildAnyScalar("2.0.0", null, "lon", compv200);
        LONGITUDE_FIELD.put("2.0.0", lonv200);
    }
    
    public static AbstractDataRecord getDataRecordProfile(final String version, final List<Field> phenomenons) {
        
        final List<AnyScalar> fields = new ArrayList<>();
        fields.add(PRESSION_FIELD.get(version));
        for (Field phenomenon : phenomenons) {
            final UomProperty uom = SOSXmlFactory.buildUomProperty(version, phenomenon.unit, null);
            final Quantity cat    = SOSXmlFactory.buildQuantity(version, phenomenon.label, uom, null);
            fields.add(SOSXmlFactory.buildAnyScalar(version, null, phenomenon.label, cat));
        }
        return SOSXmlFactory.buildSimpleDatarecord(version, null, null, null, true, fields);
    }

    public static AbstractDataRecord getDataRecordTimeSeries(final String version, final List<Field> phenomenons) {
        final List<AnyScalar> fields = new ArrayList<>();
        fields.add(TIME_FIELD.get(version));
        for (Field phenomenon : phenomenons) {
            final UomProperty uom = SOSXmlFactory.buildUomProperty(version, phenomenon.unit, null);
            final Quantity cat    = SOSXmlFactory.buildQuantity(version, phenomenon.label, uom, null);
            fields.add(SOSXmlFactory.buildAnyScalar(version, null, phenomenon.label, cat));
        }
        return SOSXmlFactory.buildSimpleDatarecord(version, null, null, null, true, fields);
    }

    public static AbstractDataRecord getDataRecordTrajectory(final String version, final List<Field> phenomenons) {
        final List<AnyScalar> fields = new ArrayList<>();
        fields.add(TIME_FIELD.get(version));
        fields.add(LATITUDE_FIELD.get(version));
        fields.add(LONGITUDE_FIELD.get(version));
        for (Field phenomenon : phenomenons) {
            final UomProperty uom = SOSXmlFactory.buildUomProperty(version, phenomenon.unit, null);
            final Quantity cat    = SOSXmlFactory.buildQuantity(version, phenomenon.label, uom, null);
            fields.add(SOSXmlFactory.buildAnyScalar(version, null, phenomenon.label, cat));
        }
        return SOSXmlFactory.buildSimpleDatarecord(version, null, null, null, true, fields);
    }
    
     public static Phenomenon getPhenomenon(final String version, final List<Field> phenomenons) {
        final Phenomenon phenomenon;
        if (phenomenons.size() == 1) {
            phenomenon = SOSXmlFactory.buildPhenomenon(version, phenomenons.get(0).label, phenomenons.get(0).label);
        } else {
            final Set<PhenomenonType> types = new HashSet<>();
            for (Field phen : phenomenons) {
                types.add(new PhenomenonType(phen.label, phen.label));
            }
            phenomenon = new CompositePhenomenonType("composite", "composite", null, null, types);
        }
        return phenomenon;
    }
}
