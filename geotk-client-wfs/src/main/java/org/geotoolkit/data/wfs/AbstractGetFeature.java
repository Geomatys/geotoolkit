/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.data.wfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import org.apache.sis.filter.internal.FunctionNames;

import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.filter.visitor.SimplifyingFilterVisitor;
import org.geotoolkit.security.ClientSecurity;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.ogc.xml.FilterMarshallerPool;
import org.geotoolkit.ogc.xml.FilterVersion;
import org.geotoolkit.ogc.xml.XMLFilter;
import org.geotoolkit.wfs.xml.GetFeature;
import org.geotoolkit.wfs.xml.Query;
import org.geotoolkit.wfs.xml.WFSMarshallerPool;
import org.geotoolkit.wfs.xml.ResultTypeType;
import org.geotoolkit.wfs.xml.WFSVersion;
import org.geotoolkit.wfs.xml.WFSXmlFactory;

import org.opengis.util.GenericName;
import org.opengis.filter.ValueReference;
import org.opengis.filter.Filter;
import org.opengis.filter.Expression;


/**
 * Abstract Get feature request.
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module
 */
public abstract class AbstractGetFeature extends AbstractRequest implements GetFeatureRequest{
    protected static final Logger LOGGER = Logger.getLogger("org.geotoolkit.data.wfs");
    protected final WFSVersion version;

    private QName typeName       = null;
    private Filter filter        = null;
    private Integer maxFeatures  = null;
    private GenericName[] propertyNames = null;
    private String outputFormat  = null;

