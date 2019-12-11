/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

package org.geotoolkit.sos.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.xml.namespace.QName;
import org.geotoolkit.gml.xml.AbstractTimePosition;
import org.geotoolkit.gml.xml.Envelope;
import org.geotoolkit.gml.xml.FeatureCollection;
import org.geotoolkit.gml.xml.FeatureProperty;
import org.geotoolkit.gml.xml.GMLXmlFactory;
import org.geotoolkit.gml.xml.LineString;
import org.geotoolkit.gml.xml.Point;
import org.geotoolkit.gml.xml.Polygon;
import org.geotoolkit.gml.xml.TimeIndeterminateValueType;
import org.geotoolkit.gml.xml.v321.ReferenceType;
import org.geotoolkit.observation.xml.OMXmlFactory;
import org.geotoolkit.observation.xml.v100.ProcessType;
import org.geotoolkit.observation.xml.v200.OMProcessPropertyType;
import org.geotoolkit.ows.xml.AbstractOperationsMetadata;
import org.geotoolkit.ows.xml.AbstractServiceIdentification;
import org.geotoolkit.ows.xml.AbstractServiceProvider;
import org.geotoolkit.ows.xml.AcceptFormats;
import org.geotoolkit.ows.xml.AcceptVersions;
import org.geotoolkit.ows.xml.OWSXmlFactory;
import org.geotoolkit.ows.xml.Range;
import org.geotoolkit.ows.xml.Sections;
import org.geotoolkit.swe.xml.AbstractBoolean;
import org.geotoolkit.swe.xml.AbstractDataComponent;
import org.geotoolkit.swe.xml.AbstractDataRecord;
import org.geotoolkit.swe.xml.AbstractEncoding;
import org.geotoolkit.swe.xml.AbstractText;
import org.geotoolkit.swe.xml.AbstractTime;
import org.geotoolkit.swe.xml.AnyScalar;
import org.geotoolkit.swe.xml.DataArray;
import org.geotoolkit.swe.xml.DataArrayProperty;
import org.geotoolkit.swe.xml.Phenomenon;
import org.geotoolkit.swe.xml.PhenomenonProperty;
import org.geotoolkit.swe.xml.Quantity;
import org.geotoolkit.swe.xml.SweXmlFactory;
import org.geotoolkit.swe.xml.TextBlock;
import org.geotoolkit.swe.xml.UomProperty;
import org.geotoolkit.swe.xml.v101.PhenomenonPropertyType;
import org.geotoolkit.swes.xml.DeleteSensor;
import org.geotoolkit.swes.xml.DeleteSensorResponse;
import org.geotoolkit.swes.xml.DescribeSensor;
import org.geotoolkit.swes.xml.InsertSensorResponse;
import org.opengis.filter.Filter;
import org.opengis.filter.spatial.BBOX;
import org.opengis.filter.temporal.After;
import org.opengis.filter.temporal.Before;
import org.opengis.filter.temporal.During;
import org.opengis.filter.temporal.TEquals;
import org.opengis.geometry.DirectPosition;
import org.opengis.observation.CompositePhenomenon;
import org.opengis.observation.Observation;
import org.opengis.observation.ObservationCollection;
import org.opengis.observation.sampling.SamplingFeature;
import org.opengis.observation.Process;
import org.opengis.temporal.Instant;
import org.opengis.temporal.Period;
import org.opengis.temporal.TemporalGeometricPrimitive;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class SOSXmlFactory {

    public static GetCapabilities buildGetCapabilities(final String version, final String service) {
        return buildGetCapabilities(version, null, null, null, null, service);
    }

    public static GetCapabilities buildGetCapabilities(final String version, final AcceptVersions versions, final Sections sections, final AcceptFormats formats, final String updateSequence, final String service) {
        if (versions != null && !(versions instanceof org.geotoolkit.ows.xml.v110.AcceptVersionsType)) {
            throw new IllegalArgumentException("unexpected object version for AcceptVersion element");
        }
        if (sections != null && !(sections instanceof org.geotoolkit.ows.xml.v110.SectionsType)) {
            throw new IllegalArgumentException("unexpected object version for Sections element");
        }
        if (formats != null && !(formats instanceof org.geotoolkit.ows.xml.v110.AcceptFormatsType)) {
            throw new IllegalArgumentException("unexpected object version for AcceptFormat element");
        }
        if ("2.0.0".equals(version)) {
            return new org.geotoolkit.sos.xml.v200.GetCapabilitiesType((org.geotoolkit.ows.xml.v110.AcceptVersionsType)versions,
                                                                       (org.geotoolkit.ows.xml.v110.SectionsType)sections,
                                                                       (org.geotoolkit.ows.xml.v110.AcceptFormatsType)formats,
                                                                       updateSequence,
                                                                       service);
        } else if ("1.0.0".equals(version)) {
            return new org.geotoolkit.sos.xml.v100.GetCapabilities((org.geotoolkit.ows.xml.v110.AcceptVersionsType)versions,
                                                                   (org.geotoolkit.ows.xml.v110.SectionsType)sections,
                                                                   (org.geotoolkit.ows.xml.v110.AcceptFormatsType)formats,
                                                                   updateSequence,
                                                                   service);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static Contents buildContents(final String version, final List<ObservationOffering> offerings) {
        if ("2.0.0".equals(version)) {
            final List<org.geotoolkit.sos.xml.v200.ObservationOfferingType> off200 = new ArrayList<>();
            for (ObservationOffering off : offerings ) {
                if (off instanceof org.geotoolkit.sos.xml.v200.ObservationOfferingType) {
                    off200.add((org.geotoolkit.sos.xml.v200.ObservationOfferingType)off);
                } else {
                    throw new IllegalArgumentException("unexpected object version for offering element");
                }
            }
            return new org.geotoolkit.sos.xml.v200.ContentsType(off200);
        } else if ("1.0.0".equals(version)) {
            final List<org.geotoolkit.sos.xml.v100.ObservationOfferingType> off100 = new ArrayList<>();
            for (ObservationOffering off : offerings ) {
                if (off instanceof org.geotoolkit.sos.xml.v100.ObservationOfferingType) {
                    off100.add((org.geotoolkit.sos.xml.v100.ObservationOfferingType)off);
                } else {
                    throw new IllegalArgumentException("unexpected object version for offering element");
                }
            }
            return new org.geotoolkit.sos.xml.v100.Contents(off100);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static Range buildRange(final String version, final String minValue, final String maxValue) {
        if ("2.0.0".equals(version) || "1.0.0".equals(version)) {
            return new org.geotoolkit.ows.xml.v110.RangeType(minValue, maxValue);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static Capabilities buildCapabilities(final String version, final String updateSequence) {
        if ("2.0.0".equals(version)) {
            return new org.geotoolkit.sos.xml.v200.CapabilitiesType(version, updateSequence);
        } else if ("1.0.0".equals(version)) {
            return new org.geotoolkit.sos.xml.v100.Capabilities(version, updateSequence);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static Capabilities buildCapabilities(final String version, final AbstractServiceIdentification serviceIdentification, final AbstractServiceProvider serviceProvider,
            final AbstractOperationsMetadata operationsMetadata, final String updateSequence, final FilterCapabilities filterCapabilities, final Contents contents, final List<Object> extension) {

        if (serviceIdentification != null && !(serviceIdentification instanceof org.geotoolkit.ows.xml.v110.ServiceIdentification)) {
            throw new IllegalArgumentException("unexpected object version for serviceIdentification element");
        }
        if (serviceProvider != null && !(serviceProvider instanceof org.geotoolkit.ows.xml.v110.ServiceProvider)) {
            throw new IllegalArgumentException("unexpected object version for serviceProvider element");
        }
        if (operationsMetadata != null && !(operationsMetadata instanceof org.geotoolkit.ows.xml.v110.OperationsMetadata)) {
            throw new IllegalArgumentException("unexpected object version for operationsMetadata element");
        }

        if ("2.0.0".equals(version)) {
            if (filterCapabilities != null && !(filterCapabilities instanceof org.geotoolkit.sos.xml.v200.FilterCapabilities)) {
                throw new IllegalArgumentException("unexpected object version for filterCapabilities element");
            }
            if (contents != null && !(contents instanceof org.geotoolkit.sos.xml.v200.ContentsType)) {
                throw new IllegalArgumentException("unexpected object version for contents element");
            }
            final List<org.geotoolkit.sos.xml.v200.InsertionCapabilitiesPropertyType> ext200 = new ArrayList<>();
            if (extension != null) {
                for (Object obs : extension) {
                    if (obs instanceof org.geotoolkit.sos.xml.v200.InsertionCapabilitiesPropertyType) {
                        ext200.add((org.geotoolkit.sos.xml.v200.InsertionCapabilitiesPropertyType) obs);
                    } else {
                        throw new IllegalArgumentException("unexpected object version for extension element");
                    }
                }
            }

            return new org.geotoolkit.sos.xml.v200.CapabilitiesType((org.geotoolkit.ows.xml.v110.ServiceIdentification)serviceIdentification,
                                                                    (org.geotoolkit.ows.xml.v110.ServiceProvider)serviceProvider,
                                                                    (org.geotoolkit.ows.xml.v110.OperationsMetadata)operationsMetadata,
                                                                    version,
                                                                    updateSequence,
                                                                    (org.geotoolkit.sos.xml.v200.FilterCapabilities)filterCapabilities,
                                                                    (org.geotoolkit.sos.xml.v200.ContentsType)contents,
                                                                    ext200);
        } else if ("1.0.0".equals(version)) {
            if (filterCapabilities != null && !(filterCapabilities instanceof org.geotoolkit.sos.xml.v100.FilterCapabilities)) {
                throw new IllegalArgumentException("unexpected object version for filterCapabilities element");
            }
            if (contents != null && !(contents instanceof org.geotoolkit.sos.xml.v100.Contents)) {
                throw new IllegalArgumentException("unexpected object version for contents element");
            }
            return new org.geotoolkit.sos.xml.v100.Capabilities((org.geotoolkit.ows.xml.v110.ServiceIdentification)serviceIdentification,
                                                                (org.geotoolkit.ows.xml.v110.ServiceProvider)serviceProvider,
                                                                (org.geotoolkit.ows.xml.v110.OperationsMetadata)operationsMetadata,
                                                                version,
                                                                updateSequence,
                                                                (org.geotoolkit.sos.xml.v100.FilterCapabilities)filterCapabilities,
                                                                (org.geotoolkit.sos.xml.v100.Contents)contents);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static InsertObservationResponse buildInsertObservationResponse(final String version, final List<String> assignedObservationIds) {
        if ("2.0.0".equals(version)) {
            return new org.geotoolkit.sos.xml.v200.InsertObservationResponseType(assignedObservationIds);
        } else if ("1.0.0".equals(version)) {
            final String id;
            if (assignedObservationIds != null && !assignedObservationIds.isEmpty()) {
                id = assignedObservationIds.get(0);
            } else {
                id = null;
            }
            return new org.geotoolkit.sos.xml.v100.InsertObservationResponse(id);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static InsertObservation buildInsertObservation(final String version, final List<String> offerings, final String assignedSensorID, final List<Observation> observations) {
        if ("2.0.0".equals(version)) {
            final List<org.geotoolkit.observation.xml.v200.OMObservationType> obs200 = new ArrayList<>();
            if (observations != null) {
                for (Observation obs : observations) {
                    if (obs instanceof org.geotoolkit.observation.xml.v200.OMObservationType) {
                        obs200.add((org.geotoolkit.observation.xml.v200.OMObservationType) obs);
                    } else {
                        throw new IllegalArgumentException("unexpected object version for observation element");
                    }
                }
            }
            return new org.geotoolkit.sos.xml.v200.InsertObservationType(version, offerings, obs200);
        } else if ("1.0.0".equals(version)) {
            org.geotoolkit.observation.xml.v100.ObservationType obs = null;
            if (observations.isEmpty() && observations.get(0) instanceof org.geotoolkit.observation.xml.v100.ObservationType) {
                obs = (org.geotoolkit.observation.xml.v100.ObservationType) observations.get(0);
            }
            return new org.geotoolkit.sos.xml.v100.InsertObservation(version, assignedSensorID, obs);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static ObservationCollection buildGetObservationResponse(final String version, final String id, final Envelope bounds, final List<Observation> observations) {
        if ("2.0.0".equals(version)) {
            final List<org.geotoolkit.observation.xml.v200.OMObservationType> obs200 = new ArrayList<>();
            if (observations != null) {
                for (Observation obs : observations) {
                    if (obs instanceof org.geotoolkit.observation.xml.v200.OMObservationType) {
                        obs200.add((org.geotoolkit.observation.xml.v200.OMObservationType) obs);
                    } else {
                        throw new IllegalArgumentException("unexpected object version for observation element");
                    }
                }
            }
            return new org.geotoolkit.sos.xml.v200.GetObservationResponseType(obs200);
        } else if ("1.0.0".equals(version)) {
            final List<org.geotoolkit.observation.xml.v100.ObservationType> obs100 = new ArrayList<>();
            if (observations != null) {
                for (Observation obs : observations) {
                    if (obs instanceof org.geotoolkit.observation.xml.v100.ObservationType) {
                        obs100.add((org.geotoolkit.observation.xml.v100.ObservationType) obs);
                    } else {
                        throw new IllegalArgumentException("unexpected object version for observation element");
                    }
                }
            }
            if (bounds != null && !(bounds instanceof org.geotoolkit.gml.xml.v311.EnvelopeType)) {
                throw new IllegalArgumentException("unexpected object version for bounds element");
            }
            return new org.geotoolkit.observation.xml.v100.ObservationCollectionType(id,
                                                                                     (org.geotoolkit.gml.xml.v311.EnvelopeType)bounds,
                                                                                     obs100);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static ObservationCollection buildGetObservationByIdResponse(final String version, final String id, final Envelope bounds, final List<Observation> observations) {
        if ("2.0.0".equals(version)) {
            final List<org.geotoolkit.observation.xml.v200.OMObservationType> obs200 = new ArrayList<>();
            if (observations != null) {
                for (Observation obs : observations) {
                    if (obs instanceof org.geotoolkit.observation.xml.v200.OMObservationType) {
                        obs200.add((org.geotoolkit.observation.xml.v200.OMObservationType) obs);
                    } else if (obs != null){
                        throw new IllegalArgumentException("unexpected object version for observation element");
                    }
                }
            }
            return new org.geotoolkit.sos.xml.v200.GetObservationByIdResponseType(obs200);
        } else if ("1.0.0".equals(version)) {
            final List<org.geotoolkit.observation.xml.v100.ObservationType> obs100 = new ArrayList<>();
            if (observations != null) {
                for (Observation obs : observations) {
                    if (obs instanceof org.geotoolkit.observation.xml.v100.ObservationType) {
                        obs100.add((org.geotoolkit.observation.xml.v100.ObservationType) obs);
                    } else if (obs != null){
                        throw new IllegalArgumentException("unexpected object version for observation element");
                    }
                }
            }
            if (bounds != null && !(bounds instanceof org.geotoolkit.gml.xml.v311.EnvelopeType)) {
                throw new IllegalArgumentException("unexpected object version for bounds element");
            }
            return new org.geotoolkit.observation.xml.v100.ObservationCollectionType(id,
                                                                                     (org.geotoolkit.gml.xml.v311.EnvelopeType)bounds,
                                                                                     obs100);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static ObservationCollection buildObservationCollection(final String version, final String nillValue) {
        if ("2.0.0".equals(version)) {
            return new org.geotoolkit.sos.xml.v200.GetObservationResponseType();
        } else if ("1.0.0".equals(version)) {
            return new org.geotoolkit.observation.xml.v100.ObservationCollectionType(nillValue);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static Observation cloneObservation(final String version, final Observation observation) {
        if ("2.0.0".equals(version)) {
            if (!(observation instanceof org.geotoolkit.observation.xml.v200.OMObservationType)) {
                throw new IllegalArgumentException("unexpected object version for observation");
            }
            return new org.geotoolkit.observation.xml.v200.OMObservationType((org.geotoolkit.observation.xml.v200.OMObservationType)observation);
        } else if ("1.0.0".equals(version)) {
            if (!(observation instanceof org.geotoolkit.observation.xml.v100.ObservationType)) {
                throw new IllegalArgumentException("unexpected object version for observation");
            }
            return new org.geotoolkit.observation.xml.v100.ObservationType((org.geotoolkit.observation.xml.v100.ObservationType)observation);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static GetObservationById buildGetObservationById(final String version, final String service, final List<String> observations,
            final QName resultModel, final ResponseModeType responseMode, final String srsName, final String responseFormat, final List<Object> extension) {
        if ("2.0.0".equals(version)) {
            org.geotoolkit.sos.xml.v200.GetObservationByIdType result = new org.geotoolkit.sos.xml.v200.GetObservationByIdType(version, service, observations, extension);
            return result;
        } else if ("1.0.0".equals(version)) {
            String oid = null;
            if (observations != null && !observations.isEmpty()) {
                oid = observations.get(0);
            }
            return new org.geotoolkit.sos.xml.v100.GetObservationById(version, service, oid, responseFormat, resultModel, responseMode, srsName);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static GetResultTemplate buildGetResultTemplate(final String version, final String service, final String offering,
            final String observedProperty, final List<Object> extensions) {
        if ("2.0.0".equals(version)) {
            return new org.geotoolkit.sos.xml.v200.GetResultTemplateType(version, service, offering, observedProperty, extensions);
        } else if ("1.0.0".equals(version)) {
            throw new IllegalArgumentException("GetResultTemplate is not implemented in SOS v100");
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static DeleteSensor buildDeleteSensor(final String version, final String service, final String procedure) {
        if ("2.0.0".equals(version)) {
            return new org.geotoolkit.swes.xml.v200.DeleteSensorType(version, service, procedure);
        } else if ("1.0.0".equals(version)) {
            throw new IllegalArgumentException("deleteSensor is not implemented in SOS v100");
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static GetObservation buildGetObservation(final String version, final String service, final List<String> offering,
            final List<String> observedProperties, final List<String> procedures, final List<String> foid, final String responseFormat,
            final List<Filter> temporalFilter, final Filter spatialFilter, final Filter resultFilter, final QName resultModel,
            final ResponseModeType responseMode, final String srsName) {
        if ("2.0.0".equals(version)) {
            final List<org.geotoolkit.ogc.xml.v200.TemporalOpsType> times = new ArrayList<>();
            for (Filter tf : temporalFilter) {
                if (tf instanceof org.geotoolkit.ogc.xml.v200.TemporalOpsType) {
                    times.add((org.geotoolkit.ogc.xml.v200.TemporalOpsType)tf);
                } else {
                    throw new IllegalArgumentException("unexpected object version for time filter");
                }
            }
            final org.geotoolkit.ogc.xml.v200.SpatialOpsType spa;
            if (spatialFilter != null && !(spatialFilter instanceof org.geotoolkit.ogc.xml.v200.SpatialOpsType)) {
                throw new IllegalArgumentException("unexpected object version for spatial filter");
            } else {
                spa = (org.geotoolkit.ogc.xml.v200.SpatialOpsType)spatialFilter;
            }
            return new org.geotoolkit.sos.xml.v200.GetObservationType(version, service, offering, times, procedures, observedProperties, spa, foid, responseFormat);
        } else if ("1.0.0".equals(version)) {
            String offName = null;
            if (!offering.isEmpty()) {
                offName = offering.get(0);
            }
            final List<org.geotoolkit.sos.xml.v100.EventTime> eventTime = new ArrayList<>();
            for (Filter filter : temporalFilter) {
                if (filter instanceof org.geotoolkit.ogc.xml.v110.TemporalOpsType) {
                    final org.geotoolkit.sos.xml.v100.EventTime time = new org.geotoolkit.sos.xml.v100.EventTime();
                    time.setFilter(filter);
                    eventTime.add(time);
                } else {
                    throw new IllegalArgumentException("unexpected object version for temporal filter element");
                }
            }
            org.geotoolkit.sos.xml.v100.GetObservation.FeatureOfInterest foiFilter = null;
            if (spatialFilter != null) {
                if (spatialFilter instanceof org.geotoolkit.ogc.xml.v110.BBOXType) {
                    foiFilter = new org.geotoolkit.sos.xml.v100.GetObservation.FeatureOfInterest((org.geotoolkit.ogc.xml.v110.BBOXType) spatialFilter);
                } else  {
                     throw new IllegalArgumentException("unexpected object version for spatial filter element");
                }
            } else if (foid != null && !foid.isEmpty()) {
                foiFilter = new org.geotoolkit.sos.xml.v100.GetObservation.FeatureOfInterest(foid);
            }

            org.geotoolkit.sos.xml.v100.GetObservation.Result result = null;
            if (resultFilter != null) {
                if (resultFilter instanceof org.geotoolkit.ogc.xml.v110.ComparisonOpsType) {
                    result = new org.geotoolkit.sos.xml.v100.GetObservation.Result((org.geotoolkit.ogc.xml.v110.ComparisonOpsType)resultFilter);
                } else {
                    throw new IllegalArgumentException("unexpected object version for result filter element");
                }
            }
            return new org.geotoolkit.sos.xml.v100.GetObservation(version, offName, eventTime, procedures, observedProperties, foiFilter, result, responseFormat, resultModel, responseMode, srsName);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static DeleteSensorResponse buildDeleteSensorResponse(final String version, final String deletedProcedure) {
        if ("2.0.0".equals(version)) {
            return new org.geotoolkit.swes.xml.v200.DeleteSensorResponseType(deletedProcedure);
        } else if ("1.0.0".equals(version)) {
            throw new IllegalArgumentException("deleteSensor is not implemented in SOS v100");
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static DescribeSensor buildDescribeSensor(final String version, final String service, final String procedure, final String outputFormat) {
        if ("2.0.0".equals(version)) {
            return new org.geotoolkit.swes.xml.v200.DescribeSensorType(version, service, procedure, outputFormat);
        } else if ("1.0.0".equals(version)) {
            return new org.geotoolkit.sos.xml.v100.DescribeSensor(version, service, procedure, outputFormat);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static GetFeatureOfInterest buildGetFeatureOfInterest(final String version, final String service, final List<String> featureId) {
        if ("2.0.0".equals(version)) {
            return new org.geotoolkit.sos.xml.v200.GetFeatureOfInterestType(version, service, featureId);
        } else if ("1.0.0".equals(version)) {
            return new org.geotoolkit.sos.xml.v100.GetFeatureOfInterest(version, service, featureId);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static GetFeatureOfInterest buildGetFeatureOfInterest(final String version, final String service, final List<String> featureId,
            final List<String> observedProperties, final List<String> procedures, final Filter spatialFilter, final List<Object> extension) {
        if ("2.0.0".equals(version)) {
            org.geotoolkit.sos.xml.v200.GetFeatureOfInterestType result = new org.geotoolkit.sos.xml.v200.GetFeatureOfInterestType(version, service, observedProperties, procedures, featureId, spatialFilter);
            result.setExtension(extension);
            return result;
        } else if ("1.0.0".equals(version)) {
            return new org.geotoolkit.sos.xml.v100.GetFeatureOfInterest(version, service, featureId, spatialFilter);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static InsertSensorResponse buildInsertSensorResponse(final String version, final String assignedProcedure, final String assignedOffering) {
        if ("2.0.0".equals(version)) {
            return new org.geotoolkit.swes.xml.v200.InsertSensorResponseType(assignedProcedure, assignedOffering);
        } else if ("1.0.0".equals(version)) {
            return new org.geotoolkit.sos.xml.v100.RegisterSensorResponse(assignedProcedure);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static GetResult buildGetResult(final String version, final String service, final String offering, final String observedProperty,
            final List<String> featureOfInterest, final Filter spatialFilter, final List<Filter> temporalFilter, final List<Object> extension) {
        if ("2.0.0".equals(version)) {
            final List<org.geotoolkit.ogc.xml.v200.TemporalOpsType> temps = new ArrayList<>();
            if (temporalFilter != null) {
                for (Filter tmp : temporalFilter) {
                    if (!(tmp instanceof org.geotoolkit.ogc.xml.v200.TemporalOpsType)) {
                        throw new IllegalArgumentException("unexpected object version for temporal filter element");
                    }
                    temps.add((org.geotoolkit.ogc.xml.v200.TemporalOpsType)tmp);
                }
            }
            if (spatialFilter != null && !(spatialFilter instanceof org.geotoolkit.ogc.xml.v200.SpatialOpsType)) {
                throw new IllegalArgumentException("unexpected object version for spatial filter element");
            }
            final org.geotoolkit.ogc.xml.v200.SpatialOpsType spa = (org.geotoolkit.ogc.xml.v200.SpatialOpsType)spatialFilter;
            org.geotoolkit.sos.xml.v200.GetResultType result = new org.geotoolkit.sos.xml.v200.GetResultType(version, service, offering, observedProperty, temps, spa, featureOfInterest);
            result.setExtension(extension);
            return result;
        } else if ("1.0.0".equals(version)) {
            throw new IllegalArgumentException("GetResult KVP is not implemented in SOS v100");
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static GetResult buildGetResult(final String version, final String templateID, final List<Filter> temporalFilter) {
        if ("2.0.0".equals(version)) {
            throw new IllegalArgumentException("GetResult for template is not implemented in SOS v200");
        } else if ("1.0.0".equals(version)) {
            final List<org.geotoolkit.sos.xml.v100.EventTime> eventTime = new ArrayList<>();
            for (Filter filter : temporalFilter) {
                if (filter instanceof org.geotoolkit.ogc.xml.v110.TemporalOpsType) {
                    final org.geotoolkit.sos.xml.v100.EventTime time = new org.geotoolkit.sos.xml.v100.EventTime();
                    time.setFilter(filter);
                    eventTime.add(time);
                } else {
                    throw new IllegalArgumentException("unexpected object version for temporal filter element");
                }
            }
            return new org.geotoolkit.sos.xml.v100.GetResult(templateID, eventTime, version);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static GetResultResponse buildGetResultResponse(final String version, final Object result, final String rs) {
        if ("2.0.0".equals(version)) {
            return new org.geotoolkit.sos.xml.v200.GetResultResponseType(result);
        } else if ("1.0.0".equals(version)) {
            if (result != null && !(result instanceof String)) {
                throw new IllegalArgumentException("unexpected object version for result element");
            }
            return new org.geotoolkit.sos.xml.v100.GetResultResponse((String)result, rs);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static Envelope buildEnvelope(final String version, final String id, final double minx, final double miny, final double maxx, final double maxy, final String srs) {
        if ("2.0.0".equals(version)) {
            return GMLXmlFactory.buildEnvelope("3.2.1", id, minx, miny, maxx, maxy, srs);
        } else if ("1.0.0".equals(version)) {
            return GMLXmlFactory.buildEnvelope("3.1.1", id, minx, miny, maxx, maxy, srs);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static Period buildTimePeriod(final String version, final String id, final String dateBegin, final String dateEnd) {
        if ("2.0.0".equals(version)) {
            return GMLXmlFactory.createTimePeriod("3.2.1", id, dateBegin, dateEnd);
        } else if ("1.0.0".equals(version)) {
            return GMLXmlFactory.createTimePeriod("3.1.1", id, dateBegin, dateEnd);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static Period buildTimePeriod(final String version, final Date dateBegin, final Date dateEnd) {
        if ("2.0.0".equals(version)) {
            return GMLXmlFactory.createTimePeriod("3.2.1", dateBegin, dateEnd);
        } else if ("1.0.0".equals(version)) {
            return GMLXmlFactory.createTimePeriod("3.1.1", dateBegin, dateEnd);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static Period buildTimePeriod(final String version, final TimeIndeterminateValueType value, final Date dateEnd) {
        if ("2.0.0".equals(version)) {
            return GMLXmlFactory.createTimePeriod("3.2.1", value, dateEnd);
        } else if ("1.0.0".equals(version)) {
            return GMLXmlFactory.createTimePeriod("3.1.1", value, dateEnd);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static Period buildTimePeriod(final String version, final Date dateBegin, final TimeIndeterminateValueType value) {
        if ("2.0.0".equals(version)) {
            return GMLXmlFactory.createTimePeriod("3.2.1", dateBegin, value);
        } else if ("1.0.0".equals(version)) {
            return GMLXmlFactory.createTimePeriod("3.1.1", dateBegin, value);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static Period buildTimePeriod(final String version, final TimeIndeterminateValueType value, final AbstractTimePosition dateEnd) {
        if ("2.0.0".equals(version)) {
            return GMLXmlFactory.createTimePeriod("3.2.1", value, dateEnd);
        } else if ("1.0.0".equals(version)) {
            return GMLXmlFactory.createTimePeriod("3.1.1", value, dateEnd);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static Period buildTimePeriod(final String version, final AbstractTimePosition dateBegin, final TimeIndeterminateValueType value) {
        if ("2.0.0".equals(version)) {
            return GMLXmlFactory.createTimePeriod("3.2.1", dateBegin, value);
        } else if ("1.0.0".equals(version)) {
            return GMLXmlFactory.createTimePeriod("3.1.1", dateBegin, value);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static Period buildTimePeriod(final String version, final AbstractTimePosition dateBegin, final AbstractTimePosition dateEnd) {
        if ("2.0.0".equals(version)) {
            return GMLXmlFactory.createTimePeriod("3.2.1", dateBegin, dateEnd);
        } else if ("1.0.0".equals(version)) {
            return GMLXmlFactory.createTimePeriod("3.1.1", dateBegin, dateEnd);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }


    public static Instant buildTimeInstant(final String version, final AbstractTimePosition date) {
        if ("2.0.0".equals(version)) {
            return GMLXmlFactory.createTimeInstant("3.2.1", date);
        } else if ("1.0.0".equals(version)) {
            return GMLXmlFactory.createTimeInstant("3.1.1", date);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static Instant buildTimeInstant(final String version, final Date date) {
        if ("2.0.0".equals(version)) {
            return GMLXmlFactory.createTimeInstant("3.2.1", date);
        } else if ("1.0.0".equals(version)) {
            return GMLXmlFactory.createTimeInstant("3.1.1", date);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static Instant buildTimeInstant(final String version, final String id, final String date) {
        if ("2.0.0".equals(version)) {
            return GMLXmlFactory.createTimeInstant("3.2.1", id, date);
        } else if ("1.0.0".equals(version)) {
            return GMLXmlFactory.createTimeInstant("3.1.1", id, date);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }

    public static ObservationOffering buildOffering(final String version, final String id, final String name, final String description, final List<String> srsName,
            final TemporalGeometricPrimitive time,  final List<String> procedure, final List<PhenomenonProperty> observedProperties, final List<String> observedPropertiesv200,
            final List<String> featureOfInterest, final List<String> responseFormat, final List<QName> resultModel, final List<String> resultModelV200, final List<ResponseModeType> responseMode,
            final List<String> procedureDescriptionFormat) {
        if ("2.0.0".equals(version)) {
            if (time != null && !(time instanceof org.geotoolkit.gml.xml.v321.TimePeriodType)) {
                throw new IllegalArgumentException("unexpected object version for time element");
            }
            String singleProcedure = null;
            if (!procedure.isEmpty()) {
                singleProcedure = procedure.get(0);
            }
            return new org.geotoolkit.sos.xml.v200.ObservationOfferingType(
                                            id,
                                            id,
                                            name,
                                            description,
                                            null,
                                            (org.geotoolkit.gml.xml.v321.TimePeriodType)time,
                                            singleProcedure,
                                            observedPropertiesv200,
                                            featureOfInterest,
                                            responseFormat,
                                            resultModelV200,
                                            procedureDescriptionFormat);
        } else if ("1.0.0".equals(version)) {
            if (time != null && !(time instanceof org.geotoolkit.gml.xml.v311.AbstractTimeGeometricPrimitiveType)) {
                throw new IllegalArgumentException("unexpected object version for time element");
            }
            final List<org.geotoolkit.swe.xml.v101.PhenomenonPropertyType> phenProp = new ArrayList<>();
            if (observedProperties != null) {
                for (PhenomenonProperty phen : observedProperties) {
                    if (!(phen instanceof org.geotoolkit.swe.xml.v101.PhenomenonPropertyType)) {
                         throw new IllegalArgumentException("unexpected object version for phenomenon element");
                    }
                    phenProp.add((org.geotoolkit.swe.xml.v101.PhenomenonPropertyType)phen);
                }
            }
            return new org.geotoolkit.sos.xml.v100.ObservationOfferingType(
                                            id,
                                            name,
                                            description,
                                            srsName,
                                            (org.geotoolkit.gml.xml.v311.AbstractTimeGeometricPrimitiveType)time,
                                            procedure,
                                            phenProp,
                                            featureOfInterest,
                                            responseFormat,
                                            resultModel,
                                            responseMode);
        } else {
            throw new IllegalArgumentException("unexpected gml version number:" + version);
        }
    }

    /**
     * Build the correct featurePropertyType from a sampling feature
     *
     * @param feature
     * @return
     */
    public static FeatureProperty buildFeatureProperty(final String version, final SamplingFeature feature) {
        return OMXmlFactory.buildFeatureProperty(version, feature);
    }

    /**
     * Build the correct featurePropertyType from a sampling feature id
     *
     * @param feature
     * @return
     */
    public static FeatureProperty buildFeatureProperty(final String version, final String featureid) {
        if ("2.0.0".equals(version)) {
            return new org.geotoolkit.gml.xml.v321.FeaturePropertyType(featureid);
        } else if ("1.0.0".equals(version)) {
            return new org.geotoolkit.gml.xml.v311.FeaturePropertyType(featureid);
        } else {
            throw new IllegalArgumentException("unexpected sos version number:" + version);
        }
    }

    public static PhenomenonProperty buildPhenomenonProperty(final String version, final Phenomenon phenomenon) {
        if ("2.0.0".equals(version)) {
            final String href = (phenomenon.getName() != null) ?phenomenon.getName().getCode() :"";
            return new org.geotoolkit.observation.xml.v200.OMObservationType.InternalPhenomenonProperty(new ReferenceType(href), phenomenon);
        } else if ("1.0.0".equals(version)) {
            if (phenomenon != null && !(phenomenon instanceof org.geotoolkit.swe.xml.v101.PhenomenonType)) {
                throw new IllegalArgumentException("unexpected object version for phenomenon component element");
            }
            return new org.geotoolkit.swe.xml.v101.PhenomenonPropertyType((org.geotoolkit.swe.xml.v101.PhenomenonType)phenomenon);
        } else {
            throw new IllegalArgumentException("unexpected sos version number:" + version);
        }
    }

    public static Object buildMeasure(final String version, final String name, final String uom, final Double value) {
        if ("2.0.0".equals(version)) {
            return new org.geotoolkit.gml.xml.v321.MeasureType(uom, value);
        } else if ("1.0.0".equals(version)) {
            return new org.geotoolkit.observation.xml.v100.MeasureType(uom, value.floatValue());
        } else {
            throw new IllegalArgumentException("unexpected sos version number:" + version);
        }
    }

    public static Phenomenon buildPhenomenon(final String version, final String id, final String phenomenonName) {
        if ("2.0.0".equals(version)) {
            return new org.geotoolkit.observation.xml.v200.OMObservationType.InternalPhenomenon(phenomenonName);
        } else if ("1.0.0".equals(version)) {
            return new org.geotoolkit.swe.xml.v101.PhenomenonType(id, phenomenonName);
        } else {
            throw new IllegalArgumentException("unexpected sos version number:" + version);
        }
    }

    public static CompositePhenomenon buildCompositePhenomenon(final String version, final String id, final String phenomenonName, final List<org.opengis.observation.Phenomenon> phenomenons) {
        if ("2.0.0".equals(version)) {
            final List<org.geotoolkit.observation.xml.v200.OMObservationType.InternalPhenomenon> phens = new ArrayList<>();
            for (org.opengis.observation.Phenomenon phen : phenomenons) {
                if (phen != null && !(phen instanceof org.geotoolkit.observation.xml.v200.OMObservationType.InternalPhenomenon)) {
                    throw new IllegalArgumentException("unexpected object version for phenomenon component element");
                }
                phens.add((org.geotoolkit.observation.xml.v200.OMObservationType.InternalPhenomenon)phen);
            }
            return new org.geotoolkit.observation.xml.v200.OMObservationType.InternalCompositePhenomenon(phenomenonName, phens);
        } else if ("1.0.0".equals(version)) {
            final List<org.geotoolkit.swe.xml.v101.PhenomenonType> phens = new ArrayList<>();
            for (org.opengis.observation.Phenomenon phen : phenomenons) {
                if (phen != null && !(phen instanceof org.geotoolkit.swe.xml.v101.PhenomenonType)) {
                    throw new IllegalArgumentException("unexpected object version for phenomenon component element");
                }
                phens.add((org.geotoolkit.swe.xml.v101.PhenomenonType)phen);
            }
            return new org.geotoolkit.swe.xml.v101.CompositePhenomenonType(id, phenomenonName, null, null, phens);
        } else {
            throw new IllegalArgumentException("unexpected sos version number:" + version);
        }
    }

    public static  org.geotoolkit.sampling.xml.SamplingFeature buildSamplingFeature(final String version, final String id, final String name, final String description, final FeatureProperty sampledFeature) {
        if ("1.0.0".equals(version)) {
            if (sampledFeature != null && !(sampledFeature instanceof org.geotoolkit.gml.xml.v311.FeaturePropertyType)) {
                throw new IllegalArgumentException("unexpected object version for sampled feature element");
            }
            return new org.geotoolkit.sampling.xml.v100.SamplingFeatureType(id, name, description,
                                                                          (org.geotoolkit.gml.xml.v311.FeaturePropertyType)sampledFeature);
        } else if ("2.0.0".equals(version)) {
            if (sampledFeature != null && !(sampledFeature instanceof org.geotoolkit.gml.xml.v321.FeaturePropertyType)) {
                throw new IllegalArgumentException("unexpected object version for sampled feature element");
            }
            return new org.geotoolkit.sampling.xml.v200.SFSamplingFeatureType(id, name, description, "http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingPoint",
                                                                          (org.geotoolkit.gml.xml.v321.FeaturePropertyType)sampledFeature,
                                                                          null);
        } else {
            throw new IllegalArgumentException("unexpected sos version number:" + version);
        }
    }

    /**
     * Build the correct featurePropertyType from a sampling feature id
     *
     * @param feature
     * @return
     */
    public static  org.geotoolkit.sampling.xml.SamplingFeature buildSamplingPoint(final String version, final String id, final String name, final String description, final FeatureProperty sampledFeature,
                              final Point location) {
        if ("1.0.0".equals(version)) {
            if (sampledFeature != null && !(sampledFeature instanceof org.geotoolkit.gml.xml.v311.FeaturePropertyType)) {
                throw new IllegalArgumentException("unexpected object version for sampled feature element");
            }
            if (location != null && !(location instanceof org.geotoolkit.gml.xml.v311.PointType)) {
                throw new IllegalArgumentException("unexpected object version for location element");
            }
            return new org.geotoolkit.sampling.xml.v100.SamplingPointType(id, name, description,
                                                                          (org.geotoolkit.gml.xml.v311.FeaturePropertyType)sampledFeature,
                                                                          (org.geotoolkit.gml.xml.v311.PointType)location);
        } else if ("2.0.0".equals(version)) {
            if (sampledFeature != null && !(sampledFeature instanceof org.geotoolkit.gml.xml.v321.FeaturePropertyType)) {
                throw new IllegalArgumentException("unexpected object version for sampled feature element");
            }
            if (location != null && !(location instanceof org.geotoolkit.gml.xml.v321.PointType)) {
                throw new IllegalArgumentException("unexpected object version for location element");
            }
            return new org.geotoolkit.samplingspatial.xml.v200.SFSpatialSamplingFeatureType(id, name, description, "http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingPoint",
                                                                          (org.geotoolkit.gml.xml.v321.FeaturePropertyType)sampledFeature,
                                                                          (org.geotoolkit.gml.xml.v321.PointType)location,
                                                                          null);
        } else {
            throw new IllegalArgumentException("unexpected sos version number:" + version);
        }
    }

    public static  org.geotoolkit.sampling.xml.SamplingFeature buildSamplingCurve(final String version, final String id, final String name, final String description, final FeatureProperty sampledFeature,
                              final LineString location, final Double lengthValue, final String uom, final Envelope env) {
        if ("1.0.0".equals(version)) {
            if (sampledFeature != null && !(sampledFeature instanceof org.geotoolkit.gml.xml.v311.FeaturePropertyType)) {
                throw new IllegalArgumentException("unexpected object version for sampled feature element");
            }
            if (location != null && !(location instanceof org.geotoolkit.gml.xml.v311.LineStringType)) {
                throw new IllegalArgumentException("unexpected object version for location element");
            }
            if (env != null && !(env instanceof org.geotoolkit.gml.xml.v311.EnvelopeType)) {
                throw new IllegalArgumentException("unexpected object version for env element");
            }
            final org.geotoolkit.gml.xml.v311.MeasureType length;
            if (lengthValue != null) {
                length = new org.geotoolkit.gml.xml.v311.MeasureType(lengthValue, uom);
            } else  if (uom != null) {
                length = new org.geotoolkit.gml.xml.v311.MeasureType(0.0, uom);
            } else {
                length = null;
            }
            return new org.geotoolkit.sampling.xml.v100.SamplingCurveType(id, name, description,
                                                                          (org.geotoolkit.gml.xml.v311.FeaturePropertyType)sampledFeature,
                                                                          new org.geotoolkit.gml.xml.v311.CurvePropertyType((org.geotoolkit.gml.xml.v311.LineStringType)location),
                                                                          length,
                                                                          (org.geotoolkit.gml.xml.v311.EnvelopeType)env);
        } else if ("2.0.0".equals(version)) {
            if (sampledFeature != null && !(sampledFeature instanceof org.geotoolkit.gml.xml.v321.FeaturePropertyType)) {
                throw new IllegalArgumentException("unexpected object version for sampled feature element");
            }
            if (location != null && !(location instanceof org.geotoolkit.gml.xml.v321.LineStringType)) {
                throw new IllegalArgumentException("unexpected object version for location element");
            }
            return new org.geotoolkit.samplingspatial.xml.v200.SFSpatialSamplingFeatureType(id, name, description, "http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingCurve",
                                                                          (org.geotoolkit.gml.xml.v321.FeaturePropertyType)sampledFeature,
                                                                          (org.geotoolkit.gml.xml.v321.LineStringType)location,
                                                                          (org.geotoolkit.gml.xml.v321.EnvelopeType)env);
        } else {
            throw new IllegalArgumentException("unexpected sos version number:" + version);
        }
    }

    public static  org.geotoolkit.sampling.xml.SamplingFeature buildSamplingPolygon(final String version, final String id, final String name, final String description, final FeatureProperty sampledFeature,
                              final Polygon location, final Double areaValue, final String uom, final Envelope env) {
        if ("1.0.0".equals(version)) {
            if (sampledFeature != null && !(sampledFeature instanceof org.geotoolkit.gml.xml.v311.FeaturePropertyType)) {
                throw new IllegalArgumentException("unexpected object version for sampled feature element");
            }
            if (location != null && !(location instanceof org.geotoolkit.gml.xml.v311.PolygonType)) {
                throw new IllegalArgumentException("unexpected object version for location element");
            }
            if (env != null && !(env instanceof org.geotoolkit.gml.xml.v311.EnvelopeType)) {
                throw new IllegalArgumentException("unexpected object version for env element");
            }
            final org.geotoolkit.gml.xml.v311.MeasureType length;
            if (areaValue != null) {
                length = new org.geotoolkit.gml.xml.v311.MeasureType(areaValue, uom);
            } else {
                length = new org.geotoolkit.gml.xml.v311.MeasureType(0.0, uom);
            }
            return new org.geotoolkit.sampling.xml.v100.SamplingSurfaceType(id, name, description,
                                                                          (org.geotoolkit.gml.xml.v311.FeaturePropertyType)sampledFeature,
                                                                          new org.geotoolkit.gml.xml.v311.SurfacePropertyType((org.geotoolkit.gml.xml.v311.PolygonType)location),
                                                                          length,
                                                                          (org.geotoolkit.gml.xml.v311.EnvelopeType)env);
        } else if ("2.0.0".equals(version)) {
            if (sampledFeature != null && !(sampledFeature instanceof org.geotoolkit.gml.xml.v321.FeaturePropertyType)) {
                throw new IllegalArgumentException("unexpected object version for sampled feature element");
            }
            if (location != null && !(location instanceof org.geotoolkit.gml.xml.v321.PolygonType)) {
                throw new IllegalArgumentException("unexpected object version for location element");
            }
            return new org.geotoolkit.samplingspatial.xml.v200.SFSpatialSamplingFeatureType(id, name, description, "http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingCurve",
                                                                          (org.geotoolkit.gml.xml.v321.FeaturePropertyType)sampledFeature,
                                                                          (org.geotoolkit.gml.xml.v321.PolygonType)location,
                                                                          (org.geotoolkit.gml.xml.v321.EnvelopeType)env);
        } else {
            throw new IllegalArgumentException("unexpected sos version number:" + version);
        }
    }


    public static FeatureCollection buildFeatureCollection(final String version, final String id, final String name, final String description,
            final List<FeatureProperty> features) {
        if ("2.0.0".equals(version)) {
            return GMLXmlFactory.createFeatureCollection("3.2.1", id, name, description, features);
        } else if ("1.0.0".equals(version)) {
            return GMLXmlFactory.createFeatureCollection("3.1.1", id, name, description, features);
        } else {
            throw new IllegalArgumentException("unexpected sos version number:" + version);
        }
    }

    public static BBOX buildBBOX(final String version, final String propertyName, final double minx, final double miny, final double maxx, final double maxy, final String srs) {
        if ("2.0.0".equals(version)) {
            return new org.geotoolkit.ogc.xml.v200.BBOXType(propertyName, minx, miny, maxx, maxy, srs);
        } else if ("1.0.0".equals(version)) {
            return new org.geotoolkit.ogc.xml.v110.BBOXType(propertyName, minx, miny, maxx, maxy, srs);
        } else {
            throw new IllegalArgumentException("unexpected SOS version number:" + version);
        }
    }

    public static After buildTimeAfter(final String currentVersion, final String propertyName, final Object temporal) {
        if ("2.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v200.TimeAfterType(propertyName, temporal);
        } else if ("1.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v110.TimeAfterType(propertyName, temporal);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static During buildTimeDuring(final String currentVersion, final String propertyName, final Object temporal) {
        if ("2.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v200.TimeDuringType(propertyName, temporal);
        } else if ("1.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v110.TimeDuringType(propertyName, temporal);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static Before buildTimeBefore(final String currentVersion, final String propertyName, final Object temporal) {
        if ("2.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v200.TimeBeforeType(propertyName, temporal);
        } else if ("1.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v110.TimeBeforeType(propertyName, temporal);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static TEquals buildTimeEquals(final String currentVersion, final String propertyName, final Object temporal) {
        if ("2.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v200.TimeEqualsType(propertyName, temporal);
        } else if ("1.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ogc.xml.v110.TimeEqualsType(propertyName, temporal);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static Point buildPoint(final String currentVersion, final String id, final org.opengis.geometry.DirectPosition pos) {
        if ("2.0.0".equals(currentVersion)) {
            return GMLXmlFactory.buildPoint("3.2.1", id, pos);
        } else if ("1.0.0".equals(currentVersion)) {
            return GMLXmlFactory.buildPoint("3.1.1", id, pos);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static Process buildProcess(final String currentVersion, final String href) {
        if ("2.0.0".equals(currentVersion)) {
            return new OMProcessPropertyType(href);
        } else if ("1.0.0".equals(currentVersion)) {
            return new ProcessType(href);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }

    public static DirectPosition buildDirectPosition(final String version, final String srsName, final Integer srsDimension, final List<Double> value) {
        if ("2.0.0".equals(version)) {
            return GMLXmlFactory.buildDirectPosition("3.2.1", srsName, srsDimension, value);
        } else if ("1.0.0".equals(version)) {
            return GMLXmlFactory.buildDirectPosition("3.1.1", srsName, srsDimension, value);
        } else {
            throw new IllegalArgumentException("unexpected SOS version number:" + version);
        }
    }

    public static LineString buildLineString(final String version, final String id, final String srsName, final List<DirectPosition> position) {
        if ("2.0.0".equals(version)) {
            return GMLXmlFactory.buildLineString("3.2.1", id, srsName, position);
        } else if ("1.0.0".equals(version)) {
            return GMLXmlFactory.buildLineString("3.1.1", id, srsName, position);
        } else {
            throw new IllegalArgumentException("unexpected SOS version number:" + version);
        }
    }

    public static TextBlock buildTextBlock(final String version, final String id, final String tokenSeparator, final String blockSeparator, final String decimalSeparator) {
        if ("2.0.0".equals(version)) {
            return SweXmlFactory.createTextBlock("2.0.0", id, tokenSeparator, blockSeparator, decimalSeparator);
        } else if ("1.0.0".equals(version)) {
            return SweXmlFactory.createTextBlock("1.0.1", id, tokenSeparator, blockSeparator, decimalSeparator);
        } else {
            throw new IllegalArgumentException("unexpected SOS version number:" + version);
        }
    }

    public static Quantity buildQuantity(final String version,  final String definition, final UomProperty uom, final Double value) {
        if ("2.0.0".equals(version)) {
            return SweXmlFactory.createQuantity("2.0.0", definition, uom, value);
        } else if ("1.0.0".equals(version)) {
            return SweXmlFactory.createQuantity("1.0.1", definition, uom, value);
        } else {
            throw new IllegalArgumentException("unexpected SOS version number:" + version);
        }
    }

    public static UomProperty buildUomProperty(final String version, final String code, final String href) {
        if ("2.0.0".equals(version)) {
            return SweXmlFactory.createUomProperty("2.0.0", code, href);
        } else if ("1.0.0".equals(version)) {
            return SweXmlFactory.createUomProperty("1.0.1", code, href);
        } else {
            throw new IllegalArgumentException("Unexpected SOS version:" + version);
        }
    }

    public static AbstractTime buildTime(final String version, final String definition, final UomProperty uom) {
        if ("2.0.0".equals(version)) {
            return SweXmlFactory.createTime("2.0.0", definition, uom);
        } else if ("1.0.0".equals(version)) {
            return SweXmlFactory.createTime("1.0.1", definition, uom);
        } else {
            throw new IllegalArgumentException("Unexpected SOS version:" + version);
        }
    }

    public static AbstractBoolean buildBoolean(final String version, final String definition, final Boolean value) {
        if ("2.0.0".equals(version)) {
            return SweXmlFactory.createBoolean("2.0.0", definition, value);
        } else if ("1.0.0".equals(version)) {
            return SweXmlFactory.createBoolean("1.0.1", definition, value);
        } else {
            throw new IllegalArgumentException("Unexpected SOS version:" + version);
        }
    }

    public static AbstractText buildText(final String version, final String definition, final String value) {
        if ("2.0.0".equals(version)) {
            return SweXmlFactory.createText("2.0.0", definition, value);
        } else if ("1.0.0".equals(version)) {
            return SweXmlFactory.createText("1.0.1", definition, value);
        } else {
            throw new IllegalArgumentException("Unexpected SOS version:" + version);
        }
    }

    public static AnyScalar buildAnyScalar(final String version, final String id, final String name, final AbstractDataComponent compo) {
        if ("2.0.0".equals(version)) {
            return SweXmlFactory.buildAnyScalar("2.0.0", id, name, compo);
        } else if ("1.0.0".equals(version)) {
            return SweXmlFactory.buildAnyScalar("1.0.1", id, name, compo);
        } else {
            throw new IllegalArgumentException("Unexpected SOS version:" + version);
        }
    }

    public static AbstractDataRecord buildSimpleDatarecord(final String version,  final String blockid, final String id, final String definition, final boolean fixed, final List<AnyScalar> components) {
        if ("2.0.0".equals(version)) {
            return SweXmlFactory.buildSimpleDataRecord("2.0.0",  blockid, id, definition, fixed, components);
        } else if ("1.0.0".equals(version)) {
            return SweXmlFactory.buildSimpleDataRecord("1.0.1", blockid, id, definition, fixed, components);
        } else {
            throw new IllegalArgumentException("Unexpected SOS version:" + version);
        }
    }

    public static DataArray buildDataArray(final String version, final String id, final int count, final String elementName, final AbstractDataRecord elementType, final AbstractEncoding encoding, final String values) {
        if ("2.0.0".equals(version)) {
            return SweXmlFactory.buildDataArray("2.0.0",  id, count, elementName, elementType, encoding, values);
        } else if ("1.0.0".equals(version)) {
            return SweXmlFactory.buildDataArray("1.0.1",  id, count, elementName, elementType, encoding, values);
        } else {
            throw new IllegalArgumentException("Unexpected SOS version:" + version);
        }
    }

    public static DataArrayProperty buildDataArrayProperty(final String version, final String id, final int count, final String elementName, final AbstractDataRecord elementType, final AbstractEncoding encoding, final String values) {
        if ("2.0.0".equals(version)) {
            return new org.geotoolkit.swe.xml.v200.DataArrayPropertyType((org.geotoolkit.swe.xml.v200.DataArrayType)SweXmlFactory.buildDataArray("2.0.0",  id, count, elementName, elementType, encoding, values));
        } else if ("1.0.0".equals(version)) {
            return new org.geotoolkit.swe.xml.v101.DataArrayPropertyType((org.geotoolkit.swe.xml.v101.DataArrayType)SweXmlFactory.buildDataArray("1.0.1",  id, count, elementName, elementType, encoding, values));
        } else {
            throw new IllegalArgumentException("Unexpected SOS version:" + version);
        }
    }

    public static AcceptVersions buildAcceptVersion(final String currentVersion, final List<String> acceptVersion) {
       return OWSXmlFactory.buildAcceptVersion("1.1.0", acceptVersion);
    }

    public static AcceptFormats buildAcceptFormats(final String currentVersion, final List<String> acceptFormat) {
       return OWSXmlFactory.buildAcceptFormat("1.1.0", acceptFormat);
    }

    public static InsertResultTemplateResponse buildInsertResultTemplateResponse(final String version, final String templateID) {
        if ("2.0.0".equals(version)) {
            return new org.geotoolkit.sos.xml.v200.InsertResultTemplateResponseType(templateID);
        } else if ("1.0.0".equals(version)) {
            throw new IllegalArgumentException("InsertResultTemplateResponse is not supported in 1.0.0");
        } else {
            throw new IllegalArgumentException("Unexpected SOS version:" + version);
        }
    }

    public static InsertResultResponse buildInsertResultResponse(final String version) {
        if ("2.0.0".equals(version)) {
            return new org.geotoolkit.sos.xml.v200.InsertResultResponseType();
        } else if ("1.0.0".equals(version)) {
            throw new IllegalArgumentException("InsertResultResponse is not supported in 1.0.0");
        } else {
            throw new IllegalArgumentException("Unexpected SOS version:" + version);
        }
    }

    public static GetResultTemplateResponse buildGetResultTemplateResponse(final String version, final AbstractDataComponent resultStructure, final AbstractEncoding encoding) {
        if (resultStructure != null && !(resultStructure instanceof org.geotoolkit.swe.xml.v200.AbstractDataComponentType)) {
            throw new IllegalArgumentException("unexpected object version for resultStructure element");
        }
        if (encoding != null && !(encoding instanceof org.geotoolkit.swe.xml.v200.AbstractEncodingType)) {
            throw new IllegalArgumentException("unexpected object version for encoding element");
        }
        if ("2.0.0".equals(version)) {
            return new org.geotoolkit.sos.xml.v200.GetResultTemplateResponseType((org.geotoolkit.swe.xml.v200.AbstractDataComponentType)resultStructure,
                                                                                 (org.geotoolkit.swe.xml.v200.AbstractEncodingType)encoding);
        } else if ("1.0.0".equals(version)) {
            throw new IllegalArgumentException("GetResultTemplateResponse is not supported in 1.0.0");
        } else {
            throw new IllegalArgumentException("Unexpected SOS version:" + version);
        }
    }

    public static ObservationOffering convert(final String version, final ObservationOffering offering) {
        if (version.equals("2.0.0")) {
            if (offering instanceof org.geotoolkit.sos.xml.v100.ObservationOfferingType) {
                return convertTo200(offering);
            } else {
                return offering;
            }
        } else if (version.equals("1.0.0")) {
            if (offering instanceof org.geotoolkit.sos.xml.v200.ObservationOfferingType) {
                return convertTo100(offering);
            } else {
                return offering;
            }
        } else {
            throw new IllegalArgumentException("unexpected SOS version number:" + version);
        }
    }

    private static ObservationOffering convertTo200(final ObservationOffering off) {

        final org.geotoolkit.gml.xml.v321.EnvelopeType env;
        if (off.getObservedArea() != null) {
            env = new org.geotoolkit.gml.xml.v321.EnvelopeType(off.getObservedArea());
        } else {
            env = null;
        }
        final org.geotoolkit.gml.xml.v321.TimePeriodType period;
        if (off.getTime() != null) {
            final org.geotoolkit.gml.xml.v311.TimePeriodType pv100 = (org.geotoolkit.gml.xml.v311.TimePeriodType) off.getTime();
            period = new org.geotoolkit.gml.xml.v321.TimePeriodType(null, pv100.getBeginPosition().getValue(), pv100.getEndPosition().getValue());
        } else {
            period = null;
        }
        final String singleProcedure;
        if (!off.getProcedures().isEmpty()) {
            singleProcedure = off.getProcedures().get(0);
        } else {
            singleProcedure = null;
        }
        final String offName = (off.getName() != null) ? off.getName().getCode() : "";
        return new org.geotoolkit.sos.xml.v200.ObservationOfferingType(
                                           off.getId(),
                                           off.getId(),
                                           offName,
                                           off.getDescription(),
                                           env,
                                           period,
                                           singleProcedure,
                                           off.getObservedProperties(),
                                           off.getFeatureOfInterestIds(),
                                           off.getResponseFormat(),
                                           Arrays.asList("http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Observation"),
                                           Arrays.asList("http://www.opengis.net/sensorML/1.0.1"));
    }

    private static ObservationOffering convertTo100(final ObservationOffering off) {

        final org.geotoolkit.gml.xml.v311.EnvelopeType env;
        if (off.getObservedArea() != null) {
            env = new org.geotoolkit.gml.xml.v311.EnvelopeType(off.getObservedArea());
        } else {
            env = null;
        }
        final org.geotoolkit.gml.xml.v311.TimePeriodType period;
        if (off.getTime() != null) {
            final org.geotoolkit.gml.xml.v321.TimePeriodType pv200 = (org.geotoolkit.gml.xml.v321.TimePeriodType) off.getTime();
            period = new org.geotoolkit.gml.xml.v311.TimePeriodType(pv200.getBeginPosition(), pv200.getEndPosition());
        } else {
            period = null;
        }
        final List<org.geotoolkit.swe.xml.v101.PhenomenonPropertyType> observedProperties = new ArrayList<>();
        for (String ref : off.getObservedProperties())  {
            observedProperties.add(new PhenomenonPropertyType(ref));
        }
        final QName OBSERVATION_QNAME = new QName("http://www.opengis.net/om/1.0", "Observation", "om");
        final QName MEASUREMENT_QNAME = new QName("http://www.opengis.net/om/1.0", "Measurement", "om");

        final String offName = (off.getName() != null) ? off.getName().getCode() : "";
        return new org.geotoolkit.sos.xml.v100.ObservationOfferingType(
                                           off.getId(),
                                           offName,
                                           off.getDescription(),
                                           null,
                                           period,
                                           off.getProcedures(),
                                           observedProperties,
                                           off.getFeatureOfInterestIds(),
                                           off.getResponseFormat(),
                                           Arrays.asList(OBSERVATION_QNAME, MEASUREMENT_QNAME),
                                           Arrays.asList(ResponseModeType.INLINE, ResponseModeType.RESULT_TEMPLATE));
    }

    public static TextBlock getDefaultTextEncoding(final String version) {
        if ("2.0.0".equals(version)) {
            return org.geotoolkit.swe.xml.v200.TextEncodingType.DEFAULT_ENCODING;
        } else if ("1.0.0".equals(version)) {
            return org.geotoolkit.swe.xml.v101.TextBlockType.DEFAULT_ENCODING;
        } else {
            throw new IllegalArgumentException("Unexpected SOS version:" + version);
        }
    }

     public static TextBlock getCsvTextEncoding(final String version) {
        if ("2.0.0".equals(version)) {
            return org.geotoolkit.swe.xml.v200.TextEncodingType.CSV_ENCODING;
        } else if ("1.0.0".equals(version)) {
            return org.geotoolkit.swe.xml.v101.TextBlockType.CSV_ENCODING;
        } else {
            throw new IllegalArgumentException("Unexpected SOS version:" + version);
        }
    }
    public static AnyScalar getDefaultTimeField(final String version) {
        if ("2.0.0".equals(version)) {
            return org.geotoolkit.swe.xml.v200.Field.TIME_FIELD;
        } else if ("1.0.0".equals(version)) {
            return org.geotoolkit.swe.xml.v101.AnyScalarPropertyType.TIME_FIELD;
        } else {
            throw new IllegalArgumentException("Unexpected SOS version:" + version);
        }
    }

    public static String getGMLVersion(final String version) {
        if ("2.0.0".equals(version)) {
            return "3.2.1";
        } else if ("1.0.0".equals(version)) {
            return "3.1.1";
        } else {
            throw new IllegalArgumentException("Unexpected SOS version:" + version);
        }
    }

    public static String getFilterVersion(final String version) {
        if ("2.0.0".equals(version)) {
            return "2.0.0";
        } else if ("1.0.0".equals(version)) {
            return "1.1.0";
        } else {
            throw new IllegalArgumentException("Unexpected SOS version:" + version);
        }
    }
}
