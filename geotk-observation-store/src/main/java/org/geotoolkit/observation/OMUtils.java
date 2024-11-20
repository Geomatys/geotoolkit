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
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import org.apache.sis.geometry.AbstractEnvelope;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.wrapper.Geometries;
import org.apache.sis.geometry.wrapper.GeometryWrapper;
import org.apache.sis.metadata.privy.RecordSchemaSIS;
import static org.apache.sis.metadata.privy.RecordSchemaSIS.INSTANCE;
import org.apache.sis.measure.Units;
import org.apache.sis.metadata.ModifiableMetadata;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultDataIdentification;
import org.apache.sis.metadata.iso.quality.DefaultQuantitativeAttributeAccuracy;
import org.apache.sis.metadata.iso.quality.DefaultQuantitativeResult;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.temporal.TemporalObjects;
import org.apache.sis.util.SimpleInternationalString;
import org.apache.sis.util.iso.DefaultRecord;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.observation.model.ComplexResult;
import org.geotoolkit.observation.model.Field;
import org.geotoolkit.observation.result.ResultBuilder;
import org.geotoolkit.observation.model.Phenomenon;
import org.geotoolkit.observation.model.CompositePhenomenon;
import org.geotoolkit.observation.model.FieldType;
import static org.geotoolkit.observation.model.FieldType.BOOLEAN;
import static org.geotoolkit.observation.model.FieldType.QUANTITY;
import static org.geotoolkit.observation.model.FieldType.TEXT;
import static org.geotoolkit.observation.model.FieldType.TIME;
import org.geotoolkit.observation.model.MeasureResult;
import org.geotoolkit.observation.model.Observation;
import org.geotoolkit.observation.model.ObservationUtils;
import org.geotoolkit.temporal.object.ISODateParser;
import org.locationtech.jts.geom.Geometry;
import org.opengis.filter.BinarySpatialOperator;
import org.opengis.filter.Expression;
import org.opengis.filter.Literal;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.quality.Element;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.temporal.TemporalPrimitive;
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
        if (phenomenons.size() == 1) {
            Field single = phenomenons.get(0);
            // look for an existing phenomenon
            for (Phenomenon exisiting : existingPhens) {
                if (!(exisiting instanceof CompositePhenomenon) && exisiting.getId().equals(single.name)) {
                    return exisiting;
                }
            }
            // build a new single phenomenon
            return new Phenomenon(single.name, single.label, single.name, single.description, null);
        } else {
            final List<Phenomenon> types = new ArrayList<>();
            for (Field phen : phenomenons) {
                // try to use existing components because if not, previous properties will be lost by overwritting
                boolean found = false;
                for (Phenomenon exisiting : existingPhens) {
                    if (!(exisiting instanceof CompositePhenomenon) && exisiting.getId().equals(phen.name)) {
                        types.add(exisiting);
                        found = true;
                        break;
                    }
                }
                // build a new phenomenon
                if (!found)  {
                    types.add(new Phenomenon(phen.name, phen.label, phen.name, phen.description, null));
                }
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
            return new CompositePhenomenon(compositeId, name, definition, null, null, types);
        }
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

    /**
     * Return true id the candidate phenomenon is equals, or if is a composite and all the components are present in the second phenomenon.
     *
     * @param candidate
     * @param phenomenon
     * @return
     */
    public static boolean isEqualsOrSubset(Phenomenon candidate, Phenomenon phenomenon) {
        if (Objects.equals(candidate, phenomenon)) {
            return true;
        } else if (phenomenon instanceof CompositePhenomenon composite) {
            if (candidate instanceof CompositePhenomenon compositeCdt) {
                return isACompositeSubSet(compositeCdt, composite);
            } else if (candidate != null) {
                return hasComponent(candidate.getId(), composite);
            }
        }
        return false;
    }

    public static boolean isPartOf(Phenomenon candidate, Phenomenon phenomenon) {
        if (candidate instanceof CompositePhenomenon compositeCdt) {
            if (phenomenon instanceof CompositePhenomenon composite) {
                return OMUtils.isACompositeSubSet(composite, compositeCdt);
            } else if (phenomenon != null) {
                return OMUtils.hasComponent(phenomenon.getId(), compositeCdt);
            }
        }
        return false;
    }

    public static Optional<CompositePhenomenon> getOverlappingComposite(List<? extends CompositePhenomenon> composites) throws DataStoreException {
        a:for (CompositePhenomenon composite : composites) {
            String compoId = composite.getId();
            for (CompositePhenomenon sub : composites) {
                if (!sub.getId().equals(compoId) && !isACompositeSubSet(sub, composite)) {
                    continue a;
                }
            }
            return Optional.of(composite);
        }
        return Optional.empty();
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
                results.add(new Field(i + 2, FieldType.QUANTITY, component.getId(), component.getName(), component.getDefinition(), null));
            }
        } else if (phen != null) {
            results.add(new Field(2, FieldType.QUANTITY, phen.getId(), phen.getName(), phen.getDefinition(), null));
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

    /**
      * Build a complex result.
      *
      * @param fields List of field involved.
      * @param nbValue Number of values in the result.
      * @param values Result values.
      * @return
      */
    public static ComplexResult buildComplexResult(final List<Field> fields, final int nbValue, final ResultBuilder values) {
        return switch (values.getMode()) {
            case CSV        -> new ComplexResult(fields, values.getEncoding(), values.getStringValues(), nbValue);
            case DATA_ARRAY -> new ComplexResult(fields, values.getDataArray(), nbValue);
            case COUNT      -> new ComplexResult(nbValue);
        };
    }

    @Deprecated
    public static TemporalPrimitive buildTime(String id, Date start, Date end) {
        return buildTime(id, (start != null) ? start.toInstant() : null,
                (end != null) ? end.toInstant() : null);
    }

    public static TemporalPrimitive buildTime(String id, Instant start, Instant end) {
        if (start != null && end != null) {
            if (start.equals(end)) {
                var t = TemporalObjects.createInstant(start);
                ObservationUtils.setIdentifier(t, id + "-time");
                return t;
            } else {
                var p = TemporalObjects.createPeriod(start, end);
                ObservationUtils.setIdentifiers(p, id);
                return p;
            }
        } else if (start != null) {
            var t = TemporalObjects.createInstant(start);
            ObservationUtils.setIdentifier(t, id + "-st-time");
            return t;
        } else if (end != null) {
            var t = TemporalObjects.createInstant(end);
            ObservationUtils.setIdentifier(t, id + "-en-time");
            return t;
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
            final String[] blocks = cr.getValues().split(cr.getTextEncodingProperties().getBlockSeparator());
            int mid = 1;
            for (String block : blocks) {
                final String[] lines = block.split(cr.getTextEncodingProperties().getTokenSeparator(), -1);
                TemporalPrimitive measureTime = null;
                int j = 0;
                for (Field f : cr.getFields()) {
                    String token = lines[j];
                    j++;
                    final String idSuffix = "-" + f.index + '-' + mid;
                    final Object value    = readField(f, token);
                    if (timeseries)  {
                        if (f.index == 1) {
                            measureTime = buildTime("time" + idSuffix, (Date) value, null);
                            continue;
                        }
                    } else {
                        measureTime = obs.getSamplingTime();
                    }
                    List<Element> quality = new ArrayList<>();
                    for (Field qf : f.qualityFields) {
                        token = lines[j];
                        j++;
                        final Object qvalue = readField(qf, token);
                        quality.add(createQualityElement(qf, qvalue));
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
                                                          quality,
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

    /**
     * Transform a Complex result in String datablock mode into a data Array.
     *
     * @param cr An observation complex result.
     * @return A complex result data array.
     */
    public static List<Object> toDataArray(ComplexResult cr) {
        List<Object> dataArray = new ArrayList<>();
        final String[] blocks = cr.getValues().split(cr.getTextEncodingProperties().getBlockSeparator());
        for (String block : blocks) {
            final String[] lines = block.split(cr.getTextEncodingProperties().getTokenSeparator(), -1);
            final List<Object> line = new ArrayList<>();
            int j = 0;
            for (Field f : cr.getFields()) {
                String token = lines[j];
                j++;
                final Object value = readField(f, token);
                line.add(value);
                for (Field qf : f.qualityFields) {
                    token = lines[j];
                    j++;
                    final Object qvalue = readField(qf, token);
                    line.add(qvalue);
                }
            }
            dataArray.add(line);
        }
        return dataArray;
    }

    private static Object readField(Field f, final String token) {
        final Object value;
        if (token.isEmpty()) {
            value = null;
        } else {
            value = switch (f.type) {
                case BOOLEAN  -> Boolean.valueOf(token);
                case QUANTITY -> Double.valueOf(token);
                case TEXT     -> token;
                case TIME     ->  new ISODateParser().parseToDate(token);
            };
        }
        return value;
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
     * @param bbox An envelope (2D).
     * @return True if the sampling point is strictly inside the specified envelope.
     * @throws org.apache.sis.storage.DataStoreException If a crs opertation fails.
     */
    public static boolean geometryMatchEnvelope(Geometry geometry, final Envelope bbox) throws DataStoreException {

        final Envelope regionOfInterest;
        if (bbox.getCoordinateReferenceSystem() == null) {
            regionOfInterest = bbox;
        } else {
            try {
                final CoordinateReferenceSystem spCRS = JTS.findCoordinateReferenceSystem(geometry);
                final CoordinateReferenceSystem eCrs = bbox.getCoordinateReferenceSystem();
                final CoordinateReferenceSystem commonCRS = CRS.suggestCommonTarget(null, spCRS, eCrs);

                regionOfInterest = Envelopes.transform(bbox, commonCRS);
                geometry = JTS.convertToCRS(geometry, commonCRS);
            } catch (TransformException | FactoryException ex) {
                throw new DataStoreException(ex);
            }
        }

        final AbstractEnvelope intersectableRegionOfInterest = AbstractEnvelope.castOrCopy(regionOfInterest);
        return Geometries.wrap(geometry)
                .map(GeometryWrapper::getEnvelope)
                .map( e -> intersectableRegionOfInterest.intersects(e, true))
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

    /**
     * Create a Data quality element for the specified field.
     * It is used to add quality element into an observation.
     *
     * @param field An observation field.
     * @param value element value.
     * @return A Data quality element.
     */
    public static Element createQualityElement(Field field, Object value) {
        return createQualityElement(field.name, field.uom, field.type, value);
    }

    /**
     * Create a Data quality element for the specified field attributes. It is
     * used to add quality element into an observation.
     *
     * @param name Field name.
     * @param uom Unit of measure of the specified value. Can be {@code null}
     * @param ft Data type of the element.
     * @param value element value.
     *
     * @return A Data quality element.
     */
    public static Element createQualityElement(String name, String uom, FieldType ft, Object value) {
        DefaultQuantitativeAttributeAccuracy element = new DefaultQuantitativeAttributeAccuracy();
        element.setNamesOfMeasure(Arrays.asList(new SimpleInternationalString(name)));
        if (value != null) {
            DefaultQuantitativeResult res      = new DefaultQuantitativeResult();

            RecordType rt = switch (ft) {
                case TEXT      -> INSTANCE.createRecordType(RecordSchemaSIS.MULTILINE.toInternationalString(), Collections.singletonMap("CharacterString", String.class));
                case BOOLEAN   -> INSTANCE.createRecordType(RecordSchemaSIS.MULTILINE.toInternationalString(), Collections.singletonMap("Boolean", Boolean.class));
                case QUANTITY -> INSTANCE.createRecordType(RecordSchemaSIS.MULTILINE.toInternationalString(), Collections.singletonMap("Real", Double.class));
                case TIME     -> INSTANCE.createRecordType(RecordSchemaSIS.MULTILINE.toInternationalString(), Collections.singletonMap("Date", Date.class));
            };

            DefaultRecord r = new DefaultRecord(rt);
            r.set(rt.getMembers().iterator().next(), value);
            res.setValues(Arrays.asList(r));
            res.setValueType(rt);
            if (uom != null) {
                res.setValueUnit(Units.valueOf(uom));
            }
            element.setResults(Arrays.asList(res));
        }
        return element;
    }

    /**
     * Extract the envelope from a bbox spatial filter.
     *
     * @param box A BBOW spatial filter.
     *
     * @return An envelope.
     * @throws DataStoreException if the secnd operand does not contains an
     * envelope.
     */
    public static Envelope getEnvelopeFromBBOXFilter(BinarySpatialOperator box) throws DataStoreException {
        final Envelope env;
        Expression e2 = box.getOperand2();
        if (e2 instanceof Envelope geoEnv) {
            env = geoEnv;
        } else if (e2 instanceof Literal lit) {
            if (lit.getValue() instanceof Envelope geoEnv) {
                env = geoEnv;
            } else {
                throw new DataStoreException("Unexpected bbox expression type for geometry");
            }
        } else {
            throw new DataStoreException("Unexpected bbox expression type for geometry");
        }
        return env;
    }
}
