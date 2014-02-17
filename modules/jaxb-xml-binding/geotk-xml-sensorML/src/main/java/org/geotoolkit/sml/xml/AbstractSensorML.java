/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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

import java.util.List;

/**
 * Abstract super class for all the version of a SensorML document.
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
public abstract class AbstractSensorML {

    public abstract List<? extends SMLMember> getMember();

    public abstract String getVersion();

    public abstract List<? extends AbstractKeywords> getKeywords();

    public abstract List<? extends AbstractIdentification> getIdentification();

    public abstract List<? extends AbstractClassification> getClassification();

    public abstract AbstractValidTime getValidTime();

    public abstract AbstractSecurityConstraint getSecurityConstraint();

    public abstract List<? extends AbstractLegalConstraint> getLegalConstraint();

    public abstract List<? extends AbstractCharacteristics> getCharacteristics();

    public abstract List<? extends AbstractCapabilities> getCapabilities();

    public abstract List<? extends AbstractContact> getContact();

    public abstract List<? extends AbstractDocumentation> getDocumentation();

    public abstract List<? extends AbstractHistory> getHistory();

}
