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

package org.geotoolkit.sml.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.apache.sis.util.logging.Logging;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class SensorMLUtilities {
 
    private static final Logger LOGGER = Logging.getLogger(SensorMLUtilities.class);
    
    public static String getSensorMLType(final AbstractSensorML sml) {
        if (sml.getMember() != null)  {
            //assume only one member
            for (SMLMember member : sml.getMember()) {
                final AbstractProcess process = member.getRealProcess();
                return getSensorMLType(process);
            }
        }
        return "unknow";
    }
    
    public static String getSensorMLType(final AbstractProcess process) {
        if (process instanceof System) {
            return "System";
        } else if (process instanceof AbstractProcessChain) {
            return "ProcessChain";
        } else if (process instanceof Component) {
            return "Component";
        } else if (process instanceof AbstractDataSource) {
            return "DataSource";
        } else if (process instanceof AbstractProcessModel) {
            return "ProcessModel";
        }
        return "unknow";
    }
    
    public static List<String> getChildrenIdentifiers(final AbstractSensorML sml) {
        if (sml.getMember() != null)  {
            //assume only one member
            for (SMLMember member : sml.getMember()) {
                final AbstractProcess process = member.getRealProcess();
                return getChildren(process);
            }
        }
        return new ArrayList<>();
    }
    
    public static List<String> getChildren(final AbstractProcess process) {
        final List<String> results = new ArrayList<>();
        if (process instanceof System) {
            final System s = (System) process;
            final AbstractComponents compos = s.getComponents();
            if (compos != null && compos.getComponentList() != null) {
                for (ComponentProperty cp : compos.getComponentList().getComponent()){
                    if (cp.getHref() != null) {
                        results.add(cp.getHref());
                    } else if (cp.getAbstractProcess()!= null) {
                        results.add(getSmlID(cp.getAbstractProcess()));
                    } else {
                        LOGGER.warning("SML system component has no href or embedded object");
                    }
                }
            }
        } else if (process instanceof AbstractProcessChain) {
            final AbstractProcessChain s = (AbstractProcessChain) process;
            final AbstractComponents compos = s.getComponents();
            if (compos != null && compos.getComponentList() != null) {
                for (ComponentProperty cp : compos.getComponentList().getComponent()){
                    if (cp.getHref() != null) {
                        results.add(cp.getHref());
                    } else if (cp.getAbstractProcess()!= null) {
                        results.add(getSmlID(cp.getAbstractProcess()));
                    } else {
                        LOGGER.warning("SML system component has no href or embedded object");
                    }
                }
            }
        }
        return results;
    }
    
    public static String getSmlID(final AbstractSensorML sensor) {
        if (sensor != null && sensor.getMember().size() > 0) {
            final AbstractProcess process = sensor.getMember().get(0).getRealProcess();
            return getSmlID(process);
        }
        return "unknow_identifier";
    }
    
    public static String getSmlID(final AbstractProcess process) {
        final List<? extends AbstractIdentification> idents = process.getIdentification();

        for(AbstractIdentification ident : idents) {
            if (ident.getIdentifierList() != null) {
                for (AbstractIdentifier identifier: ident.getIdentifierList().getIdentifier()) {
                    if (("uniqueID".equals(identifier.getName()) && identifier.getTerm() != null) ||
                        (identifier.getTerm() != null && identifier.getTerm().getDefinition() != null && identifier.getTerm().getDefinition().toString().equals("urn:ogc:def:identifier:OGC:uniqueID"))) {
                        return identifier.getTerm().getValue();
                    }
                }
            }
        }

        // else look for simple id mark
        if (process.getId() != null) {
            return process.getId();
        }
        return "unknow_identifier";
    }

}
