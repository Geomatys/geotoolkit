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
import org.geotoolkit.ows.xml.AbstractOperationsMetadata;
import org.geotoolkit.ows.xml.AbstractServiceIdentification;
import org.geotoolkit.ows.xml.AbstractServiceProvider;
import org.geotoolkit.ows.xml.AcceptFormats;
import org.geotoolkit.ows.xml.AcceptVersions;
import org.geotoolkit.ows.xml.Range;
import org.geotoolkit.ows.xml.Sections;

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
            if (contents != null && !(contents instanceof org.geotoolkit.sos.xml.v200.CapabilitiesType.Contents)) {
                throw new IllegalArgumentException("unexpected object version for contents element");
            }
            return new org.geotoolkit.sos.xml.v200.CapabilitiesType((org.geotoolkit.ows.xml.v110.ServiceIdentification)serviceIdentification, 
                                                                    (org.geotoolkit.ows.xml.v110.ServiceProvider)serviceProvider, 
                                                                    (org.geotoolkit.ows.xml.v110.OperationsMetadata)operationsMetadata, 
                                                                    version, 
                                                                    updateSequence, 
                                                                    (org.geotoolkit.sos.xml.v200.FilterCapabilities)filterCapabilities,
                                                                    (org.geotoolkit.sos.xml.v200.CapabilitiesType.Contents)contents);
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
}
