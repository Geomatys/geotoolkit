/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.xml;

import java.net.URI;
import java.util.Map;
import java.util.Deque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ServiceLoader;
import java.util.Collections;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import net.jcip.annotations.ThreadSafe;

import org.apache.sis.util.logging.Logging;
import org.geotoolkit.internal.jaxb.RegisterableTypes;
import org.geotoolkit.internal.jaxb.RegisterableAdapter;
import org.geotoolkit.internal.jaxb.gco.GO_CharacterString;
import org.geotoolkit.internal.jaxb.gco.CharSequenceAdapter;
import org.geotoolkit.internal.jaxb.gco.InternationalStringAdapter;
import org.geotoolkit.internal.jaxb.gco.StringAdapter;
import org.geotoolkit.internal.jaxb.gco.URIAdapter;
import org.geotoolkit.internal.jaxb.gmd.LocaleAdapter;


/**
 * Creates and configures {@link Marshaller} or {@link Unmarshaller} objects for use with Geotk.
 * Users fetch (un)marshallers by calls to the {@link #acquireMarshaller()} or
 * {@link #acquireUnmarshaller()} methods.
 *
 * {@section Configuring marshallers}
 * The (un)marshallers created by this class can optionally by configured with the Geotk-specific
 * properties defined in the {@link XML} class, in addition to JAXB standard properties.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @version 3.18
 *
 * @see XML
 *
 * @since 3.00
 * @module
 *
 * @todo Need a timeout for disposing marshallers that have been unused for a while.
 */
@ThreadSafe
public class MarshallerPool {
    /**
     * Maximal amount of marshaller and unmarshaller to keep.
     */
    private static final int CAPACITY = 16;

    /**
     * The key to be used in the map given to the constructors for specifying the root namespace.
     * An example of value for this key is {@code "http://www.isotc211.org/2005/gmd"}.
     *
     * @since 3.04
     */
    public static final String ROOT_NAMESPACE_KEY = "org.geotoolkit.xml.rootNamespace";

    /**
     * Root objects to be marshalled. They are the objects to be given by default to the
     * {@link JAXBContext}, if no classes were explicitly given.
     */
    private static final ServiceLoader<RegisterableTypes> TYPES = ServiceLoader.load(RegisterableTypes.class);

    /**
     * Adapters configured externally. This is mostly for handling extensions to metadata profile,
     * like the {@code FRA} extension for France (provided in the {@code geotk-metadata-fra} module).
     */
    private static final ServiceLoader<RegisterableAdapter> ADAPTERS = ServiceLoader.load(RegisterableAdapter.class);

    /**
     * The JAXB context to use for creating marshaller and unmarshaller.
     */
    private final JAXBContext context;

    /**
     * {@code true} if the JAXB implementation is the one bundled in JDK 6,
     * or {@code false} if this is an external implementation like a JAR put
     * in the endorsed directory.
     */
    private final boolean internal;

    /**
     * The mapper between namespaces and prefix.
     */
    private final Object mapper;

    /**
     * A configurable adapter.
     */
    private final CharSequenceAdapter anchors;

    /**
     * Additional adapters derived from {@link #anchors}.
     */
    private final XmlAdapter<GO_CharacterString, ? extends CharSequence>[] adapters;

    /**
     * The pool of marshaller. This pool is initially empty
     * and will be filled with elements as needed.
     */
    private final Deque<Marshaller> marshallers = new LinkedList<>();

    /**
     * The pool of unmarshaller. This pool is initially empty
     * and will be filled with elements as needed.
     */
    private final Deque<Unmarshaller> unmarshallers = new LinkedList<>();

    /**
     * Returns the root classes of Geotk objects to be marshalled by default.
     * Those classes can be given as the last argument to the {@code MarshallerPool}
     * constructors, in order to bound a default set of classes with {@link JAXBContext}.
     * <p>
     * The list of classes is determined dynamically from the Geotk modules found on
     * the classpath.
     *
     * @return The default set of classes to be bound to the {@code JAXBContext}.
     */
    public static Class<?>[] defaultClassesToBeBound() {
        final ArrayList<Class<?>> types = new ArrayList<>();
        for (final RegisterableTypes t : TYPES) {
            t.getTypes(types);
        }
        return types.toArray(new Class<?>[types.size()]);
    }

    /**
     * Creates a new factory for the given class to be bound, with a default empty namespace.
     *
     * @param  classesToBeBound The classes to be bound, for example {@code DefaultMetadata.class}.
     * @throws JAXBException    If the JAXB context can not be created.
     */
    public MarshallerPool(final Class<?>... classesToBeBound) throws JAXBException {
        this(Collections.<String,String>emptyMap(), classesToBeBound);
    }

