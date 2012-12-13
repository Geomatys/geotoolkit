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
package org.geotoolkit.ows.xml;

import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.inspire.xml.MultiLingualCapabilities;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class OWSXmlFactory {
    
    
    public static AcceptVersions buildAcceptVersion(final String currentVersion, final List<String> acceptVersion) {
        if ("1.1.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v110.AcceptVersionsType(acceptVersion);
        } else if ("1.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v100.AcceptVersionsType(acceptVersion);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }
    
    public static AcceptFormats buildAcceptFormat(final String currentVersion, final List<String> acceptformats) {
        if ("1.1.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v110.AcceptFormatsType(acceptformats);
        } else if ("1.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v100.AcceptFormatsType(acceptformats);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }
    
    public static Sections buildSections(final String currentVersion, final List<String> sections) {
        if ("1.1.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v110.SectionsType(sections);
        } else if ("1.0.0".equals(currentVersion)) {
            return new org.geotoolkit.ows.xml.v100.SectionsType(sections);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }
    
    public static AbstractOperationsMetadata buildOperationsMetadata(final String currentVersion, final List<AbstractOperation> operations, final List<AbstractDomain> parameters,
             final List<AbstractDomain> constraints, final Object extendedCapabilities) {
        if ("1.1.0".equals(currentVersion)) {
            final List<org.geotoolkit.ows.xml.v110.Operation> ops = new ArrayList<org.geotoolkit.ows.xml.v110.Operation>();
            if (operations != null) {
                for (AbstractOperation op : operations) {
                    if (op != null && !(op instanceof org.geotoolkit.ows.xml.v110.Operation)) {
                        throw new IllegalArgumentException("unexpected object version for operation");
                    } else if (op != null) {
                        ops.add((org.geotoolkit.ows.xml.v110.Operation)op);
                    }
                }
            }
            final List<org.geotoolkit.ows.xml.v110.DomainType> params = new ArrayList<org.geotoolkit.ows.xml.v110.DomainType>();
            if (parameters != null) {
                for (AbstractDomain param : parameters) {
                    if (param != null && !(param instanceof org.geotoolkit.ows.xml.v110.DomainType)) {
                        throw new IllegalArgumentException("unexpected object version for parameter");
                    } else if (param != null) {
                        params.add((org.geotoolkit.ows.xml.v110.DomainType)param);
                    }
                }
            }
            final List<org.geotoolkit.ows.xml.v110.DomainType> consts = new ArrayList<org.geotoolkit.ows.xml.v110.DomainType>();
            if (constraints != null) {
                for (AbstractDomain constr : constraints) {
                    if (constr != null && !(constr instanceof org.geotoolkit.ows.xml.v110.DomainType)) {
                        throw new IllegalArgumentException("unexpected object version for constraint");
                    } else if (constr != null) {
                        consts.add((org.geotoolkit.ows.xml.v110.DomainType)constr);
                    }
                }
            }
            return new org.geotoolkit.ows.xml.v110.OperationsMetadata(ops, params, consts, extendedCapabilities);
        } else if ("1.0.0".equals(currentVersion)) {
            final List<org.geotoolkit.ows.xml.v100.Operation> ops = new ArrayList<org.geotoolkit.ows.xml.v100.Operation>();
            if (operations != null) {
                for (AbstractOperation op : operations) {
                    if (op != null && !(op instanceof org.geotoolkit.ows.xml.v100.Operation)) {
                        throw new IllegalArgumentException("unexpected object version for operation");
                    } else if (op != null) {
                        ops.add((org.geotoolkit.ows.xml.v100.Operation)op);
                    }
                }
            }
            final List<org.geotoolkit.ows.xml.v100.DomainType> params = new ArrayList<org.geotoolkit.ows.xml.v100.DomainType>();
            if (parameters != null) {
                for (AbstractDomain param : parameters) {
                    if (param != null && !(param instanceof org.geotoolkit.ows.xml.v100.DomainType)) {
                        throw new IllegalArgumentException("unexpected object version for parameter");
                    } else if (param != null) {
                        params.add((org.geotoolkit.ows.xml.v100.DomainType)param);
                    }
                }
            }
            final List<org.geotoolkit.ows.xml.v100.DomainType> consts = new ArrayList<org.geotoolkit.ows.xml.v100.DomainType>();
            if (constraints != null) {
                for (AbstractDomain constr : constraints) {
                    if (constr != null && !(constr instanceof org.geotoolkit.ows.xml.v100.DomainType)) {
                        throw new IllegalArgumentException("unexpected object version for constraint");
                    } else if (constr != null) {
                        consts.add((org.geotoolkit.ows.xml.v100.DomainType)constr);
                    }
                }
            }
            if (extendedCapabilities != null && !(extendedCapabilities instanceof MultiLingualCapabilities)) {
                throw new IllegalArgumentException("unexpected object type for extendedCapabilities");
            }
            return new org.geotoolkit.ows.xml.v100.OperationsMetadata(ops, params, consts, (MultiLingualCapabilities) extendedCapabilities);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + currentVersion);
        }
    }
}
