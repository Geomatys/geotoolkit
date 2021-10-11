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
package org.geotoolkit.observation;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.gml.xml.AbstractFeature;
import org.geotoolkit.gml.xml.BoundingShape;
import org.geotoolkit.gml.xml.Envelope;
import org.geotoolkit.gml.xml.FeatureProperty;
import org.geotoolkit.gml.xml.LineString;
import org.geotoolkit.gml.xml.Point;
import org.geotoolkit.observation.model.Field;
import org.geotoolkit.observation.model.OMEntity;
import org.geotoolkit.observation.xml.AbstractObservation;
import org.geotoolkit.observation.xml.OMXmlFactory;
import static org.geotoolkit.ows.xml.OWSExceptionCode.INVALID_PARAMETER_VALUE;
import static org.geotoolkit.ows.xml.OWSExceptionCode.MISSING_PARAMETER_VALUE;
import org.geotoolkit.sampling.xml.SamplingFeature;
import org.geotoolkit.sml.xml.AbstractIdentification;
import org.geotoolkit.sml.xml.AbstractIdentifier;
import org.geotoolkit.sml.xml.AbstractProcess;
import org.geotoolkit.sml.xml.AbstractSensorML;
import org.geotoolkit.sos.MeasureStringBuilder;
import org.geotoolkit.sos.xml.SOSXmlFactory;
import org.geotoolkit.swe.xml.AbstractDataComponent;
import org.geotoolkit.swe.xml.AbstractDataRecord;
import org.geotoolkit.swe.xml.AnyScalar;
import org.geotoolkit.swe.xml.DataArrayProperty;
import org.geotoolkit.swe.xml.Phenomenon;
import org.geotoolkit.swe.xml.Quantity;
import org.geotoolkit.swe.xml.TextBlock;
import org.geotoolkit.swe.xml.UomProperty;
import org.geotoolkit.swe.xml.v101.CompositePhenomenonType;
import org.geotoolkit.swe.xml.v101.PhenomenonType;
import static org.geotoolkit.swe.xml.v200.TextEncodingType.DEFAULT_ENCODING;
import org.geotoolkit.temporal.object.ISODateParser;
import org.opengis.geometry.DirectPosition;
import org.opengis.observation.CompositePhenomenon;
import org.opengis.observation.Observation;
import org.opengis.temporal.TemporalGeometricPrimitive;
import org.opengis.observation.Process;

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
        final UomProperty uomv100 = SOSXmlFactory.buildUomProperty("1.0.0", "dbar", "--to be completed--");
        final AbstractDataComponent compv100 = SOSXmlFactory.buildQuantity("1.0.0", "http://mmisw.org/ont/cf/parameter/sea_water_pressure", uomv100, null);
        final AnyScalar pressionv100 = SOSXmlFactory.buildAnyScalar("1.0.0", null, "Zlevel", compv100);
        PRESSION_FIELD.put("1.0.0", pressionv100);

        final UomProperty uomv200 = SOSXmlFactory.buildUomProperty("2.0.0", "dbar", "--to be completed--");
        final AbstractDataComponent compv200 = SOSXmlFactory.buildQuantity("2.0.0", "http://mmisw.org/ont/cf/parameter/sea_water_pressure", uomv200, null);
        final AnyScalar pressionv200 = SOSXmlFactory.buildAnyScalar("2.0.0", null, "Zlevel", compv200);
        PRESSION_FIELD.put("2.0.0", pressionv200);
    }

    public static final Map<String, AnyScalar> TIME_FIELD = new HashMap<>();

    static {
        final UomProperty uomv100 = SOSXmlFactory.buildUomProperty("1.0.0", "gregorian", "http://www.opengis.net/def/uom/ISO-8601/0/Gregorian");
        final AbstractDataComponent compv100 = SOSXmlFactory.buildTime("1.0.0", "http://www.opengis.net/def/property/OGC/0/SamplingTime", uomv100);
        final AnyScalar timev100 = SOSXmlFactory.buildAnyScalar("1.0.0", null, "time", compv100);
        TIME_FIELD.put("1.0.0", timev100);

        final UomProperty uomv200 = SOSXmlFactory.buildUomProperty("2.0.0", "gregorian", "http://www.opengis.net/def/uom/ISO-8601/0/Gregorian");
        final AbstractDataComponent compv200 = SOSXmlFactory.buildTime("2.0.0", "http://www.opengis.net/def/property/OGC/0/SamplingTime", uomv200);
        final AnyScalar timev200 = SOSXmlFactory.buildAnyScalar("2.0.0", null, "time", compv200);
        TIME_FIELD.put("2.0.0", timev200);
    }

    public static final Map<String, AnyScalar> LATITUDE_FIELD = new HashMap<>();

    static {
        final UomProperty uomv100 = SOSXmlFactory.buildUomProperty("1.0.0", "deg", null);
        final AbstractDataComponent compv100 = SOSXmlFactory.buildQuantity("1.0.0", "http://mmisw.org/ont/cf/parameter/latitude", uomv100, null);
        final AnyScalar latv100 = SOSXmlFactory.buildAnyScalar("1.0.0", null, "lat", compv100);
        LATITUDE_FIELD.put("1.0.0", latv100);

        final UomProperty uomv200 = SOSXmlFactory.buildUomProperty("2.0.0", "deg", null);
        final AbstractDataComponent compv200 = SOSXmlFactory.buildQuantity("2.0.0", "http://mmisw.org/ont/cf/parameter/latitude", uomv200, null);
        final AnyScalar latv200 = SOSXmlFactory.buildAnyScalar("2.0.0", null, "lat", compv200);
        LATITUDE_FIELD.put("2.0.0", latv200);
    }

    public static final Map<String, AnyScalar> LONGITUDE_FIELD = new HashMap<>();

    static {
        final UomProperty uomv100 = SOSXmlFactory.buildUomProperty("1.0.0", "deg", null);
        final AbstractDataComponent compv100 = SOSXmlFactory.buildQuantity("1.0.0", "http://mmisw.org/ont/cf/parameter/longitude", uomv100, null);
        final AnyScalar lonv100 = SOSXmlFactory.buildAnyScalar("1.0.0", null, "lon", compv100);
        LONGITUDE_FIELD.put("1.0.0", lonv100);

        final UomProperty uomv200 = SOSXmlFactory.buildUomProperty("2.0.0", "deg", null);
        final AbstractDataComponent compv200 = SOSXmlFactory.buildQuantity("2.0.0", "http://mmisw.org/ont/cf/parameter/longitude", uomv200, null);
        final AnyScalar lonv200 = SOSXmlFactory.buildAnyScalar("2.0.0", null, "lon", compv200);
        LONGITUDE_FIELD.put("2.0.0", lonv200);
    }

    public static AbstractDataRecord getDataRecordProfile(final String version, final List<? extends Field> phenomenons) {

        final List<AnyScalar> fields = new ArrayList<>();
        fields.add(PRESSION_FIELD.get(version));
        for (Field phenomenon : phenomenons) {
            final UomProperty uom = SOSXmlFactory.buildUomProperty(version, phenomenon.uom, null);
            final Quantity cat = SOSXmlFactory.buildQuantity(version, phenomenon.name, uom, null);
            fields.add(SOSXmlFactory.buildAnyScalar(version, null, phenomenon.name, cat));
        }
        return SOSXmlFactory.buildSimpleDatarecord(version, null, null, null, true, fields);
    }

    public static AbstractDataRecord getDataRecordTimeSeries(final String version, final List<? extends Field> phenomenons) {
        final List<AnyScalar> fields = new ArrayList<>();
        fields.add(TIME_FIELD.get(version));
        for (Field phenomenon : phenomenons) {
            final UomProperty uom = SOSXmlFactory.buildUomProperty(version, phenomenon.uom, null);
            final Quantity cat = SOSXmlFactory.buildQuantity(version, phenomenon.name, uom, null);
            fields.add(SOSXmlFactory.buildAnyScalar(version, null, phenomenon.name, cat));
        }
        return SOSXmlFactory.buildSimpleDatarecord(version, null, null, null, true, fields);
    }

    public static AbstractDataRecord getDataRecordTrajectory(final String version, final List<? extends Field> phenomenons) {
        final List<AnyScalar> fields = new ArrayList<>();
        fields.add(TIME_FIELD.get(version));
        fields.add(LATITUDE_FIELD.get(version));
        fields.add(LONGITUDE_FIELD.get(version));
        for (Field phenomenon : phenomenons) {
            final UomProperty uom = SOSXmlFactory.buildUomProperty(version, phenomenon.uom, null);
            final Quantity cat = SOSXmlFactory.buildQuantity(version, phenomenon.name, uom, null);
            fields.add(SOSXmlFactory.buildAnyScalar(version, null, phenomenon.name, cat));
        }
        return SOSXmlFactory.buildSimpleDatarecord(version, null, null, null, true, fields);
    }

    public static Phenomenon getPhenomenon(final String version, final List<? extends Field> phenomenons) {
        return getPhenomenon(version, phenomenons, "urn:ogc:phenomenon:", new HashSet<>());
    }

    public static Phenomenon getPhenomenon(final String version, final List<? extends Field> phenomenons, final Set<org.opengis.observation.Phenomenon> existingPhens) {
        return getPhenomenon(version, phenomenons, "urn:ogc:phenomenon:", existingPhens);
    }

    public static Phenomenon getPhenomenon(final String version, final List<? extends Field> phenomenons, final String phenomenonIdBase, final Set<org.opengis.observation.Phenomenon> existingPhens) {
        return getPhenomenon(version, null, phenomenons, phenomenonIdBase, existingPhens);
    }

    public static Phenomenon getPhenomenon(final String version, String name, final List<? extends Field> phenomenons, final String phenomenonIdBase, final Set<org.opengis.observation.Phenomenon> existingPhens) {
        final Phenomenon phenomenon;
        if (phenomenons.size() == 1) {
            phenomenon = SOSXmlFactory.buildPhenomenon(version, phenomenons.get(0).name, phenomenons.get(0).label, phenomenons.get(0).name, phenomenons.get(0).description);
        } else {
            final Set<PhenomenonType> types = new LinkedHashSet<>();
            for (Field phen : phenomenons) {
                types.add(new PhenomenonType(phen.name, phen.label, phen.name, phen.description));
            }

            // look for an already existing (composite) phenomenon to use instead of creating a new one
            for (org.opengis.observation.Phenomenon existingPhen : existingPhens) {
                if (existingPhen instanceof CompositePhenomenon) {
                    CompositePhenomenon cphen = (CompositePhenomenon) existingPhen;
                    if (componentsEquals(cphen.getComponent(), types)) {
                        return (Phenomenon) cphen;
                    }
                }
            }

            final String compositeId = "composite" + UUID.randomUUID().toString();
            final String definition = phenomenonIdBase + compositeId;
            if (name == null) {
                name = definition;
            }
            phenomenon = new CompositePhenomenonType(compositeId, name, definition, null, null, types);
        }
        return phenomenon;
    }

    private static boolean componentsEquals(Collection as, Collection bs) {
        if (as.size() == bs.size()) {
            Iterator i1 = as.iterator();
            Iterator i2 = bs.iterator();
            while (i1.hasNext()) {
                if (!Objects.equals(i1.next(), i2.next())) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static SamplingFeature buildSamplingPoint(final String identifier, final double latitude, final double longitude) {
        final DirectPosition position = SOSXmlFactory.buildDirectPosition("2.0.0", "EPSG:4326", 2, Arrays.asList(latitude, longitude));
        final Point geom              = SOSXmlFactory.buildPoint("2.0.0", "SamplingPoint", position);
        geom.setSrsName("EPSG:4326");
        final SamplingFeature sp      = SOSXmlFactory.buildSamplingPoint("2.0.0", identifier, null, null, null, geom);
        return sp;
    }

    public static SamplingFeature buildSamplingCurve(final String identifier, final List<DirectPosition> positions) {
        final LineString geom         = SOSXmlFactory.buildLineString("2.0.0", null, "EPSG:4326", positions);
        final SamplingFeature sp      = SOSXmlFactory.buildSamplingCurve("2.0.0", identifier, null, null, null, geom, null, null, null);
        return sp;
    }

    public static Process buildProcess(final String procedureId) {
        return SOSXmlFactory.buildProcess("2.0.0", procedureId);
    }

    public static AbstractObservation buildObservation(final String obsid, final SamplingFeature sf,
            final Phenomenon phenomenon, final Process procedure, final int count , final AbstractDataRecord datarecord, final MeasureStringBuilder sb, final TemporalGeometricPrimitive time) {

        final DataArrayProperty result = SOSXmlFactory.buildDataArrayProperty("2.0.0", "array-1", count, "SimpleDataArray", datarecord, DEFAULT_ENCODING, sb.getString(), null);
        final FeatureProperty foi = SOSXmlFactory.buildFeatureProperty("2.0.0", sf);
        return OMXmlFactory.buildObservation("2.0.0",       // version
                                             obsid,         // id
                                             obsid,         // name
                                             null,          // description
                                             foi,           // foi
                                             phenomenon,    // phenomenon
                                             procedure,     // procedure
                                             result,        // result
                                             time,
                                             null);
    }

    public static AbstractObservation buildObservation(final String obsid, final SamplingFeature sf,
            final Phenomenon phenomenon, final Process procedure, final int count , final AbstractDataRecord datarecord, final List<Object> dataValues, final TemporalGeometricPrimitive time) {

        final DataArrayProperty result = SOSXmlFactory.buildDataArrayProperty("2.0.0", "array-1", count, "SimpleDataArray", datarecord, DEFAULT_ENCODING, null, dataValues);
        final FeatureProperty foi = SOSXmlFactory.buildFeatureProperty("2.0.0", sf);
        return OMXmlFactory.buildObservation("2.0.0",       // version
                                             obsid,         // id
                                             obsid,         // name
                                             null,          // description
                                             foi,           // foi
                                             phenomenon,    // phenomenon
                                             procedure,     // procedure
                                             result,        // result
                                             time,
                                             null);
    }

    /**
     * Return the physical ID of a sensor.
     * This ID is found into a "Identifier" mark with the name 'supervisorCode'
     *
     * @param sensor
     * @return
     */
    public static String getPhysicalID(final AbstractSensorML sensor) {
        if (sensor != null && sensor.getMember().size() > 0) {
            final AbstractProcess process = sensor.getMember().get(0).getRealProcess();
            final List<? extends AbstractIdentification> idents = process.getIdentification();

            for(AbstractIdentification ident : idents) {
                if (ident.getIdentifierList() != null) {
                    for (AbstractIdentifier identifier: ident.getIdentifierList().getIdentifier()) {
                        if ("supervisorCode".equals(identifier.getName()) && identifier.getTerm() != null) {
                            return identifier.getTerm().getValue();
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * return a SQL formatted timestamp
     *
     * @param time a GML time position object.
     * @return
     * @throws org.geotoolkit.observation.ObservationStoreException
     */
    public static String getTimeValue(final Date time) throws ObservationStoreException {
        if (time != null) {
             try {
                 DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS");
                 final String value = df.format(time);

                 //here t is not used but it allow to verify the syntax of the timestamp
                 final ISODateParser parser = new ISODateParser();
                 final Date d = parser.parseToDate(value);
                 final Timestamp t = new Timestamp(d.getTime());
                 return t.toString();

             } catch(IllegalArgumentException e) {
                throw new ObservationStoreException("Unable to parse the value: " + time.toString() + '\n' +
                                               "Bad format of timestamp:\n" + e.getMessage(),
                                               INVALID_PARAMETER_VALUE, "eventTime");
             }
          } else {
            String locator;
            if (time == null) {
                locator = "Timeposition";
            } else {
                locator = "TimePosition value";
            }
            throw new ObservationStoreException("bad format of time, " + locator + " mustn't be null",
                                              MISSING_PARAMETER_VALUE, "eventTime");
          }
    }

    /**
     * Return an envelope containing all the Observation member of the collection.
     *
     * @param version
     * @param observations
     * @param srsName
     * @return
     */
    public static Envelope getCollectionBound(final String version, final List<Observation> observations, final String srsName) {
        double minx = Double.MAX_VALUE;
        double miny = Double.MAX_VALUE;
        double maxx = -Double.MAX_VALUE;
        double maxy = -Double.MAX_VALUE;

        for (Observation observation: observations) {
            final AbstractFeature feature = (AbstractFeature) observation.getFeatureOfInterest();
            if (feature != null) {
                if (feature.getBoundedBy() != null) {
                    final BoundingShape bound = feature.getBoundedBy();
                    if (bound.getEnvelope() != null) {
                        if (bound.getEnvelope().getLowerCorner() != null
                            && bound.getEnvelope().getLowerCorner().getCoordinate() != null
                            && bound.getEnvelope().getLowerCorner().getCoordinate().length == 2 ) {
                            final double[] lower = bound.getEnvelope().getLowerCorner().getCoordinate();
                            if (lower[0] < minx) {
                                minx = lower[0];
                            }
                            if (lower[1] < miny) {
                                miny = lower[1];
                            }
                        }
                        if (bound.getEnvelope().getUpperCorner() != null
                            && bound.getEnvelope().getUpperCorner().getCoordinate() != null
                            && bound.getEnvelope().getUpperCorner().getCoordinate().length == 2 ) {
                            final double[] upper = bound.getEnvelope().getUpperCorner().getCoordinate();
                            if (upper[0] > maxx) {
                                maxx = upper[0];
                            }
                            if (upper[1] > maxy) {
                                maxy = upper[1];
                            }
                        }
                    }
                }
            }
        }

        if (minx == Double.MAX_VALUE) {
            minx = -180.0;
        }
        if (miny == Double.MAX_VALUE) {
            miny = -90.0;
        }
        if (maxx == (-Double.MAX_VALUE)) {
            maxx = 180.0;
        }
        if (maxy == (-Double.MAX_VALUE)) {
            maxy = 90.0;
        }

        final Envelope env = SOSXmlFactory.buildEnvelope(version, null, minx, miny, maxx, maxy, srsName);
        env.setSrsDimension(2);
        env.setAxisLabels(Arrays.asList("Y X"));
        return env;
    }

    public static Date dateFromTS(Timestamp t) {
        if (t != null) {
            return new Date(t.getTime());
        }
        return null;
    }

    public static String getVersionFromHints(Map<String, Object> hints) {
        if (hints != null && hints.containsKey("version")) {
            Object value = hints.get("version");
            if (value instanceof String) {
                return(String) value;
            } else {
                throw new IllegalArgumentException("unexpected type for hints param version");
            }
        }
        return "2.0.0";
    }

    public static boolean getBooleanHint(Map<String, Object> hints, String key, boolean defaultValue) {
        if (hints != null && hints.containsKey(key)) {
            Object value = hints.get(key);
            if (value instanceof Boolean) {
                return (boolean) value;
            } else if (value instanceof String) {
                return Boolean.parseBoolean((String) value);
            } else {
                throw new IllegalArgumentException("unexpected type for hints param:" + key);
            }
        }
        return defaultValue;
    }

    public static Integer getIntegerHint(Map<String, Object> hints, String key, Integer fallback) {
        if (hints != null && hints.containsKey(key)) {
            Object value = hints.get(key);
            if (value instanceof Integer) {
                return (Integer) value;
            } else if (value instanceof String) {
                return Integer.parseInt((String) value);
            } else {
                throw new IllegalArgumentException("unexpected type for hints param:" + key);
            }
        }
        return fallback;
    }

    public static Long getLongHint(Map<String, Object> hints, String key) {
        if (hints != null && hints.containsKey(key)) {
            Object value = hints.get(key);
            if (value instanceof Long) {
                return (Long) value;
            } else if (value instanceof String) {
                return Long.parseLong((String) value);
            } else {
                throw new IllegalArgumentException("unexpected type for hints param:" + key);
            }
        }
        return null;
    }

    public static OMEntity getObjectTypeHint(Map<String, Object> hints, String key) {
        if (hints != null && hints.containsKey(key)) {
            Object value = hints.get(key);
            if (value instanceof OMEntity) {
                return (OMEntity) value;
            } else if (value instanceof String) {
                return OMEntity.fromName((String) value);
            } else {
                throw new IllegalArgumentException("unexpected type for hints param:" + key);
            }
        }
        return null;
    }

    /**
     * Return true if a composite phenomenon is a subset of another composite.
     * meaning that every of its component is present in the second.
     *
     * @param composite
     * @param fullComposite
     * @return
     */
    public static boolean isACompositeSubSet(CompositePhenomenon composite, CompositePhenomenon fullComposite) {
        for (org.opengis.observation.Phenomenon component : composite.getComponent()) {
            if (!fullComposite.getComponent().contains(component)) {
                return false;
            }
        }
        return true;
    }

    public static CompositePhenomenon getOverlappingComposite(List<CompositePhenomenon> composites) throws DataStoreException {
        a:for (CompositePhenomenon composite : composites) {
            String compoId = getId(composite);
            for (CompositePhenomenon sub : composites) {
                if (!getId(sub).equals(compoId) && !isACompositeSubSet(sub, composite)) {
                    continue a;
                }
            }
            return composite;
        }
        throw new DataStoreException("No composite has all other as subset");
    }

    public static String getId(org.opengis.observation.Phenomenon phen) {
        if (phen instanceof org.geotoolkit.swe.xml.Phenomenon) {
            return ((org.geotoolkit.swe.xml.Phenomenon)phen).getId();
        }
        throw new IllegalArgumentException("Unable to get an id from the phenomenon");
    }

    public static boolean hasComponent(org.opengis.observation.Phenomenon phen, CompositePhenomenon composite) {
        String phenId = getId(phen);
        for (org.opengis.observation.Phenomenon component : composite.getComponent()) {
            if (phenId.equals(getId(component))) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasComponent(String phenId, CompositePhenomenon composite) {
        for (org.opengis.observation.Phenomenon component : composite.getComponent()) {
            if (phenId.equals(getId(component))) {
                return true;
            }
        }
        return false;
    }
}