    /**
     * Creates a new factory for the given class to be bound. The keys in the {@code properties} map
     * shall be one or many of the constants defined in this class like {@link #ROOT_NAMESPACE_KEY}.
     *
     * @param  properties       The set of properties to be given to the pool.
     * @param  classesToBeBound The classes to be bound, for example {@code DefaultMetadata.class}.
     * @throws JAXBException    If the JAXB context can not be created.
     *
     * @since 3.04
     */
    public MarshallerPool(final Map<String,String> properties, final Class<?>... classesToBeBound) throws JAXBException {
        this(properties, JAXBContext.newInstance(classesToBeBound));
    }

    /**
     * Creates a new factory for the given packages, with a default empty namespace.
     * The separator character for the packages is the colon.
     *
     * @param  packages         The packages in which JAXB will search for annotated classes to be bound,
     *                          for example {@code "org.geotoolkit.metadata.iso:org.geotoolkit.metadata.iso.citation"}.
     * @throws JAXBException    If the JAXB context can not be created.
     */
    public MarshallerPool(final String packages) throws JAXBException {
        this(Collections.<String,String>emptyMap(), packages);
    }

    /**
     * Creates a new factory for the given packages. The separator character for the packages is the
     * colon. The keys in the {@code properties} map shall be one or many of the constants defined
     * in this class like {@link #ROOT_NAMESPACE_KEY}.
     *
     * @param  properties    The set of properties to be given to the pool.
     * @param  packages      The packages in which JAXB will search for annotated classes to be bound,
     *                       for example {@code "org.geotoolkit.metadata.iso:org.geotoolkit.metadata.iso.citation"}.
     * @throws JAXBException If the JAXB context can not be created.
     *
     * @since 3.04
     */
    public MarshallerPool(final Map<String,String> properties, final String packages) throws JAXBException {
        this(properties, JAXBContext.newInstance(packages));
    }

    /**
     * Creates a new factory for the given packages.
     *
     * @param  properties    The set of properties to be given to the pool.
     * @param  context       The JAXB context.
     * @throws JAXBException If the OGC namespace prefix mapper can not be created.
     */
    @SuppressWarnings({"unchecked", "rawtypes"}) // Generic array creation
    private MarshallerPool(final Map<String,String> properties, final JAXBContext context) throws JAXBException {
        this.context = context;
        anchors = new CharSequenceAdapter();
        adapters = new XmlAdapter/*<GO_CharacterString, ? extends CharSequence>*/[] {
            anchors,
            new StringAdapter(anchors),
            new InternationalStringAdapter(anchors),
            new LocaleAdapter(anchors),
            new URIAdapter(anchors)
        };
        String rootNamespace = properties.get(ROOT_NAMESPACE_KEY);
        if (rootNamespace == null) {
            rootNamespace = "";
        }
        /*
         * Detects if we are using the endorsed JAXB implementation (i.e. the one provided in
         * separated JAR files).  If not, we will assume that we are using the implementation
         * bundled in JDK 6. We use the JAXB context package name as a criterion.
         *
         *   JAXB endorsed JAR uses    "com.sun.xml.bind"
         *   JAXB bundled in JDK uses  "com.sun.xml.internal.bind"
         */
        internal = !context.getClass().getName().startsWith("com.sun.xml.bind");
        String type = "org.geotoolkit.xml.OGCNamespacePrefixMapper_Endorsed";
        if (internal) {
            type = type.substring(0, type.lastIndexOf('_'));
        }
        /*
         * Instantiates the OGCNamespacePrefixMapper appropriate for the implementation
         * we just detected.
         */
        try {
            mapper = Class.forName(type).getConstructor(String.class).newInstance(rootNamespace);
        } catch (ReflectiveOperationException | NoClassDefFoundError exception) {
            // The NoClassDefFoundError is because of our trick using "geotk-provided".
            throw new JAXBException("Unsupported JAXB implementation.", exception);
        }
    }

    /**
     * Returns the marshaller or unmarshaller to use from the given queue.
     * If the queue is empty, returns {@code null}.
     */
    private static <T> T acquire(final Deque<T> queue) {
        synchronized (queue) {
            return queue.pollLast();
        }
    }

    /**
     * Marks the given marshaller or unmarshaller available for further reuse.
     */
    private static <T> void release(final Deque<T> queue, final T marshaller) {
        try {
            ((Pooled) marshaller).reset();
        } catch (JAXBException exception) {
            // Not expected to happen because the we are supposed
            // to reset the properties to their initial values.
            Logging.unexpectedException(MarshallerPool.class, "release", exception);
            return;
        }
        synchronized (queue) {
            queue.addLast(marshaller);
            while (queue.size() > CAPACITY) {
                // Remove the least recently used marshallers.
                queue.removeFirst();
            }
        }
    }

