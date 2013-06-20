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

import java.util.Locale;
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
 * Provides convenience methods for marshalling and unmarshalling Geotk objects.
 * This class defines also some property keys that can be given to the {@link Marshaller}
 * and {@link Unmarshaller} instances created by {@link PooledMarshaller}:
 * <p>
 * <ul>
 *   <li>{@link #LOCALE} for specifying the locale to use for international strings and code lists.</li>
 *   <li>{@link #TIMEZONE} for specifying the timezone to use for dates and times.</li>
 *   <li>{@link #SCHEMAS} for specifying the root URL of metadata schemas to use.</li>
 *   <li>{@link #LINKER} for replacing {@code xlink} or {@code uuidref} attributes by the actual object to use.</li>
 *   <li>{@link #CONVERTERS} for controlling the conversion of URL, UUID, Units or similar objects.</li>
 *   <li>{@link #STRING_SUBSTITUTES} for specifying which code lists to replace by simpler
 *       {@code <gco:CharacterString>} elements.</li>
 * </ul>
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.00
 * @module
 */
public final class XML extends Static {
    /**
     * Allows client code to specify the locale to use for marshalling
     * {@link org.opengis.util.InternationalString} and {@link org.opengis.util.CodeList}
     * instances. The value for this property shall be an instance of {@link Locale}.
     * <p>
     * This property is mostly for marshallers. However this property can also be used at
     * unmarshalling time, for example if a {@code <gmd:PT_FreeText>} element containing
     * many localized strings need to be represented in a Java {@link String} object. In
     * such case, the unmarshaller will try to pickup a string in the language specified
     * by this property.
     *
     * {@section Default behavior}
     * If this property is never set, then (un)marshalling will try to use "unlocalized" strings -
     * typically some programmatic strings like {@linkplain org.opengis.annotation.UML#identifier()
     * UML identifiers}. While such identifiers often look like English words, they are not
     * considered as the {@linkplain Locale#ENGLISH English} localization.
     * The algorithm attempting to find a "unlocalized" string is defined in the
     * {@link org.geotoolkit.util.DefaultInternationalString#toString(Locale)} javadoc.
     *
     * {@section Special case}
     * If the object to be marshalled is an instance of
     * {@link org.apache.sis.metadata.iso.DefaultMetadata}, then the value given to its
     * {@link org.apache.sis.metadata.iso.DefaultMetadata#setLanguage setLanguage(Locale)}
     * method will have precedence over this property. This behavior is compliant with
     * INSPIRE rules.
     *
     * @see Marshaller#setProperty(String, Object)
     * @see org.apache.sis.metadata.iso.DefaultMetadata#setLanguage(Locale)
     *
     * @since 3.17
     */
    public static final String LOCALE = "org.geotoolkit.xml.locale";

    /**
     * The timezone to use for marshalling dates and times.
     *
     * {@section Default behavior}
     * If this property is never set, then (un)marshalling will use the
     * {@linkplain java.util.TimeZone#getDefault() default timezone}.
     *
     * @since 3.17
     */
    public static final String TIMEZONE = "org.geotoolkit.xml.timezone";

    /**
     * Allows client code to specify the root URL of schemas. The value for this property shall
     * be an instance of {@link java.util.Map Map&lt;String,String&gt;}. This property controls
     * the URL to be used when marshalling the following elements:
     * <p>
     * <ul>
     *   <li>The value of the {@code codeList} attribute when marshalling subclasses of
     *       {@link org.opengis.util.CodeList} in ISO 19139 compliant XML document.</li>
     *   <li>The value of the {@code uom} attribute when marshalling measures (for example
     *       {@code <gco:Distance>}) in ISO 19139 compliant XML document.</li>
     * </ul>
     * <p>
     * As of Geotk 3.17, only one {@code Map} key is recognized: {@code "gmd"}, which stands
     * for the ISO 19139 schemas. Additional keys, if any, are ignored. Future Geotk versions
     * may recognize more keys.
     *
     * {@section Valid values}
     * <table border="1" cellspacing="0" cellpadding="6">
     *   <tr bgcolor="lightblue"><th>Map key</th> <th>Typical values (choose only one)</th></tr>
     *   <tr><th><b>gmd</b></th><td nowrap>
     *     http://schemas.opengis.net/iso/19139/20070417/<br>
     *     http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/<br>
     *     http://eden.ign.fr/xsd/fra/20060922/
     *   </td></tr>
     * </table>
     *
     * @since 3.17
     */
    public static final String SCHEMAS = "org.geotoolkit.xml.schemas";

    /**
     * Specifies the GML version to be marshalled or unmarshalled. The GML version may affect the
     * set of XML elements to be marshalled. Newer versions typically have more elements, but not
     * always. For example in {@code gml:VerticalDatum}, the {@code gml:verticalDatumType} property
     * presents in GML 3.0 and 3.1 has been removed in GML 3.2.
     * <p>
     * The value can be {@link String} or {@link org.geotoolkit.util.Version} objects.
     * If no version is specified, then the most recent GML version is assumed.
     *
     * @since 3.20
     */
    public static final String GML_VERSION = "org.geotoolkit.gml.version";

    /**
     * Allows client code to replace {@code xlink} or {@code uuidref} attributes by the actual
     * object to use. The value for this property shall be an instance of {@link ObjectLinker}.
     * <p>
     * If a property in a XML document is defined only by {@code xlink} or {@code uuidref} attributes,
     * without any concrete definition, then the default behavior is to create an empty element which
     * contain only the values of the above-cited attributes. This is usually not the right behavior,
     * since we should use the reference ({@code href} or {@code uuidref} attributes) for fetching
     * the appropriate object. However doing so require some application knowledge, for example a
     * catalog where to perform the search, which is left to users. Users can define their search
     * algorithm by subclassing {@link ObjectLinker} and configure a unmarshaller as below:
     *
     * {@preformat java
     *     ObjectLinker myLinker = ...;
     *     Unmarshaller um = marshallerPool.acquireUnmarshaller();
     *     um.setProperty(XML.LINKER, myLinker);
     *     Object obj = um.unmarshal(xml);
     *     marshallerPool.release(um);
     * }
     *
     * @see Unmarshaller#setProperty(String, Object)
     * @see ObjectLinker
     *
     * @since 3.18
     */
    public static final String LINKER = "org.geotoolkit.xml.linker";

    /**
     * Allows client code to control the behavior of the (un)marshalling process when an element
     * can not be processed, or alter the element values. The value for this property shall be an
     * instance of {@link ObjectConverters}.
     * <p>
     * If an element in a XML document can not be parsed (for example if a {@linkplain java.net.URL}
     * string is not valid), the default behavior is to throw an exception which cause the
     * (un)marshalling of the entire document to fail. This default behavior can be customized by
     * invoking {@link Marshaller#setProperty(String, Object)} with this {@code CONVERTERS} property
     * key and a custom {@link ObjectConverters} instance. {@code ObjectConverters} can also be used
     * for replacing an erroneous URL by a fixed URL. See the {@link ObjectConverters} javadoc for
     * more details.
     *
     * {@section Example}
     * The following example collect the failures in a list without stopping the (un)marshalling
     * process.
     *
     * {@preformat java
     *     class Warnings extends ObjectConverters {
     *         // The warnings collected during (un)marshalling.
     *         List<String> messages = new ArrayList<String>();
     *
     *         // Override the default implementation in order to
     *         // collect the warnings and allow the process to continue.
     *         protected <T> boolean exceptionOccured(T value, Class<T> sourceType, Class<T> targetType, Exception e) {
     *             mesages.add(e.getLocalizedMessage());
     *             return true;
     *         }
     *     }
     *
     *     // Unmarshall a XML string, trapping some kind of errors.
     *     // Not all errors are trapped - see the ObjectConverters
     *     // javadoc for more details.
     *     Warnings myWarningList = new Warnings();
     *     Unmarshaller um = marshallerPool.acquireUnmarshaller();
     *     um.setProperty(XML.CONVERTERS, myWarningList);
     *     Object obj = um.unmarshal(xml);
     *     marshallerPool.release(um);
     *     if (!myWarningList.isEmpty()) {
     *         // Report here the warnings to the user.
     *     }
     * }
     *
     * @see Unmarshaller#setProperty(String, Object)
     * @see ObjectConverters
     *
     * @since 3.17
     */
    public static final String CONVERTERS = "org.geotoolkit.xml.converters";

    /**
     * Allows marshallers to substitute some code lists by the simpler {@code <gco:CharacterString>}
     * element. The value for this property shall be a coma-separated list of any of the following
     * values: "{@code language}", "{@code country}".
     *
     * {@section Example}
     * INSPIRE compliant language code shall be formatted like below (formatting may vary):
     *
     * {@preformat xml
     *   <gmd:language>
     *     <gmd:LanguageCode
     *         codeList="http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/ML_gmxCodelists.xml#LanguageCode"
     *         codeListValue="fra">French</gmd:LanguageCode>
     *   </gmd:language>
     * }
     *
     * However if this property contains the "{@code language}" value, then the marshaller will
     * format the language code like below (which is legal according OGC schemas, but is not
     * INSPIRE compliant):
     *
     * {@preformat xml
     *   <gmd:language>
     *     <gco:CharacterString>fra</gco:CharacterString>
     *   </gmd:language>
     * }
     *
     * @since 3.18
     */
    public static final String STRING_SUBSTITUTES = "org.geotoolkit.xml.stringSubstitutes";

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
     * @throws JAXBException If an error occurred during the marshalling.
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
     * @throws JAXBException If an error occurred during the marshalling.
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
     * @throws JAXBException If an error occurred during the marshalling.
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
     * @throws JAXBException If an error occurred during the unmarshalling.
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
     * @throws JAXBException If an error occurred during the unmarshalling.
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
     * @throws JAXBException If an error occurred during the unmarshalling.
     */
    public static Object unmarshal(final File input) throws JAXBException {
        final Unmarshaller unmarshaller = POOL.acquireUnmarshaller();
        final Object object = unmarshaller.unmarshal(input);
        POOL.release(unmarshaller);
        return object;
    }
}
