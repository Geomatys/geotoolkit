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
package org.geotoolkit.sml.xml.v101;

import org.geotoolkit.gml.GMLStandard;
import org.geotoolkit.sml.xml.AbstractProcess;
import org.geotoolkit.sml.xml.AbstractSensorML;
import org.geotoolkit.sml.xml.SMLMember;
import org.geotoolkit.swe.xml.v101.AbstractDataComponentType;
import org.apache.sis.metadata.MetadataStandard;


/**
 * A metadata standard for the {@link org.geotoolkit.sml.xml} package.
 *
 * @author Martin Desruisseaux (Geomatys)
 */
public final class SensorMLStandard extends GMLStandard {
    /**
     * The singleton instance for system and component Sensor ML.
     */
    public static final MetadataStandard SYSTEM, COMPONENT;
    static {
        final Package pck = AbstractSensorML.class.getPackage();
        SYSTEM    = new SensorMLStandard("System SML",    pck, true,  AbstractDataComponentType.STANDARD);
        COMPONENT = new SensorMLStandard("Component SML", pck, false, AbstractDataComponentType.STANDARD);
    }

    /**
     * {@code true} for system Sensor ML, or {@code false} for component Sensor ML.
     */
    private final boolean system;

    /**
     * Constructor for the singleton instance.
     */
    private SensorMLStandard(final String name, final Package pck, final boolean system,
            final MetadataStandard... dependencies)
    {
        super(name, pck, "v101", dependencies);
        this.system = system;
    }

    /**
     * Returns the implementation class for the given interface, or {@code null} if none.
     *
     * @param  <T>  The compile-time {@code type}.
     * @param  type The interface from the {@code org.geotoolkit.sml.xml} package.
     * @return The implementation class, or {@code null} if none.
     */
    @Override
    public <T> Class<? extends T> getImplementation(final Class<T> type) {
        Class<?> impl;
        if (type == SMLMember.class) {
            impl = SensorML.Member.class;
        } else if (type == AbstractProcess.class) {
            impl = system ? SystemType.class : ComponentType.class;
        } else {
            return super.getImplementation(type);
        }
        return impl.asSubclass(type);
    }
}