    /**
     * Returns a JAXB marshaller from the pool. If there is no marshaller currently available
     * in the pool, then this method will {@linkplain #createMarshaller create} a new one.
     * <p>
     * This method should be used as below:
     *
     * {@preformat java
     *     Marshaller marshaller = pool.acquireMarshaller();
     *     marshaller.marchall(...);
     *     pool.release(marshaller);
     * }
     *
     * Note that this is not strictly required to release the marshaller in a {@code finally}
     * block. Actually it is safer to let the garbage collector disposes the marshaller if an
     * error occurred while marshalling the object.
     *
     * @return A marshaller configured for formatting OGC/ISO XML.
     * @throws JAXBException If an error occurred while creating and configuring a marshaller.
     */
    public Marshaller acquireMarshaller() throws JAXBException {
        Marshaller marshaller = acquire(marshallers);
        if (marshaller == null) {
            marshaller = new PooledMarshaller(createMarshaller(), internal);
        }
        return marshaller;
    }

    /**
     * Returns a JAXB unmarshaller from the pool. If there is no unmarshaller currently available
     * in the pool, then this method will {@linkplain #createUnmarshaller create} a new one.
     * <p>
     * This method should be used as below:
     *
     * {@preformat java
     *     Unmarshaller unmarshaller = pool.acquireUnmarshaller();
     *     Unmarshaller.unmarchall(...);
     *     pool.release(unmarshaller);
     * }
     *
     * Note that this is not strictly required to release the unmarshaller in a {@code finally}
     * block. Actually it is safer to let the garbage collector disposes the unmarshaller if an
     * error occurred while unmarshalling the object.
     *
     * @return A unmarshaller configured for parsing OGC/ISO XML.
     * @throws JAXBException If an error occurred while creating and configuring the unmarshaller.
     */
    public Unmarshaller acquireUnmarshaller() throws JAXBException {
        Unmarshaller unmarshaller = acquire(unmarshallers);
        if (unmarshaller == null) {
            unmarshaller = new PooledUnmarshaller(createUnmarshaller(), internal);
        }
        return unmarshaller;
    }

    /**
     * Declares a marshaller as available for reuse. The caller should not use
     * anymore the marshaller after this method call.
     *
     * @param marshaller The marshaller to return to the pool.
     */
    public void release(final Marshaller marshaller) {
        release(marshallers, marshaller);
    }

    /**
     * Declares a unmarshaller as available for reuse. The caller should not use
     * anymore the unmarshaller after this method call.
     *
     * @param unmarshaller The unmarshaller to return to the pool.
     */
    public void release(final Unmarshaller unmarshaller) {
        release(unmarshallers, unmarshaller);
    }

    /**
     * Creates an configure a new JAXB marshaller.
     *
     * @return A new marshaller configured for formatting OGC/ISO XML.
     * @throws JAXBException If an error occurred while creating and configuring the marshaller.
     */
    protected Marshaller createMarshaller() throws JAXBException {
        final String mapperKey = internal ?
            "com.sun.xml.internal.bind.namespacePrefixMapper" :
            "com.sun.xml.bind.namespacePrefixMapper";
        final Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.setProperty(mapperKey, mapper);
        for (final XmlAdapter<GO_CharacterString, ? extends CharSequence> adapter : adapters) {
            marshaller.setAdapter(adapter);
        }
        for (final RegisterableAdapter adapter : ADAPTERS) {
            adapter.register(marshaller);
        }
        return marshaller;
    }

    /**
     * Creates an configure a new JAXB unmarshaller.
     *
     * @return A new unmarshaller configured for parsing OGC/ISO XML.
     * @throws JAXBException If an error occurred while creating and configuring the unmarshaller.
     */
    protected Unmarshaller createUnmarshaller() throws JAXBException {
        final Unmarshaller unmarshaller = context.createUnmarshaller();
        for (final XmlAdapter<GO_CharacterString, ? extends CharSequence> adapter : adapters) {
            unmarshaller.setAdapter(adapter);
        }
        for (final RegisterableAdapter adapter : ADAPTERS) {
            adapter.register(unmarshaller);
        }
        return unmarshaller;
    }

    /**
     * Adds a label associated to an URN. For some methods expected to return a code as a
     * {@link String} object, the code will be completed by the given URN in an {@code AnchorType}
     * element.
     * <p>
     * This method should be invoked from subclasses constructor only. Anchors can be added
     * but can not be removed or modified.
     *
     * @param  label The label associated to the URN.
     * @param  linkage The URN.
     * @throws IllegalStateException If a URN is already associated to the given linkage.
     */
    protected void addAnchor(final String label, final URI linkage) throws IllegalStateException {
        anchors.addLinkage(label, linkage);
    }
}
