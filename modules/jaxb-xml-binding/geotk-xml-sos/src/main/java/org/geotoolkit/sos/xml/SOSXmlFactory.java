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
import java.util.List;
import org.geotoolkit.gml.xml.Envelope;
import org.geotoolkit.gml.xml.GMLXmlFactory;
import org.geotoolkit.ows.xml.AbstractOperationsMetadata;
import org.geotoolkit.ows.xml.AbstractServiceIdentification;
import org.geotoolkit.ows.xml.AbstractServiceProvider;
import org.geotoolkit.ows.xml.AcceptFormats;
import org.geotoolkit.ows.xml.AcceptVersions;
import org.geotoolkit.ows.xml.Range;
import org.geotoolkit.ows.xml.Sections;
import org.geotoolkit.swes.xml.InsertSensorResponse;
import org.opengis.observation.Observation;
import org.opengis.observation.ObservationCollection;

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
            final List<org.geotoolkit.sos.xml.v200.ObservationOfferingType> off200 = new ArrayList<org.geotoolkit.sos.xml.v200.ObservationOfferingType>();
            for (ObservationOffering off : offerings ) {
                if (off instanceof org.geotoolkit.sos.xml.v200.ObservationOfferingType) {
                    off200.add((org.geotoolkit.sos.xml.v200.ObservationOfferingType)off);
                } else {
                    throw new IllegalArgumentException("unexpected object version for offering element");
                }
            }
            return new org.geotoolkit.sos.xml.v200.ContentsType(off200);
        } else if ("1.0.0".equals(version)) {
            final List<org.geotoolkit.sos.xml.v100.ObservationOfferingType> off100 = new ArrayList<org.geotoolkit.sos.xml.v100.ObservationOfferingType>();
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
            final AbstractOperationsMetadata operationsMetadata, final String updateSequence, final FilterCapabilities filterCapabilities, final Contents contents) {
        
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
            return new org.geotoolkit.sos.xml.v200.CapabilitiesType((org.geotoolkit.ows.xml.v110.ServiceIdentification)serviceIdentification, 
                                                                    (org.geotoolkit.ows.xml.v110.ServiceProvider)serviceProvider, 
                                                                    (org.geotoolkit.ows.xml.v110.OperationsMetadata)operationsMetadata, 
                                                                    version, 
                                                                    updateSequence, 
                                                                    (org.geotoolkit.sos.xml.v200.FilterCapabilities)filterCapabilities,
                                                                    (org.geotoolkit.sos.xml.v200.ContentsType)contents);
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
    
    public static ObservationCollection buildObservationCollection(final String version, final String id, final Envelope bounds, final List<Observation> observations) {
        if ("2.0.0".equals(version)) {
            final List<org.geotoolkit.observation.xml.v200.OMObservationType> obs200 = new ArrayList<org.geotoolkit.observation.xml.v200.OMObservationType>();
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
            final List<org.geotoolkit.observation.xml.v100.ObservationType> obs100 = new ArrayList<org.geotoolkit.observation.xml.v100.ObservationType>();
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
    
    public static InsertSensorResponse buildInsertSensorResponse(final String version, final String assignedProcedure, final String assignedOffering) {
        if ("2.0.0".equals(version)) {
            return new org.geotoolkit.swes.xml.v200.InsertSensorResponseType(assignedProcedure, assignedOffering);
        } else if ("1.0.0".equals(version)) {
            return new org.geotoolkit.sos.xml.v100.RegisterSensorResponse(assignedProcedure);
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
    
    public static Envelope buildEnvelope(final String version, final double minx, final double miny, final double maxx, final double maxy, final String srs) {
        if ("2.0.0".equals(version)) {
            return GMLXmlFactory.buildEnvelope("3.2.1", minx, miny, maxx, maxy, srs);
        } else if ("1.0.0".equals(version)) {
            return GMLXmlFactory.buildEnvelope("3.1.1", minx, miny, maxx, maxy, srs);
        } else {
            throw new IllegalArgumentException("unexpected gml version number:" + version);
        }
    }
}
