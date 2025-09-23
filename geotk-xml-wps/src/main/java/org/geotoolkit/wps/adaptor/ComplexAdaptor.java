/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.wps.adaptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ServiceLoader;
import org.apache.sis.util.internal.shared.UnmodifiableArrayList;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.xml.ReferenceProxy;
import org.geotoolkit.wps.xml.v200.DataInput;
import org.geotoolkit.wps.xml.v200.DataOutput;
import org.geotoolkit.wps.xml.v200.Format;
import org.geotoolkit.wps.xml.v200.Reference;

/**
 * Define a mapping between WPS complex data type and java type.
 *
 * @author Johann Sorel (Geomatys)
 * @param <T> Java type
 */
public abstract class ComplexAdaptor<T> implements DataAdaptor<T> {

    private static final Collection<ComplexAdaptor.Spi> SERVICES;

    static {
        final ServiceLoader<ComplexAdaptor.Spi> serviceLodaer = ServiceLoader.load(ComplexAdaptor.Spi.class);
        final List<ComplexAdaptor.Spi> spis = new ArrayList<>();
        for(final ComplexAdaptor.Spi r : serviceLodaer){
            spis.add(r);
        }
        SERVICES = UnmodifiableArrayList.wrap(spis.toArray(new ComplexAdaptor.Spi[spis.size()]));
    }

    /**
     * WPS data mime type.
     *
     * @return
     */
    public abstract String getMimeType();

    /**
     * WPS data encoding.
     *
     * @return
     */
    public abstract String getEncoding();

    /**
     * WPS data schema if xml type.
     *
     * @return
     */
    public abstract String getSchema();

    /**
     * Convert java object to WPS-2 input.
     *
     * <p>
     * Default implementation of this method only support objects of type Reference.
     * </p>
     *
     * @param candidate
     * @return
     */
    @Override
    public DataInput toWPS2Input(T candidate) throws UnconvertibleObjectException {
        if (candidate instanceof ReferenceProxy) {
            final Reference reference = ((ReferenceProxy)candidate).getReference();

            final Reference ref;
            if (reference instanceof Reference) {
                ref = (Reference) reference;
            } else {
                ref = new Reference();
                ref.setHref(reference.getHref());
                ref.setEncoding(reference.getEncoding());
                ref.setMimeType(reference.getMimeType());
                ref.setSchema(reference.getSchema());
                ref.setBody(reference.getBody());
            }

            final DataInput dit = new DataInput();
            dit.setReference(ref);
            return dit;
        }
        throw new UnconvertibleObjectException("Unsupported value.");
    }

    /**
     * Convert java object to WPS-2 input.
     *
     * <p>
     * Default implementation of this method only support objects of type Reference.
     * </p>
     *
     * @param candidate
     * @return
     */
    @Override
    public T fromWPS2Input(DataOutput candidate) throws UnconvertibleObjectException {
        final Reference ref = candidate.getReference();
        if (ref != null) return ReferenceProxy.create(ref, getValueClass());
        throw new UnconvertibleObjectException("Unsupported value.");
    }

    /**
     * Find an adaptor for given format.
     *
     * @param format
     * @return WPSDataAdaptor or null if no adaptor are available
     */
    public static ComplexAdaptor getAdaptor(Format format) {
        ArgumentChecks.ensureNonNull("format", format);

        for (Spi spi : SERVICES) {
            final ComplexAdaptor adaptor = spi.create(format);
            if(adaptor!= null) return adaptor;
        }

        return null;
    }

    /**
     * For testing purposes.
     *
     * @return
     */
    static int getAdaptorCount() {
        return SERVICES.size();
    }

    static interface Spi {

        /**
         * Create a adaptor for a given format.
         *
         * @param format WPS format description
         * @return
         */
        ComplexAdaptor create(Format format);

    }

}
