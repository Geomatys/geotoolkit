/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.observation;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.gml.xml.AbstractFeature;
import org.geotoolkit.gml.xml.BoundingShape;
import org.geotoolkit.gml.xml.Envelope;
import static org.geotoolkit.ows.xml.OWSExceptionCode.INVALID_PARAMETER_VALUE;
import static org.geotoolkit.ows.xml.OWSExceptionCode.MISSING_PARAMETER_VALUE;
import org.geotoolkit.sml.xml.AbstractIdentification;
import org.geotoolkit.sml.xml.AbstractIdentifier;
import org.geotoolkit.sml.xml.AbstractProcess;
import org.geotoolkit.sml.xml.AbstractSensorML;
import org.geotoolkit.sos.xml.SOSXmlFactory;
import org.geotoolkit.temporal.object.ISODateParser;
import org.opengis.observation.CompositePhenomenon;
import org.opengis.observation.Observation;
import org.opengis.observation.Phenomenon;

/**
 * Utility methods for SOS / Sensor.
 *
 * @author Guilhem Legal (Geomatys)
 */
public class Utils {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.observation");

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
        for (Phenomenon component : composite.getComponent()) {
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

    public static String getId(Phenomenon phen) {
        if (phen instanceof org.geotoolkit.swe.xml.Phenomenon) {
            return ((org.geotoolkit.swe.xml.Phenomenon)phen).getId();
        }
        throw new IllegalArgumentException("Unable to get an id from the phenomenon");
    }

    public static boolean hasComponent(Phenomenon phen, CompositePhenomenon composite) {
        String phenId = getId(phen);
        for (Phenomenon component : composite.getComponent()) {
            if (phenId.equals(getId(component))) {
                return true;
            }
        }
        return false;
    }
}
