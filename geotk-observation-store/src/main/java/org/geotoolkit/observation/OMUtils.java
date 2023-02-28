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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.logging.Logger;
import javax.measure.format.MeasurementParseException;
import javax.xml.namespace.QName;
import org.apache.sis.geometry.AbstractEnvelope;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.internal.feature.Geometries;
import org.apache.sis.internal.feature.GeometryWrapper;
import org.apache.sis.internal.metadata.RecordSchemaSIS;
import static org.apache.sis.internal.metadata.RecordSchemaSIS.INSTANCE;
import org.apache.sis.measure.Units;
import org.apache.sis.metadata.ModifiableMetadata;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultDataIdentification;
import org.apache.sis.metadata.iso.quality.DefaultQuantitativeAttributeAccuracy;
import org.apache.sis.metadata.iso.quality.DefaultQuantitativeResult;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.SimpleInternationalString;
import org.apache.sis.util.iso.DefaultRecord;
import org.apache.sis.util.iso.DefaultRecordType;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.observation.model.ComplexResult;
import org.geotoolkit.observation.model.Field;
import org.geotoolkit.observation.result.ResultBuilder;
import org.geotoolkit.observation.model.Phenomenon;
import org.geotoolkit.observation.model.CompositePhenomenon;
import org.geotoolkit.observation.model.FieldType;
import org.geotoolkit.observation.model.MeasureResult;
import org.geotoolkit.observation.model.Observation;
import org.geotoolkit.observation.model.TextEncoderProperties;
import org.geotoolkit.temporal.object.DefaultInstant;
import org.geotoolkit.temporal.object.DefaultPeriod;
import org.geotoolkit.temporal.object.ISODateParser;
import org.locationtech.jts.geom.Geometry;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.quality.Element;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.ParameterValueGroup;
import static org.opengis.referencing.IdentifiedObject.NAME_KEY;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.temporal.Instant;
import org.opengis.temporal.TemporalGeometricPrimitive;
import org.opengis.util.FactoryException;
import org.opengis.util.RecordType;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class OMUtils {

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.observation");

    public static final String EVENT_TIME = "eventTime";

    public static final String OM_NAMESPACE = "http://www.opengis.net/om/1.0";
    public static final QName OBSERVATION_QNAME = new QName(OM_NAMESPACE, "Observation", "om");
    public static final QName MEASUREMENT_QNAME = new QName(OM_NAMESPACE, "Measurement", "om");

    public static final String RESPONSE_FORMAT_V100 = "text/xml; subtype=\"om/1.0.0\"";
    public static final String RESPONSE_FORMAT_V200 = "http://www.opengis.net/om/2.0";

    public static final Field PRESSION_FIELD  = new Field(-1, FieldType.QUANTITY, "Zlevel", null, "http://mmisw.org/ont/cf/parameter/sea_water_pressure", "dbar");
    public static final Field TIME_FIELD      = new Field(-1, FieldType.TIME,     "time",   null, "http://www.opengis.net/def/property/OGC/0/SamplingTime", null);
    public static final Field LATITUDE_FIELD  = new Field(-1, FieldType.QUANTITY, "lat",    null, "http://mmisw.org/ont/cf/parameter/latitude", "deg");
    public static final Field LONGITUDE_FIELD = new Field(-1, FieldType.QUANTITY, "lon",    null, "http://mmisw.org/ont/cf/parameter/longitude", "deg");

    public static Phenomenon getPhenomenonModels(String name, final List<? extends Field> phenomenons, final String phenomenonIdBase, final Set<Phenomenon> existingPhens) {
        final Phenomenon phenomenon;
        if (phenomenons.size() == 1) {
            phenomenon = new Phenomenon(phenomenons.get(0).name, phenomenons.get(0).label, phenomenons.get(0).name, phenomenons.get(0).description, null);
        } else {
            final List<Phenomenon> types = new ArrayList<>();
            for (Field phen : phenomenons) {
                types.add(new Phenomenon(phen.name, phen.label, phen.name, phen.description, null));
            }

            // look for an already existing (composite) phenomenon to use instead of creating a new one
            for (Phenomenon existingPhen : existingPhens) {
                if (existingPhen instanceof CompositePhenomenon cphen) {
                    if (Objects.equals(cphen.getComponent(), types)) {
                        return cphen;
                    }
                }
            }

            final String compositeId = "composite" + UUID.randomUUID().toString();
            final String definition = phenomenonIdBase + compositeId;
            if (name == null) {
                name = definition;
            }
            phenomenon = new CompositePhenomenon(compositeId, name, definition, null, null, types);
        }
        return phenomenon;
    }

    public static Date dateFromTS(Timestamp t) {
        if (t != null) {
            return new Date(t.getTime());
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

    public static CompositePhenomenon getOverlappingComposite(List<? extends CompositePhenomenon> composites) throws DataStoreException {
        a:for (CompositePhenomenon composite : composites) {
            String compoId = composite.getId();
            for (CompositePhenomenon sub : composites) {
                if (!sub.getId().equals(compoId) && !isACompositeSubSet(sub, composite)) {
                    continue a;
                }
            }
            return composite;
        }
        throw new DataStoreException("No composite has all other as subset");
    }

    public static boolean hasComponent(String phenId, CompositePhenomenon composite) {
        for (Phenomenon component : composite.getComponent()) {
            if (phenId.equals(component.getId())) {
                return true;
            }
        }
        return false;
    }

    public static List<Field> reOrderFields(List<Field> procedureFields, List<Field> subset) {
        List<Field> result = new ArrayList();
        for (Field pField : procedureFields) {
            if (subset.contains(pField)) {
                result.add(pField);
            }
        }
        return result;
    }

    public static List<Field> getPhenomenonsFields(final Phenomenon phen) {
        final List<Field> results = new ArrayList<>();
         if (phen instanceof CompositePhenomenon comp) {

            for (int i = 0; i < comp.getComponent().size(); i++) {
                Phenomenon component = comp.getComponent().get(i);
                String id = component.getId();
                results.add(new Field(i + 2, FieldType.QUANTITY, component.getName(), null, component.getDefinition(), null));
            }
        } else if (phen != null) {
            results.add(new Field(2, FieldType.QUANTITY, phen.getName(), null, phen.getDefinition(), null));
        }
        return results;
    }

     public static List<String> getPhenomenonsFieldIdentifiers(final Phenomenon phen) {
        final List<String> results = new ArrayList<>();
         if (phen instanceof CompositePhenomenon comp) {
            for (Phenomenon component : comp.getComponent()) {
                results.add(component.getId());
            }
        } else if (phen != null) {
            results.add(phen.getId());
        }
        return results;
    }

    public static ComplexResult buildComplexResult(final List<Field> fields, final int nbValue, final TextEncoderProperties encoding, final ResultBuilder values) {
        return switch (values.getMode()) {
            case CSV        -> new ComplexResult(fields, encoding, values.getStringValues(), nbValue);
            case DATA_ARRAY -> new ComplexResult(fields, values.getDataArray(), nbValue);
            case COUNT      -> new ComplexResult(nbValue);
        };
    }

    public static TemporalGeometricPrimitive buildTime(String id, Date start, Date end) {
        if (start != null && end != null) {
            if (start.equals(end)) {
                return new DefaultInstant(Collections.singletonMap(NAME_KEY, id + "time"), start);
            } else {
                return new DefaultPeriod(Collections.singletonMap(NAME_KEY, id + "-time"),
                        new DefaultInstant(Collections.singletonMap(NAME_KEY, id + "-st-time"), start),
                        new DefaultInstant(Collections.singletonMap(NAME_KEY, id + "-en-time"), end));
            }
        } else if (start != null) {
            return new DefaultInstant(Collections.singletonMap(NAME_KEY, id + "-st-time"), start);
        } else if (end != null) {
            return new DefaultInstant(Collections.singletonMap(NAME_KEY, id + "-st-time"), end);
        }
        return null;
    }

    public static List<Observation> splitComplexTemplateIntoMeasurement(Observation obs, List<Integer> fieldFilters) {
         String obsType = (String) obs.getProperties().get("type");
        boolean timeseries = "timeseries".equals(obsType);

        List<Observation> results = new ArrayList<>();
        if (obs.getResult() instanceof ComplexResult cr) {

            for (Field f : cr.getFields()) {

                final String idSuffix = "-" + f.index;

                // no measure for main time field + exclude observation by field
                if ((timeseries && f.index == 1) || (!fieldFilters.isEmpty() && !fieldFilters.contains(f.index))) {
                    continue;
                }
                MeasureResult result = new MeasureResult(f, null);
                Observation measObs = new Observation(obs.getId() + idSuffix,
                                                      obs.getName().getCode() + idSuffix,
                                                      obs.getDescription(),
                                                      obs.getDefinition(),
                                                      getOmTypeFromFieldType(f.type),
                                                      obs.getProcedure(),
                                                      obs.getSamplingTime(),
                                                      obs.getFeatureOfInterest(),
                                                      obs.getObservedProperty(), // TODO split phenomenon
                                                      obs.getResultQuality(),
                                                      result,
                                                      obs.getProperties());
                results.add(measObs);
            }
        } else {
            results.add(obs);
        }
        return results;
    }

    public static List<Observation> splitComplexObservationIntoMeasurement(Observation obs, List<Integer> fieldFilters, List<Integer> measureIdFilters) {
        String obsType = (String) obs.getProperties().get("type");
        boolean timeseries = "timeseries".equals(obsType);

        List<Observation> results = new ArrayList<>();
        if (obs.getResult() instanceof ComplexResult cr) {
            final StringTokenizer blockTokenizer = new StringTokenizer(cr.getValues(), cr.getTextEncodingProperties().getBlockSeparator());
            int mid = 1;
            while (blockTokenizer.hasMoreTokens()) {
                final String block = blockTokenizer.nextToken();
                final StringTokenizer tokenizer = new StringTokenizer(block, cr.getTextEncodingProperties().getTokenSeparator());

                TemporalGeometricPrimitive measureTime = null;
                for (Field f : cr.getFields()) {

                    final String idSuffix = "-" + f.index + '-' + mid;
                    final String token = tokenizer.nextToken();
                    final Object value = switch (f.type) {
                        case BOOLEAN  -> Boolean.valueOf(token);
                        case QUANTITY -> Double.valueOf(token);
                        case TEXT     -> token;
                        case TIME     ->  new ISODateParser().parseToDate(token);
                    };
                    if (timeseries)  {
                        if (f.index == 1) {
                            measureTime = buildTime("time" + idSuffix, (Date) value, null);
                            continue;
                        }
                    } else {
                        measureTime = obs.getSamplingTime();
                    }

                    // exclude observation by field or measure id
                    if ((!fieldFilters.isEmpty() && !fieldFilters.contains(f.index)) || (!measureIdFilters.isEmpty() && !measureIdFilters.contains(mid))) {
                        continue;
                    }

                    MeasureResult result = new MeasureResult(f, value);
                    Observation measObs = new Observation(obs.getId() + idSuffix,
                                                          obs.getName().getCode() + idSuffix,
                                                          obs.getDescription(),
                                                          obs.getDefinition(),
                                                          getOmTypeFromFieldType(f.type),
                                                          obs.getProcedure(),
                                                          measureTime,
                                                          obs.getFeatureOfInterest(),
                                                          obs.getObservedProperty(), // TODO split phenomenon
                                                          obs.getResultQuality(),
                                                          result,
                                                          obs.getProperties());
                    results.add(measObs);
                }
                mid++;
            }
        } else {
            results.add(obs);
        }
        return results;
    }

    public static String getOmTypeFromClass(Class c) {
        if (Double.class.isAssignableFrom(c)) {
            return "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement";
        } else if (Date.class.isAssignableFrom(c) || Instant.class.isAssignableFrom(c)) {
            return "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_TemporalObservation";
        } else if (Integer.class.isAssignableFrom(c)) {
            return "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_CountObservation";
        } else if (Boolean.class.isAssignableFrom(c)) {
            return "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_TruthObservation";
        } else if (String.class.isAssignableFrom(c)) {
            return "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_CategoryObservation";
        } else {
            return "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Observation";
        }
    }

    public static String getOmTypeFromFieldType(FieldType ft) {
        return switch(ft) {
            case BOOLEAN  -> "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_TruthObservation";
            case TIME     -> "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_TemporalObservation";
            case QUANTITY -> "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement";
            //case INT     -> "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_CountObservation";
            case TEXT -> "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_CategoryObservation";
        };
    }


    /**
     * Return true if the samplingPoint entry is strictly inside the specified envelope.
     *
     * @param geometry A JTS geometry station.
     * @param e An envelope (2D).
     * @return True if the sampling point is strictly inside the specified envelope.
     * @throws org.apache.sis.storage.DataStoreException If a crs opertation fails.
     */
    public static boolean samplingFeatureMatchEnvelope(final Geometry geometry, final Envelope e) throws DataStoreException {

        final Envelope regionOfInterest;
        if (e.getCoordinateReferenceSystem() == null) {
            regionOfInterest = e;
        } else {
            try {
                final CoordinateReferenceSystem spCRS = JTS.findCoordinateReferenceSystem(geometry);
                regionOfInterest = spCRS == null ? e : Envelopes.transform(e, spCRS);
            } catch (TransformException | FactoryException ex) {
                throw new DataStoreException(ex);
            }
        }

        final AbstractEnvelope intersectableRegionOfInterest = AbstractEnvelope.castOrCopy(regionOfInterest);
        return Geometries.wrap(geometry)
                .map(GeometryWrapper::getEnvelope)
                .map(intersectableRegionOfInterest::intersects)
                .orElse(false);
    }

    /**
     * Utility method to extract a a parameter value (if its present) and put it
     * in a Map.
     *
     * @param params Configuration parameters.
     * @param param The param descriptor to look for.
     * @param properties The trget map where to put the value.
     */
    public static void extractParameter(final ParameterValueGroup params, ParameterDescriptor param, final Map<String,Object> properties) {
        try {
            String name = param.getName().getCode();
            final Object value = params.parameter(name).getValue();
            if (value != null) {
                properties.put(name, value);
            }
        } catch (ParameterNotFoundException ex) {}
    }

    public static Metadata buildMetadata(final String name) {
        final DefaultMetadata metadata = new DefaultMetadata();
        final DefaultDataIdentification identification = new DefaultDataIdentification();
        final NamedIdentifier identifier = new NamedIdentifier(new DefaultIdentifier(name));
        final DefaultCitation citation = new DefaultCitation(name);
        citation.setIdentifiers(Collections.singleton(identifier));
        identification.setCitation(citation);
        metadata.setIdentificationInfo(Collections.singleton(identification));
        metadata.transitionTo(ModifiableMetadata.State.FINAL);
        return metadata;
    }

    public static <T> List<T> applyPostPagination(List<T> full, Long offset, Long limit) {
        var slice = computeRange(full.size(), offset, limit);
        return slice.isEmpty()
                ? Collections.emptyList()
                : new ArrayList<>(full.subList(slice.from, slice.to));
    }

    private static IntRange computeRange(int maxSize, Long offset, Long limit) {
        final int from = offset == null ? 0 : Math.toIntExact(offset);
        final int to = limit == null ? maxSize : Math.min(Math.toIntExact(from + limit), maxSize);
        return new IntRange(from, to);
    }

    private record IntRange(int from, int to) {
        boolean isEmpty() { return from >= to; }
    }

    public static <K, V> Map<K, V> applyPostPagination(Map<K, V> full, Long offset, Long limit) {
        var slice = computeRange(full.size(), offset, limit);
        if (slice.isEmpty()) return Collections.EMPTY_MAP;

        var result = new LinkedHashMap<K, V>();
        var it = full.entrySet().iterator();
        int i = 0;
        while (it.hasNext() && i < slice.to) {
             var e = it.next();
            if (i >= slice.from) {
                result.put(e.getKey(), e.getValue());
            }
            i++;
        }
        return result;
    }

    public static Element createQualityElement(Field field, Object value) {
        DefaultQuantitativeAttributeAccuracy element = new DefaultQuantitativeAttributeAccuracy();
        element.setNamesOfMeasure(Arrays.asList(new SimpleInternationalString(field.name)));
        if (value != null) {
            DefaultQuantitativeResult res      = new DefaultQuantitativeResult();

            RecordType rt = INSTANCE.createRecordType("global", Collections.singletonMap("CharacterString", String.class));

            DefaultRecord r = new DefaultRecord(rt);
            r.set(rt.getMembers().iterator().next(), value);
            res.setValues(Arrays.asList(r));
            res.setValueType(rt);
            if (field.uom != null) {
                res.setValueUnit(Units.valueOf(field.uom));
            }
            element.setResults(Arrays.asList(res));
        }
        return element;
    }

    /**
     * Temporary until SIS version is updated
     */
    public static Element createQualityElement2(Field field, Object value) throws ReflectiveOperationException {
        return createQualityElement2(field.name, field.uom, value);
    }

    /**
     * Temporary until SIS version is updated
     */
    public static Element createQualityElement2(String name, String uom, Object value) throws ReflectiveOperationException {
        ArgumentChecks.ensureNonNull("name", name);
        DefaultQuantitativeAttributeAccuracy element = new DefaultQuantitativeAttributeAccuracy();
        element.setNamesOfMeasure(Collections.singleton(new SimpleInternationalString(name)));
        if (value != null) {
            DefaultQuantitativeResult res = new DefaultQuantitativeResult();
            Constructor<DefaultRecordType> c = DefaultRecordType.class.getDeclaredConstructor();
            c.setAccessible(true);
            DefaultRecordType rt = c.newInstance();
            Method m = DefaultRecordType.class.getDeclaredMethod("setValue", String.class);
            m.setAccessible(true);
            m.invoke(rt, "CharacterString");
            DefaultRecord r = new DefaultRecord(RecordSchemaSIS.STRING);
            r.set(RecordSchemaSIS.STRING.getMembers().iterator().next(), value);
            res.setValues(Collections.singletonList(r));
            res.setValueType(rt);
            if (uom != null) {
                try {
                    res.setValueUnit(Units.valueOf(uom));
                } catch (MeasurementParseException ex) {
                    LOGGER.warning("Error while parsing uom : " + uom);
                }
            }
            element.setResults(Collections.singleton(res));
        }
        return element;
    }
}
