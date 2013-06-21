/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.internal.jaxb;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.adapters.XmlAdapter;


/**
 * An interface for {@link XmlAdapter} to be used in replacement of the instance created by JAXB.
 * This interface provides a way to replace <cite>default</cite> adapters by <cite>configured</cite>
 * ones, through a call to {@link Marshaller#setAdapter}. It does not allow the addition of new adapters
 * (i.e. it can not be used in replacement of {@link javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter}
 * annotation).
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
public interface RegisterableAdapter {
    /**
     * Invoked when a new adapter is created by {@link org.apache.sis.xml.MarshallerPool}.
     * Typical implementations will be as below:
     *
     * {@preformat java
     *     marshaller.setAdapter(MyParent.class, this);
     * }
     *
     * @param marshaller The marshaller to be configured.
     * @throws JAXBException If the given marshaller can not be configured.
     */
    void register(Marshaller marshaller) throws JAXBException;

    /**
     * Invoked when a new adapter is created by {@link org.apache.sis.xml.MarshallerPool}.
     * Typical implementations will be as below:
     *
     * {@preformat java
     *     unmarshaller.setAdapter(MyParent.class, this);
     * }
     *
     * @param unmarshaller The unmarshaller to be configured.
     * @throws JAXBException If the given unmarshaller can not be configured.
     */
    void register(Unmarshaller unmarshaller) throws JAXBException;
}
