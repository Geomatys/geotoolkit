/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.xml;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;

import org.geotoolkit.lang.Static;


/**
 * Provides convenience methods for marshalling and unmarshalling Geotoolkit objects.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
@Static
public final class XML {
    /**
     * The pool of marshallers and unmarshallers used by this class.
     */
    private static final MarshallerPool POOL;
    static {
        try {
            POOL = new MarshallerPool(MarshallerPool.defaultClassesToBeBound());
        } catch (JAXBException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Do not allow instantiation on this class.
     */
    private XML() {
    }

    /**
     * Marshall the given object into a string.
     *
     * @param  object The root of content tree to be marshalled.
     * @return The XML representation of the given object.
     * @throws JAXBException If an error occured during the marshalling.
     */
    public static String marshal(final Object object) throws JAXBException {
        final StringWriter output = new StringWriter();
        final Marshaller marshaller = POOL.acquireMarshaller();
        marshaller.marshal(object, output);
        POOL.release(marshaller);
        return output.toString();
    }

    /**
     * Marshall the given object into a stream.
     *
     * @param  object The root of content tree to be marshalled.
     * @param  output The stream where to write.
     * @throws JAXBException If an error occured during the marshalling.
     */
    public static void marshal(final Object object, final OutputStream output) throws JAXBException {
        final Marshaller marshaller = POOL.acquireMarshaller();
        marshaller.marshal(object, output);
        POOL.release(marshaller);
    }

    /**
     * Marshall the given object into a file.
     *
     * @param  object The root of content tree to be marshalled.
     * @param  output The file to be written.
     * @throws JAXBException If an error occured during the marshalling.
     */
    public static void marshal(final Object object, final File output) throws JAXBException {
        final Marshaller marshaller = POOL.acquireMarshaller();
        marshaller.marshal(object, output);
        POOL.release(marshaller);
    }

    /**
     * Unmarshall an object from the given string.
     *
     * @param  input The XML representation of an object.
     * @return The object unmarshalled from the given input.
     * @throws JAXBException If an error occured during the unmarshalling.
     */
    public static Object unmarshal(final String input) throws JAXBException {
        final StringReader in = new StringReader(input);
        final Unmarshaller unmarshaller = POOL.acquireUnmarshaller();
        final Object object = unmarshaller.unmarshal(in);
        POOL.release(unmarshaller);
        return object;
    }

    /**
     * Unmarshall an object from the given stream.
     *
     * @param  input The stream from which to read a XML representation.
     * @return The object unmarshalled from the given input.
     * @throws JAXBException If an error occured during the unmarshalling.
     */
    public static Object unmarshal(final InputStream input) throws JAXBException {
        final Unmarshaller unmarshaller = POOL.acquireUnmarshaller();
        final Object object = unmarshaller.unmarshal(input);
        POOL.release(unmarshaller);
        return object;
    }

    /**
     * Unmarshall an object from the given file.
     *
     * @param  input The file from which to read a XML representation.
     * @return The object unmarshalled from the given input.
     * @throws JAXBException If an error occured during the unmarshalling.
     */
    public static Object unmarshal(final File input) throws JAXBException {
        final Unmarshaller unmarshaller = POOL.acquireUnmarshaller();
        final Object object = unmarshaller.unmarshal(input);
        POOL.release(unmarshaller);
        return object;
    }
}