    protected AbstractGetFeature(final String serverURL, final WFSVersion version, final ClientSecurity security) {
        super(serverURL,security,null);
        this.version = version;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public QName getTypeName() {
        return typeName;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setTypeName(final QName type) {
        this.typeName = type;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Filter getFilter(){
        return filter;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setFilter(final Filter filter){
        this.filter = filter;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Integer getMaxFeatures(){
        return maxFeatures;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setMaxFeatures(final Integer max){
        maxFeatures = max;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public GenericName[] getPropertyNames() {
        return propertyNames;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setPropertyNames(final GenericName[] properties) {
        this.propertyNames = properties;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getOutputFormat() {
       return outputFormat;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setOutputFormat(final String outputFormat) {
        this.outputFormat = outputFormat;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URL getURL() throws MalformedURLException {
        requestParameters.put("SERVICE", "WFS");
        requestParameters.put("REQUEST", "GETFEATURE");
        requestParameters.put("VERSION", version.getCode());

        if(maxFeatures != null){
            requestParameters.put("MAXFEATURES", maxFeatures.toString());
        }

        String prefix = "";
        final String namespace = typeName == null? null : typeName.getNamespaceURI();
        if (typeName != null) {
            final StringBuilder sbN = new StringBuilder();
            final StringBuilder sbNS = new StringBuilder();

            prefix = typeName.getPrefix();
            boolean emptyPrefix = prefix == null || prefix.isEmpty();
            if (emptyPrefix && namespace != null && !namespace.isEmpty()) {
                prefix = "ut"; // We've got a namespace to replace, we need a prefix
                emptyPrefix = false;
            }

            if (emptyPrefix) {
                requestParameters.put(getTypeNameParameterKey(), typeName.getLocalPart());
            } else {
                requestParameters.put(getTypeNameParameterKey(), prefix+":"+typeName.getLocalPart());
            }

            if (namespace != null && !namespace.isEmpty()) {
                requestParameters.put("NAMESPACE", new StringBuilder("xmlns(").append(prefix).append('=').append(namespace).append(')').toString());
            }
        }

        prepareFilter()
                .map(this::toString)
                .ifPresent(text -> requestParameters.put("FILTER", text));

            final String pNames = preparePropertyNames(prefix, namespace)
                    .collect(Collectors.joining(","));
            if (pNames != null && !pNames.isEmpty()) {
                requestParameters.put("PROPERTYNAME", pNames);
            }

        if (outputFormat != null) {
            requestParameters.put("OUTPUTFORMAT",outputFormat);
        }
        return super.getURL();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public InputStream getResponseStream() throws IOException {
        final List<QName> typeNames = new ArrayList<>();

        final List<String> propNames;
        if(typeName != null) {
            typeNames.add(typeName);
            propNames = preparePropertyNames(typeName.getPrefix(), typeName.getNamespaceURI()).collect(Collectors.toList());
        } else {
            propNames = preparePropertyNames(null, null).collect(Collectors.toList());
        }

        final XMLFilter xmlFilter = prepareFilter().orElse(null);
        final Query query = WFSXmlFactory.buildQuery(version.getCode(), xmlFilter, typeNames, null, null, null, propNames);

        final GetFeature request = WFSXmlFactory.buildGetFeature(version.getCode(), "WFS", null, null, maxFeatures, query, ResultTypeType.RESULTS, outputFormat);

        final URL url = new URL(serverURL);
        URLConnection conec = url.openConnection();
        conec = security.secure(conec);

        conec.setDoOutput(true);
        conec.setRequestProperty("Content-Type", "text/xml");

        OutputStream stream = conec.getOutputStream();
        stream = security.encrypt(stream);
        try (final OutputStream toClose = stream) {
            Marshaller marshaller = WFSMarshallerPool.getInstance(version).acquireMarshaller();
            marshaller.marshal(request, stream);
            WFSMarshallerPool.getInstance().recycle(marshaller);
        } catch (JAXBException ex) {
            throw new IOException(ex);
        }

        return security.decrypt(conec.getInputStream());
    }

    public abstract FilterVersion getFilterVersion();

    public abstract String getTypeNameParameterKey();

    /**
     * Marshall given XML filter (using version defined by {@link #getFilterVersion() }.
     * @param source The filter to marshall.
     * @return XML content of the filter.
     */
    private String toString(final XMLFilter source) {
        try (final StringWriter writer = new StringWriter()) {
            final MarshallerPool pool = FilterMarshallerPool.getInstance(getFilterVersion());
            final Marshaller marsh = pool.acquireMarshaller();
            marsh.marshal(source, writer);
            pool.recycle(marsh);

            return writer.toString();
        } catch (JAXBException | IOException ex) {
            LOGGER.log(Level.WARNING, "GetFeature: FILTER parameter cannot be written", ex);
        }

        return null;
    }

    protected Stream<String> preparePropertyNames(final String prefix, final String namespace) {
        if (propertyNames == null || propertyNames.length < 1) {
            return Stream.empty();
        }

        Stream<GenericName> names = Stream.of(propertyNames);
        if (prefix == null || prefix.isEmpty()) {
            return names.map(name -> name.tip().toString());
        } else {
            final String fPrefix = prefix;
            return names.map(name -> {
                final String local = name.tip().toString();
                return namespace.equals(NamesExt.getNamespace(name)) ? fPrefix + ":" + local : local;
            });
        }
    }

    protected Optional<XMLFilter> prepareFilter() {
        if (filter == null || Filter.include().equals(filter)) {
            return Optional.empty();
        }

        final SimplifyingFilterVisitor visitor;
        String namespace = typeName == null? null : typeName.getNamespaceURI();
        if (namespace != null && !namespace.isEmpty()) {
            String prefix = typeName.getPrefix();
            if (prefix == null || prefix.trim().isEmpty()) {
                namespace += ":";
                prefix = "";
            }

            visitor = new PrefixSwitchVisitor();
            ((PrefixSwitchVisitor)visitor).setPrefix(namespace, prefix);
        } else {
            visitor = SimplifyingFilterVisitor.INSTANCE;
        }

        final Object result = visitor.visit(filter);
        if (result == null || Filter.include().equals(result)) {
            return Optional.empty();
        } else if (result instanceof Filter) {
            return Optional.of(FilterMarshallerPool.transform((Filter)result, getFilterVersion()));
        }

        throw new IllegalStateException("Filter visit resulted in an unexpected object: "+result.getClass());
    }

    /**
     * Check property names, and replace their prefix with a new one configured
     * beforehand. To configure a replacement, use method {@link #setPrefix(java.lang.String, java.lang.String) }.
     */
    protected static class PrefixSwitchVisitor extends SimplifyingFilterVisitor {
        final Map<String, String> namespaceReplacements = new HashMap<>();

        public PrefixSwitchVisitor() {
            Function<Expression<Object, ?>, Object> fallback = getExpressionHandler(FunctionNames.ValueReference);
            setExpressionHandler(FunctionNames.ValueReference, (e) -> {
                final ValueReference expression = (ValueReference) e;
                final String pName = expression.getXPath();
                for (final Map.Entry<String, String> replacement : namespaceReplacements.entrySet()) {
                    final String namespace = replacement.getKey();
                    if (pName.startsWith(namespace)) {
                        return ff.property(replacement.getValue() + pName.substring(namespace.length()));
                    }
                }
                return fallback.apply(e);
            });
        }

        /**
         * Specify a prefix replacement.
         * @param namespace The prefix that must be replaced
         * @param replacementPrefix The new prefix to set on property name.
         */
        public void setPrefix(final String namespace, final String replacementPrefix) {
            namespaceReplacements.put(namespace, replacementPrefix);
        }
    }
}
