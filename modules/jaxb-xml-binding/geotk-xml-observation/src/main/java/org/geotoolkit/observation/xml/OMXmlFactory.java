/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

package org.geotoolkit.observation.xml;

import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.gml.xml.FeatureProperty;
import org.geotoolkit.gml.xml.GMLXmlFactory;
import org.geotoolkit.swe.xml.v101.PhenomenonType;
import org.opengis.observation.Measurement;
import org.opengis.observation.Observation;
import org.opengis.observation.sampling.SamplingFeature;
import org.opengis.temporal.Instant;
import org.opengis.temporal.Period;
import org.opengis.temporal.TemporalGeometricPrimitive;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class OMXmlFactory {

    public static AbstractObservation convert(final String version, final Observation observation) {
        if (version.equals("2.0.0")) {
            if (observation instanceof org.geotoolkit.observation.xml.v100.ObservationType) {
                return convertTo200(observation);
            } else {
                return (AbstractObservation)observation;
            }
        } else if (version.equals("1.0.0")) {
            if (observation instanceof org.geotoolkit.observation.xml.v200.OMObservationType) {
                return convertTo100(observation);
            } else {
                return (AbstractObservation)observation;
            }
        } else {
            throw new IllegalArgumentException("unexpected O&M version number:" + version);
        }
    }
    
    private static AbstractObservation convertTo100(final Observation observation) {
        final String name = observation.getName();
        final String definition = observation.getDefinition();
        final org.geotoolkit.gml.xml.v311.AbstractTimeGeometricPrimitiveType time;
        if (observation.getSamplingTime() instanceof Period) {
            final Period p = (Period) observation.getSamplingTime();
            String dateBegin = null;
            if (p.getBeginning() != null && p.getBeginning().getPosition() != null) {
                dateBegin = p.getBeginning().getPosition().getDateTime().toString();
            }
            String dateEnd = null;
            if (p.getEnding() != null && p.getEnding().getPosition() != null) {
                dateEnd = p.getEnding().getPosition().getDateTime().toString();
            }
            time = (org.geotoolkit.gml.xml.v311.AbstractTimeGeometricPrimitiveType) GMLXmlFactory.createTimePeriod("3.1.1", null, dateBegin, dateEnd);
        } else if (observation.getSamplingTime() instanceof Instant) {
            final Instant p = (Instant) observation.getSamplingTime();
            String date = null;
            if (p.getPosition() != null) {
                date = p.getPosition().getDateTime().toString();
            }
            time = (org.geotoolkit.gml.xml.v311.AbstractTimeGeometricPrimitiveType) GMLXmlFactory.createTimeInstant("3.1.1", null, date);
        } else if (observation.getSamplingTime() != null) {
            throw new IllegalArgumentException("Unexpected samplingTime type:" + observation.getSamplingTime().getClass().getName());
        } else {
            time = null;
        }
        final String procedure = ((org.geotoolkit.observation.xml.Process)observation.getProcedure()).getHref();
        final String phenomenonName = ((org.geotoolkit.swe.xml.Phenomenon)observation.getObservedProperty()).getName();
        // extract id
        final String phenId;
        if (phenomenonName.indexOf(':') != -1) {
            phenId = phenomenonName.substring(phenomenonName.lastIndexOf(':') + 1, phenomenonName.length());
        } else {
            phenId = phenomenonName;
        }
        final org.geotoolkit.swe.xml.v101.PhenomenonType observedProperty = new PhenomenonType(phenId, phenomenonName);
        
        final org.geotoolkit.sampling.xml.v100.SamplingFeatureType sf = (org.geotoolkit.sampling.xml.v100.SamplingFeatureType)convertTo100((SamplingFeature)observation.getFeatureOfInterest());
        final Object result;
        if (observation.getResult() instanceof org.geotoolkit.swe.xml.v200.DataArrayPropertyType) {
            final org.geotoolkit.swe.xml.v200.DataArrayPropertyType resultv200 = (org.geotoolkit.swe.xml.v200.DataArrayPropertyType) observation.getResult();
            final org.geotoolkit.swe.xml.v200.TextEncodingType encodingV200 = (org.geotoolkit.swe.xml.v200.TextEncodingType) resultv200.getDataArray().getEncoding();

            final int count = resultv200.getDataArray().getElementCount().getCount().getValue();
            final String id = resultv200.getDataArray().getId();
            final org.geotoolkit.swe.xml.v101.TextBlockType enc = new org.geotoolkit.swe.xml.v101.TextBlockType(encodingV200.getId(), encodingV200.getDecimalSeparator(), encodingV200.getTokenSeparator(), encodingV200.getBlockSeparator());
            final String values = resultv200.getDataArray().getValues();
            
            final org.geotoolkit.swe.xml.v200.DataRecordType recordv200 =  (org.geotoolkit.swe.xml.v200.DataRecordType) resultv200.getDataArray().getElementType().getAbstractRecord();
            final List<org.geotoolkit.swe.xml.v101.AnyScalarPropertyType> fields = new ArrayList<org.geotoolkit.swe.xml.v101.AnyScalarPropertyType>();
            for (org.geotoolkit.swe.xml.v200.Field scalar : recordv200.getField()) {
                fields.add(new org.geotoolkit.swe.xml.v101.AnyScalarPropertyType(scalar));
            }
            final org.geotoolkit.swe.xml.v101.SimpleDataRecordType record = new org.geotoolkit.swe.xml.v101.SimpleDataRecordType(null, 
                                                                                                                                 recordv200.getId(), 
                                                                                                                                 recordv200.getDefinition(),
                                                                                                                                 recordv200.isFixed(),
                                                                                                                                 fields);
                    
            final org.geotoolkit.swe.xml.v101.DataArrayType array = new org.geotoolkit.swe.xml.v101.DataArrayType(id, count, null, record, enc, values);
            final org.geotoolkit.swe.xml.v101.DataArrayPropertyType resultv100 = new org.geotoolkit.swe.xml.v101.DataArrayPropertyType(array);
            result = resultv100;
        } else {
            result = observation.getResult();
        }
        
        if (observation instanceof Measurement) {
            return new org.geotoolkit.observation.xml.v100.MeasurementType(name, definition, sf, observedProperty, procedure, (org.geotoolkit.observation.xml.v100.MeasureType)result, time);
        } else {
            return new org.geotoolkit.observation.xml.v100.ObservationType(name, definition, sf, observedProperty, procedure, result, time);
        }
    }
    
    private static AbstractObservation convertTo200(final Observation observation) {
       
        final String name = observation.getName();
        final String type;
        if (observation instanceof Measurement) {
            type = "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement";
        } else {
            type = "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_ComplexObservation";
        }
        final org.geotoolkit.gml.xml.v321.AbstractTimeObjectType time;
        if (observation.getSamplingTime() instanceof Period) {
            final Period p = (Period) observation.getSamplingTime();
            String dateBegin = null;
            if (p.getBeginning() != null && p.getBeginning().getPosition() != null) {
                dateBegin = p.getBeginning().getPosition().getDateTime().toString();
            }
            String dateEnd = null;
            if (p.getEnding() != null && p.getEnding().getPosition() != null) {
                dateEnd = p.getEnding().getPosition().getDateTime().toString();
            }
            time = (org.geotoolkit.gml.xml.v321.AbstractTimeObjectType) GMLXmlFactory.createTimePeriod("3.2.1", null, dateBegin, dateEnd);
        } else if (observation.getSamplingTime() instanceof Instant) {
            final Instant p = (Instant) observation.getSamplingTime();
            String date = null;
            if (p.getPosition() != null) {
                date = p.getPosition().getDateTime().toString();
            }
            time = (org.geotoolkit.gml.xml.v321.AbstractTimeObjectType) GMLXmlFactory.createTimeInstant("3.2.1", null, date);
        } else if (observation.getSamplingTime() != null) {
            throw new IllegalArgumentException("Unexpected samplingTime type:" + observation.getSamplingTime().getClass().getName());
        } else {
            time = null;
        }
        final String procedure            = ((org.geotoolkit.observation.xml.Process)observation.getProcedure()).getHref();
        final String observedProperty     = ((org.geotoolkit.swe.xml.Phenomenon)observation.getObservedProperty()).getName();
        final SamplingFeature sf          = convertTo200((SamplingFeature)observation.getFeatureOfInterest());
        final org.geotoolkit.gml.xml.v321.FeaturePropertyType feature = (org.geotoolkit.gml.xml.v321.FeaturePropertyType) buildFeatureProperty("2.0.0", sf);
        final Object result;
        if (observation.getResult() instanceof org.geotoolkit.swe.xml.v101.DataArrayPropertyType) {
            final org.geotoolkit.swe.xml.v101.DataArrayPropertyType resultv100 = (org.geotoolkit.swe.xml.v101.DataArrayPropertyType) observation.getResult();
            final org.geotoolkit.swe.xml.v101.TextBlockType encodingV100 = (org.geotoolkit.swe.xml.v101.TextBlockType) resultv100.getDataArray().getEncoding();

            final int count = resultv100.getDataArray().getElementCount().getCount().getValue();
            final String id = resultv100.getDataArray().getId();
            final org.geotoolkit.swe.xml.v200.TextEncodingType enc = new org.geotoolkit.swe.xml.v200.TextEncodingType(encodingV100.getId(), encodingV100.getDecimalSeparator(), encodingV100.getTokenSeparator(), encodingV100.getBlockSeparator());
            final String values = resultv100.getDataArray().getValues();
            final org.geotoolkit.swe.xml.v101.SimpleDataRecordType recordv100 =  (org.geotoolkit.swe.xml.v101.SimpleDataRecordType) resultv100.getDataArray().getElementType();
            final List<org.geotoolkit.swe.xml.v200.Field> fields = new ArrayList<org.geotoolkit.swe.xml.v200.Field>();
            for (org.geotoolkit.swe.xml.v101.AnyScalarPropertyType scalar : recordv100.getField()) {
                final org.geotoolkit.swe.xml.v200.AbstractDataComponentType component = convert(scalar.getValue());
                fields.add(new org.geotoolkit.swe.xml.v200.Field(scalar.getName(), component));
            }
            final org.geotoolkit.swe.xml.v200.DataRecordType record = new org.geotoolkit.swe.xml.v200.DataRecordType(recordv100.getId(), recordv100.getDefinition(), recordv100.isFixed(), fields);
            //final ElementType elem = new ElementType(resultv100.getDataArray().getName(), record);
            final org.geotoolkit.swe.xml.v200.DataArrayType array = new org.geotoolkit.swe.xml.v200.DataArrayType(id, count, enc, values, id, record);
            final org.geotoolkit.swe.xml.v200.DataArrayPropertyType resultv200 = new org.geotoolkit.swe.xml.v200.DataArrayPropertyType(array);
            result = resultv200;
        } else {
            result = observation.getResult();
        }
        return new org.geotoolkit.observation.xml.v200.OMObservationType(null, name, type, time, procedure, observedProperty, feature, result);
    }
    
    public static SamplingFeature convert(final String version, final SamplingFeature feature) {
        if (version.equals("2.0.0")) {
            if (feature instanceof org.geotoolkit.sampling.xml.v100.SamplingFeatureType) {
                return convertTo200(feature);
            } else {
                return feature;
            }
        } else if (version.equals("1.0.0")) {
            if (feature instanceof org.geotoolkit.sampling.xml.v200.SFSamplingFeatureType) {
                return convertTo100(feature);
            } else {
                return feature;
            }
        } else {
            throw new IllegalArgumentException("unexpected O&M version number:" + version);
        }
    }
    
    private static SamplingFeature convertTo100(final SamplingFeature feature) {
        final org.geotoolkit.sampling.xml.v200.SFSamplingFeatureType feature200 = (org.geotoolkit.sampling.xml.v200.SFSamplingFeatureType) feature;
        final org.geotoolkit.gml.xml.v311.FeaturePropertyType fp;
            if (feature200.getSampledFeatureProperty().getHref() != null) {
                fp = new org.geotoolkit.gml.xml.v311.FeaturePropertyType(feature200.getSampledFeatureProperty().getHref());
            } else {
                fp = null;
            }
        if (feature200.getType().getHref().equals("http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingPoint")) {
            final org.geotoolkit.gml.xml.v311.PointType pt;
            if (feature200.getGeometry() != null) {
                final org.geotoolkit.gml.xml.v321.PointType pt200 = (org.geotoolkit.gml.xml.v321.PointType)feature200.getGeometry();
                pt = new org.geotoolkit.gml.xml.v311.PointType(pt200.getDirectPosition());
            } else {
                pt = null;
            }
            return new org.geotoolkit.sampling.xml.v100.SamplingPointType(feature200.getId(), feature200.getName(), feature200.getDescription(),
                    fp, pt);
        } else if (feature200.getType().getHref().equals("http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingCurve")) {
            
            final org.geotoolkit.gml.xml.v311.LineStringType pt;
            if (feature200.getGeometry() != null && feature200.getGeometry() instanceof org.geotoolkit.gml.xml.v321.LineStringType) {
                final org.geotoolkit.gml.xml.v321.LineStringType line321 = (org.geotoolkit.gml.xml.v321.LineStringType)feature200.getGeometry();
                final List<org.geotoolkit.gml.xml.v311.DirectPositionType> positions = new ArrayList<org.geotoolkit.gml.xml.v311.DirectPositionType>();
                for (org.geotoolkit.gml.xml.v321.DirectPositionType pos : line321.getPos()) {
                    positions.add(new org.geotoolkit.gml.xml.v311.DirectPositionType(pos.getValue()));
                }
                pt = new org.geotoolkit.gml.xml.v311.LineStringType(line321.getId(), line321.getSrsName(), positions);

            } else {
                pt = null;
            }
            final org.geotoolkit.gml.xml.v311.EnvelopeType env = new org.geotoolkit.gml.xml.v311.EnvelopeType(feature200.getBoundedBy().getEnvelope());
            return new org.geotoolkit.sampling.xml.v100.SamplingCurveType(feature200.getId(), feature200.getName(), feature200.getDescription(), 
                    fp, new org.geotoolkit.gml.xml.v311.CurvePropertyType(pt), null, env);
        } else if (feature instanceof org.geotoolkit.sampling.xml.v200.SFSamplingFeatureType) {
            
            return new org.geotoolkit.sampling.xml.v100.SamplingFeatureType(feature200.getId(), feature200.getName(), feature200.getDescription(), fp);
        } else {
            throw new IllegalArgumentException("unexpected feature type.");
        }
    }
    
    private static SamplingFeature convertTo200(final SamplingFeature feature) {
        if (feature instanceof  org.geotoolkit.sampling.xml.v100.SamplingPointType) {
            final org.geotoolkit.sampling.xml.v100.SamplingPointType sp = (org.geotoolkit.sampling.xml.v100.SamplingPointType) feature;
            final org.geotoolkit.gml.xml.v321.FeaturePropertyType fp;
            if (sp.getSampledFeatures() != null && !sp.getSampledFeatures().isEmpty()) {
                fp = new org.geotoolkit.gml.xml.v321.FeaturePropertyType(sp.getSampledFeatures().iterator().next().getHref());
            } else {
                fp = null;
            }
            final org.geotoolkit.gml.xml.v321.PointType pt;
            if (sp.getPosition() != null) {
                final org.geotoolkit.gml.xml.v321.DirectPositionType dp = new org.geotoolkit.gml.xml.v321.DirectPositionType(sp.getPosition().getPos());
                pt = new org.geotoolkit.gml.xml.v321.PointType(sp.getPosition().getId(), dp);
            } else {
                pt = null;
            }
            return new org.geotoolkit.samplingspatial.xml.v200.SFSpatialSamplingFeatureType(sp.getId(), sp.getName(), sp.getDescription(),
                    "http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingPoint", fp, pt, null);
        } else if (feature instanceof org.geotoolkit.sampling.xml.v100.SamplingCurveType) {
            final org.geotoolkit.sampling.xml.v100.SamplingCurveType sp = (org.geotoolkit.sampling.xml.v100.SamplingCurveType) feature;
            final org.geotoolkit.gml.xml.v321.FeaturePropertyType fp;
            if (sp.getSampledFeatures() != null && !sp.getSampledFeatures().isEmpty()) {
                fp = new org.geotoolkit.gml.xml.v321.FeaturePropertyType(sp.getSampledFeatures().iterator().next().getHref());
            } else {
                fp = null;
            }
            final org.geotoolkit.gml.xml.v321.LineStringType pt;
            if (sp.getShape() != null && sp.getShape().getAbstractCurve() instanceof org.geotoolkit.gml.xml.v311.LineStringType) {
                final org.geotoolkit.gml.xml.v311.LineStringType line311 = (org.geotoolkit.gml.xml.v311.LineStringType)sp.getShape().getAbstractCurve();
                final List<org.geotoolkit.gml.xml.v321.DirectPositionType> positions = new ArrayList<org.geotoolkit.gml.xml.v321.DirectPositionType>();
                for (org.geotoolkit.gml.xml.v311.DirectPositionType pos : line311.getPos()) {
                    positions.add(new org.geotoolkit.gml.xml.v321.DirectPositionType(pos.getValue()));
                }
                pt = new org.geotoolkit.gml.xml.v321.LineStringType(line311.getId(), positions);

            } else {
                pt = null;
            }
            final org.geotoolkit.gml.xml.v321.EnvelopeType env = new org.geotoolkit.gml.xml.v321.EnvelopeType(sp.getBoundedBy().getEnvelope());
            return new org.geotoolkit.samplingspatial.xml.v200.SFSpatialSamplingFeatureType(sp.getId(), sp.getName(), sp.getDescription(), 
                    "http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingCurve", fp, pt, env);
        } else if (feature instanceof org.geotoolkit.sampling.xml.v100.SamplingFeatureType) {
            final org.geotoolkit.sampling.xml.v100.SamplingFeatureType sp = (org.geotoolkit.sampling.xml.v100.SamplingFeatureType) feature;
            final org.geotoolkit.gml.xml.v321.FeaturePropertyType fp;
            if (sp.getSampledFeatures() != null && !sp.getSampledFeatures().isEmpty()) {
                fp = new org.geotoolkit.gml.xml.v321.FeaturePropertyType(sp.getSampledFeatures().iterator().next().getHref());
            } else {
                fp = null;
            }
            return new org.geotoolkit.sampling.xml.v200.SFSamplingFeatureType(sp.getId(), sp.getName(), sp.getDescription(), 
                    "http://www.opengis.net/def/samplingFeatureType/OGC-OM/SF_SamplingFeature", fp, null);
        } else {
            throw new IllegalArgumentException("unexpected feature type.");
        }
    }
    
    public static org.geotoolkit.swe.xml.v200.AbstractDataComponentType convert(org.geotoolkit.swe.xml.v101.AbstractDataComponentType data) {
        if (data instanceof org.geotoolkit.swe.xml.v101.BooleanType) {
            final org.geotoolkit.swe.xml.v101.BooleanType old = (org.geotoolkit.swe.xml.v101.BooleanType)data;
            return new org.geotoolkit.swe.xml.v200.BooleanType(old.isValue(), old.getDefinition());
        } else if (data instanceof org.geotoolkit.swe.xml.v101.VectorType) {
            final org.geotoolkit.swe.xml.v101.VectorType old = (org.geotoolkit.swe.xml.v101.VectorType)data;
            return new org.geotoolkit.swe.xml.v200.VectorType(); // TODO
        } else if (data instanceof org.geotoolkit.swe.xml.v101.TimeType) {
            final org.geotoolkit.swe.xml.v101.TimeType old = (org.geotoolkit.swe.xml.v101.TimeType)data;
            return new org.geotoolkit.swe.xml.v200.TimeType(old.getDefinition(), null);
        } else if (data instanceof org.geotoolkit.swe.xml.v101.TimeRange) {
            final org.geotoolkit.swe.xml.v101.TimeRange old = (org.geotoolkit.swe.xml.v101.TimeRange)data;
            return new org.geotoolkit.swe.xml.v200.TimeRangeType(old.getDefinition(), old.getValue());
        } else if (data instanceof org.geotoolkit.swe.xml.v101.Category) {
            final org.geotoolkit.swe.xml.v101.Category old = (org.geotoolkit.swe.xml.v101.Category)data;
            return new org.geotoolkit.swe.xml.v200.CategoryType(old.getDefinition(), old.getValue());
        } else if (data instanceof org.geotoolkit.swe.xml.v101.QuantityRange) {
            final org.geotoolkit.swe.xml.v101.QuantityRange old = (org.geotoolkit.swe.xml.v101.QuantityRange)data;
            return new org.geotoolkit.swe.xml.v200.QuantityRangeType(old.getDefinition(), old.getValue());
        } else if (data instanceof org.geotoolkit.swe.xml.v101.CountRange) {
            final org.geotoolkit.swe.xml.v101.CountRange old = (org.geotoolkit.swe.xml.v101.CountRange)data;
            return new org.geotoolkit.swe.xml.v200.CountRangeType(old.getDefinition(), old.getValue());
        } else if (data instanceof org.geotoolkit.swe.xml.v101.QuantityType) {
            final org.geotoolkit.swe.xml.v101.QuantityType old = (org.geotoolkit.swe.xml.v101.QuantityType)data;
            String uomCode = null;
            if (old.getUom() != null) {
                uomCode = old.getUom().getCode();
            }
            return new org.geotoolkit.swe.xml.v200.QuantityType(old.getDefinition(), uomCode, old.getValue());
        } else if (data instanceof org.geotoolkit.swe.xml.v101.Text) {
            final org.geotoolkit.swe.xml.v101.Text old = (org.geotoolkit.swe.xml.v101.Text)data;
            return new org.geotoolkit.swe.xml.v200.TextType(old.getDefinition(), old.getValue());
        } else if (data instanceof org.geotoolkit.swe.xml.v101.Count) {
            final org.geotoolkit.swe.xml.v101.Count old = (org.geotoolkit.swe.xml.v101.Count)data;
            return new org.geotoolkit.swe.xml.v200.CountType(old.getDefinition(), old.getValue());
        } else {
            throw new IllegalArgumentException("Unexpected data component type:" + data);
        }
    }
    
    public static FeatureProperty buildFeatureProperty(final String version, final SamplingFeature feature) {
        if ("1.0.0".equals(version)) {
            final org.geotoolkit.sampling.xml.v100.ObjectFactory samplingFactory = new org.geotoolkit.sampling.xml.v100.ObjectFactory();
            if (feature instanceof org.geotoolkit.sampling.xml.v100.SamplingPointType) {
                return new org.geotoolkit.gml.xml.v311.FeaturePropertyType(samplingFactory.createSamplingPoint((org.geotoolkit.sampling.xml.v100.SamplingPointType)feature));
            } else if (feature instanceof org.geotoolkit.sampling.xml.v100.SamplingCurveType) {
                return new org.geotoolkit.gml.xml.v311.FeaturePropertyType(samplingFactory.createSamplingCurve((org.geotoolkit.sampling.xml.v100.SamplingCurveType)feature));
            } else if (feature instanceof org.geotoolkit.sampling.xml.v100.SamplingSolidType) {
                return new org.geotoolkit.gml.xml.v311.FeaturePropertyType(samplingFactory.createSamplingSolid((org.geotoolkit.sampling.xml.v100.SamplingSolidType)feature));
            } else if (feature instanceof org.geotoolkit.sampling.xml.v100.SamplingSurfaceType) {
                return new org.geotoolkit.gml.xml.v311.FeaturePropertyType(samplingFactory.createSamplingSurface((org.geotoolkit.sampling.xml.v100.SamplingSurfaceType)feature));
            } else if (feature instanceof org.geotoolkit.sampling.xml.v100.SamplingFeatureType) {
                return new org.geotoolkit.gml.xml.v311.FeaturePropertyType(samplingFactory.createSamplingFeature((org.geotoolkit.sampling.xml.v100.SamplingFeatureType)feature));
            } else if (feature != null) {
                throw new IllegalArgumentException("unexpected object version");
            }
        } else if ("2.0.0".equals(version)) {
             final org.geotoolkit.sampling.xml.v200.ObjectFactory samplingFactory = new org.geotoolkit.sampling.xml.v200.ObjectFactory();
             final org.geotoolkit.samplingspatial.xml.v200.ObjectFactory spatialFactory = new org.geotoolkit.samplingspatial.xml.v200.ObjectFactory();
             if (feature instanceof org.geotoolkit.samplingspatial.xml.v200.SFSpatialSamplingFeatureType) {
                return new org.geotoolkit.gml.xml.v321.FeaturePropertyType(spatialFactory.createSFSpatialSamplingFeature((org.geotoolkit.samplingspatial.xml.v200.SFSpatialSamplingFeatureType)feature));
             } else if (feature instanceof org.geotoolkit.sampling.xml.v200.SFSamplingFeatureType) {
                 return new org.geotoolkit.gml.xml.v321.FeaturePropertyType(samplingFactory.createSFSamplingFeature((org.geotoolkit.sampling.xml.v200.SFSamplingFeatureType)feature));
             } else if (feature != null) { 
                throw new IllegalArgumentException("unexpected object version");
             }
        } else {
            throw new IllegalArgumentException("unexpected sos version number:" + version);
        }
        return null;
    }
    
    public static Observation buildObservation(final String version, final String id, final String name, final String definition, final FeatureProperty sampledFeature, final org.opengis.observation.Phenomenon phen,
            final String procedure, final Object result, final TemporalGeometricPrimitive time) {
        if ("1.0.0".equals(version)) {
            if (sampledFeature != null && !(sampledFeature instanceof org.geotoolkit.gml.xml.v311.FeaturePropertyType)) {
                throw new IllegalArgumentException("unexpected object version for sampled feature element");
            }
            if (phen != null && !(phen instanceof org.geotoolkit.swe.xml.v101.PhenomenonType)) {
                throw new IllegalArgumentException("unexpected object version for phenomenon element");
            }
            if (time != null && !(time instanceof org.geotoolkit.gml.xml.v311.AbstractTimeGeometricPrimitiveType)) {
                throw new IllegalArgumentException("unexpected object version for time element");
            }
            return new org.geotoolkit.observation.xml.v100.ObservationType(name, 
                                                                           definition, 
                                                                           (org.geotoolkit.gml.xml.v311.FeaturePropertyType)sampledFeature,
                                                                           (org.geotoolkit.swe.xml.v101.PhenomenonType)phen, 
                                                                           procedure , 
                                                                           result, 
                                                                           (org.geotoolkit.gml.xml.v311.AbstractTimeGeometricPrimitiveType)time);
        } else if ("2.0.0".equals(version)) {
            if (sampledFeature != null && !(sampledFeature instanceof org.geotoolkit.gml.xml.v321.FeaturePropertyType)) {
                throw new IllegalArgumentException("unexpected object version for sampled feature element");
            }
            if (time != null && !(time instanceof org.geotoolkit.gml.xml.v321.AbstractTimeObjectType)) {
                throw new IllegalArgumentException("unexpected object version for time element");
            }
            if (phen != null && !(phen instanceof org.geotoolkit.swe.xml.Phenomenon)) {
                throw new IllegalArgumentException("unexpected object version for phenomenon element");
            }
           return new org.geotoolkit.observation.xml.v200.OMObservationType(id,
                                                                            name, 
                                                                            "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_ComplexObservation", 
                                                                            (org.geotoolkit.gml.xml.v321.AbstractTimeObjectType)time,
                                                                            procedure,
                                                                            ((org.geotoolkit.swe.xml.Phenomenon)phen).getName(),
                                                                            (org.geotoolkit.gml.xml.v321.FeaturePropertyType)sampledFeature,
                                                                            result);
        } else {
            throw new IllegalArgumentException("unexpected sos version number:" + version);
        }
    }
    
    public static Observation buildMeasurement(final String version, final String id, final String name, final String definition, final FeatureProperty sampledFeature, final org.opengis.observation.Phenomenon phen,
            final String procedure, final Object result, final TemporalGeometricPrimitive time) {
        if ("1.0.0".equals(version)) {
            if (sampledFeature != null && !(sampledFeature instanceof org.geotoolkit.gml.xml.v311.FeaturePropertyType)) {
                throw new IllegalArgumentException("unexpected object version for sampled feature element");
            }
            if (phen != null && !(phen instanceof org.geotoolkit.swe.xml.v101.PhenomenonType)) {
                throw new IllegalArgumentException("unexpected object version for phenomenon element");
            }
            if (time != null && !(time instanceof org.geotoolkit.gml.xml.v311.AbstractTimeGeometricPrimitiveType)) {
                throw new IllegalArgumentException("unexpected object version for time element");
            }
            if (result != null && !(result instanceof org.geotoolkit.observation.xml.v100.MeasureType)) {
                throw new IllegalArgumentException("unexpected object version for result element");
            }
            return new org.geotoolkit.observation.xml.v100.MeasurementType(name, 
                                                                           definition, 
                                                                           (org.geotoolkit.gml.xml.v311.FeaturePropertyType)sampledFeature,
                                                                           (org.geotoolkit.swe.xml.v101.PhenomenonType)phen, 
                                                                           procedure , 
                                                                           (org.geotoolkit.observation.xml.v100.MeasureType)result, 
                                                                           (org.geotoolkit.gml.xml.v311.AbstractTimeGeometricPrimitiveType)time);
        } else if ("2.0.0".equals(version)) {
            if (sampledFeature != null && !(sampledFeature instanceof org.geotoolkit.gml.xml.v321.FeaturePropertyType)) {
                throw new IllegalArgumentException("unexpected object version for sampled feature element");
            }
            if (time != null && !(time instanceof org.geotoolkit.gml.xml.v321.TimePeriodType)) {
                throw new IllegalArgumentException("unexpected object version for time element");
            }
            if (phen != null && !(phen instanceof org.geotoolkit.swe.xml.Phenomenon)) {
                throw new IllegalArgumentException("unexpected object version for phenomenon element");
            }
            if (result != null && !(result instanceof org.geotoolkit.gml.xml.v321.MeasureType)) {
                throw new IllegalArgumentException("unexpected object version for result element");
            }
           return new org.geotoolkit.observation.xml.v200.OMObservationType(id,
                                                                            name, 
                                                                            "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement", 
                                                                            (org.geotoolkit.gml.xml.v321.TimePeriodType)time,
                                                                            procedure,
                                                                            ((org.geotoolkit.swe.xml.Phenomenon)phen).getName(),
                                                                            (org.geotoolkit.gml.xml.v321.FeaturePropertyType)sampledFeature,
                                                                            result);
        } else {
            throw new IllegalArgumentException("unexpected sos version number:" + version);
        }
    }
    
    public static Observation cloneObervation(final String version, final Observation observation) {
        if (version.equals("1.0.0")) {
            if (observation instanceof org.geotoolkit.observation.xml.v100.MeasurementType) {
                return new org.geotoolkit.observation.xml.v100.MeasurementType((org.geotoolkit.observation.xml.v100.MeasurementType)observation);
            } else if (observation instanceof org.geotoolkit.observation.xml.v100.ObservationType) {
                return new org.geotoolkit.observation.xml.v100.ObservationType((org.geotoolkit.observation.xml.v100.ObservationType)observation);
            } else {
                throw new IllegalArgumentException("unexpected observation element version");
            }
        } else if (version.equals("2.0.0")) {
            if (observation instanceof org.geotoolkit.observation.xml.v200.OMObservationType) {
                return new org.geotoolkit.observation.xml.v200.OMObservationType((org.geotoolkit.observation.xml.v200.OMObservationType)observation);
            } else {
                throw new IllegalArgumentException("unexpected observation element version");
            }
        } else {
            throw new IllegalArgumentException("unexpected O&M version number:" + version);
        }
    }
}
