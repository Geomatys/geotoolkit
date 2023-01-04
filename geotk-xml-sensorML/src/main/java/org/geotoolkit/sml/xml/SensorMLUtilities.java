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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.opengis.metadata.Identifier;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class SensorMLUtilities {

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.sml.xml");

    public static String getSensorMLType(final AbstractSensorML sml) {
        return getFirstProcess(sml).map(SensorMLUtilities::getSensorMLType).orElse("unknown");
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
        return "unknown";
    }

    public static List<String> getChildrenIdentifiers(final AbstractSensorML sml) {
        return getFirstProcess(sml).map(SensorMLUtilities::getChildren).orElse(new ArrayList<>());
    }

    public static List<String> getChildren(final AbstractProcess process) {
        final List<String> results = new ArrayList<>();
        if (process instanceof System s) {
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
        } else if (process instanceof AbstractProcessChain s) {
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

    public static String getSmlID(final AbstractSensorML sml) {
        return getFirstProcess(sml).map(SensorMLUtilities::getSmlID).orElse("unknown_identifier");
    }

    public static String getSmlID(final AbstractProcess process) {
        final List<? extends AbstractIdentification> idents = process.getIdentification();

        for (AbstractIdentification ident : idents) {
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
        return "unknown_identifier";
    }

    /**
     * Return the position of a sensor.
     *
     * @param sensor
     * @return
     */
    public static Optional<AbstractGeometry> getSensorPosition(final AbstractSensorML sensor) {
        return getFirstProcess(sensor).flatMap(SensorMLUtilities::getSensorPosition);
    }

    public static Optional<AbstractGeometry> getSensorPosition(final AbstractProcess process) {
        if (process instanceof AbstractDerivableComponent component) {
            if (component.getSMLLocation() != null && component.getSMLLocation().getGeometry()!= null) {
                return Optional.of(component.getSMLLocation().getGeometry());
            } else if (component.getPosition() != null &&
                       component.getPosition().getPosition() != null &&
                       component.getPosition().getPosition().getLocation() != null &&
                       component.getPosition().getPosition().getLocation().getVector() != null) {
                final URI crs = component.getPosition().getPosition().getReferenceFrame();
                return Optional.of(component.getPosition().getPosition().getLocation().getVector().getGeometry(crs));
            }
        }
        LOGGER.warning("there is no sensor position in the specified sensorML");
        return Optional.empty();
    }

    public static Optional<String> getOMType(final AbstractSensorML sensor) {
        return getFirstProcess(sensor).flatMap(SensorMLUtilities::getOMType);
    }

    public static Optional<String> getOMType(final AbstractProcess process) {
        var classifs = process.getClassification();
        if (classifs == null || classifs.isEmpty()) return Optional.empty();
        return classifs.stream()
                       .flatMap(cl -> cl.getClassifierList() == null ? Stream.empty() : cl.getClassifierList().getClassifier().stream())
                       .filter(classifier -> "data-type".equals(classifier.getName()) && classifier.getTerm() != null)
                       .map(classifier -> classifier.getTerm().getValue())
                       .findFirst();
    }

    /**
     * Search for the first non-null/available process described by the sensor.
     * If none can be found, return an empty optional.
     */
    public static Optional<AbstractProcess> getFirstProcess(final AbstractSensorML sensor) {
        var members = sensor == null ? null : sensor.getMember();
        if (members == null || members.isEmpty()) return Optional.empty();
        return members.stream()
                      .map(SMLMember::getRealProcess)
                      .filter(Objects::nonNull)
                      .findFirst();
    }

    /**
     * Return the gml name of the first process member in the specified sensorML.
     *
     * @param sensor A sensorML object.
     * @return the code name or {@code Optional.empty()}
     */
    public static Optional<String> getSmlName(final AbstractSensorML sensor) {
        return getFirstProcess(sensor)
                   .map(AbstractProcess::getName)
                   .map(Identifier::getCode);
    }

    /**
     * Return the gml description of the first process member in the specified sensorML.
     *
     * @param sensor A sensorML object.
     * @return the description or {@code Optional.empty()}
     */
    public static Optional<String> getSmlDescription(final AbstractSensorML sensor) {
        return getFirstProcess(sensor).map(AbstractProcess::getDescription);
    }

    /**
     * Return the physical ID of a sensor.
     * This ID is found into a "Identifier" mark with the name 'supervisorCode'
     *
     * @param sensor A SML sensor decription.
     * @return the physical id or {@code Optional.empty()}
     */
    public static Optional<String> getPhysicalID(final AbstractSensorML sensor) {
        return getFirstProcess(sensor).stream()
                                      .flatMap(p -> p.getIdentification().stream())
                                      .flatMap(id -> id.getIdentifierList() == null ? Stream.empty() : id.getIdentifierList().getIdentifier().stream())
                                      .filter(identifier -> "supervisorCode".equals(identifier.getName()) && identifier.getTerm() != null)
                                      .map(identifier -> identifier.getTerm().getValue())
                                      .findFirst();
    }

}
