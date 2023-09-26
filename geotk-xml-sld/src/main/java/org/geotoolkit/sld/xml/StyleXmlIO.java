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
package org.geotoolkit.sld.xml;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardOpenOption.*;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import static org.apache.sis.util.ArgumentChecks.*;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.filter.FilterFactory2;
import org.geotoolkit.ogc.xml.OGC200toGTTransformer;
import org.geotoolkit.ogc.xml.v110.PropertyNameType;
import org.geotoolkit.sld.DefaultSLDFactory;
import org.geotoolkit.sld.MutableSLDFactory;
import org.geotoolkit.sld.MutableStyledLayerDescriptor;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.SortProperty;
import org.opengis.filter.ValueReference;
import org.opengis.metadata.citation.OnlineResource;
import org.geotoolkit.sld.StyledLayerDescriptor;
import org.opengis.style.FeatureTypeStyle;
import org.opengis.style.Rule;
import org.opengis.style.Style;
import org.opengis.util.FactoryException;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import java.util.logging.Logger;
import org.geotoolkit.filter.FilterUtilities;
import org.geotoolkit.ogc.xml.FilterMarshallerPool;
import org.geotoolkit.ogc.xml.FilterToOGC200Converter;
import org.geotoolkit.ogc.xml.FilterVersion;
import org.geotoolkit.ogc.xml.v200.ObjectFactory;
import org.geotoolkit.style.DefaultStyleFactory;

/**
 * Utility class to handle XML reading and writing for OGC SLD, SE and Filter.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public final class StyleXmlIO {

    private final FilterFactory filterFactory;
    private final MutableStyleFactory styleFactory;
    private final MutableSLDFactory sldFactory;

    private final org.geotoolkit.se.xml.v110.ObjectFactory factorySEv110 = new org.geotoolkit.se.xml.v110.ObjectFactory();
    private final org.geotoolkit.ogc.xml.v100.ObjectFactory factoryOGCv100 = new org.geotoolkit.ogc.xml.v100.ObjectFactory();
    private final org.geotoolkit.ogc.xml.v110.ObjectFactory factoryOGCv110 = new org.geotoolkit.ogc.xml.v110.ObjectFactory();

    private SLD100toGTTransformer transformerGTv100 = null;
    private SLD110toGTTransformer transformerGTv110 = null;
    private OGC200toGTTransformer transformerGTv200 = null;
    private GTtoSLD100Transformer transformerXMLv100 = null;
    private GTtoSLD110Transformer transformerXMLv110 = null;

    public StyleXmlIO() {
        this.styleFactory = DefaultStyleFactory.provider();
        this.filterFactory = FilterUtilities.FF;
        this.sldFactory = new DefaultSLDFactory();
    }

    public StyleXmlIO(final FilterFactory2 filterFactory, final MutableStyleFactory styleFactory, final MutableSLDFactory sldFactory) {
        this.filterFactory = filterFactory;
        this.styleFactory = styleFactory;
        this.sldFactory = sldFactory;
    }

    public static MarshallerPool getJaxbContext100() {
        return JAXBSLDUtilities.getMarshallerPoolSLD100();
    }

    public static MarshallerPool getJaxbContext110() {
        return JAXBSLDUtilities.getMarshallerPoolSLD110();
    }

    public SLD100toGTTransformer getTransformer100(){
        if (transformerGTv100 == null) {
            transformerGTv100 = new SLD100toGTTransformer(filterFactory, styleFactory, sldFactory);
        }
        return transformerGTv100;
    }

    public SLD110toGTTransformer getTransformer110(){
        if (transformerGTv110 == null) {
            transformerGTv110 = new SLD110toGTTransformer(filterFactory, styleFactory, sldFactory);
        }
        return transformerGTv110;
    }

    public SLD110toGTTransformer getTransformer110(final Map<String, String> namespaceMapping){
        if (transformerGTv110 == null) {
            transformerGTv110 = new SLD110toGTTransformer(filterFactory, styleFactory, sldFactory, namespaceMapping);
        }
        return transformerGTv110;
    }

    public OGC200toGTTransformer getTransformer200(final Map<String, String> namespaceMapping){
        if (transformerGTv200 == null) {
            transformerGTv200 = new OGC200toGTTransformer(filterFactory, namespaceMapping);
        }
        return transformerGTv200;
    }

    public GTtoSLD100Transformer getTransformerXMLv100() {
        if (transformerXMLv100 == null) transformerXMLv100 = new GTtoSLD100Transformer();
        return transformerXMLv100;
    }

    public GTtoSLD110Transformer getTransformerXMLv110() {
        if (transformerXMLv110 == null) transformerXMLv110 = new GTtoSLD110Transformer();
        return transformerXMLv110;
    }

    private Object unmarshall(final Object source, final Unmarshaller unMarshaller)
            throws JAXBException{
        if (source instanceof File sf) {
            return unMarshaller.unmarshal(sf);
        } else if (source instanceof Path sp) {
            try (InputStream in = Files.newInputStream(sp)) {
                return unMarshaller.unmarshal(in);
            } catch (IOException e) {
                throw new JAXBException(e.getMessage(), e);
            }
        } else if (source instanceof InputSource is) {
            return unMarshaller.unmarshal(is);
        } else if (source instanceof InputStream is) {
            return unMarshaller.unmarshal(is);
        } else if (source instanceof Node nd) {
            return unMarshaller.unmarshal(nd);
        } else if (source instanceof Reader r) {
            return unMarshaller.unmarshal(r);
        } else if (source instanceof Source src) {
            return unMarshaller.unmarshal( src);
        } else if (source instanceof URL u) {
            return unMarshaller.unmarshal(u);
        } else if (source instanceof XMLEventReader xer) {
            return unMarshaller.unmarshal(xer);
        } else if (source instanceof XMLStreamReader xsr) {
            return unMarshaller.unmarshal(xsr);
        } else if (source instanceof OnlineResource online) {
            try {
                final URL url = online.getLinkage().toURL();
                return unMarshaller.unmarshal(url);
            } catch (MalformedURLException ex) {
                Logger.getLogger("org.geotoolkit.sld.xml").log(Level.WARNING, null, ex);
                return null;
            }
        } else if (source instanceof String str) {
            return unMarshaller.unmarshal(new StringReader(str));
        } else if (source instanceof byte[] buffer) {
            return unMarshaller.unmarshal(new ByteArrayInputStream(buffer));
        } else {
            throw new IllegalArgumentException("Source object is not a valid class :" + source.getClass());
        }
    }

    public Object unmarshall(final Object source, final Specification.StyledLayerDescriptor version) throws JAXBException{
        switch(version){
            case V_1_0_0:
                return unmarshallV100(source);
            case V_1_1_0:
                return unmarshallV110(source);
            default:
                throw new IllegalArgumentException("Unknowned version :" + version);
        }
    }

    private Object unmarshallV100(final Object source) throws JAXBException{
        if (transformerGTv100 == null) {
            transformerGTv100 = new SLD100toGTTransformer(filterFactory, styleFactory, sldFactory);
        }
        final Unmarshaller unMarshaller = getJaxbContext100().acquireUnmarshaller();
        Object obj = unmarshall(source, unMarshaller);
        getJaxbContext100().recycle(unMarshaller);
        return obj;
    }

    private Object unmarshallV110(final Object source) throws JAXBException{
        if (transformerGTv110 == null) {
            transformerGTv110 = new SLD110toGTTransformer(filterFactory, styleFactory, sldFactory);
        }
        final Unmarshaller unMarshaller = getJaxbContext110().acquireUnmarshaller();
        Object obj = unmarshall(source, unMarshaller);
        getJaxbContext110().recycle(unMarshaller);
        return obj;
    }

    private void marshall(final Object target, final Object jaxbElement,
            final Marshaller marshaller) throws JAXBException{
        if(target instanceof File){
            marshaller.marshal(jaxbElement, (File)target );
        }else if(target instanceof Path){
            try (OutputStream out = Files.newOutputStream((Path) target,CREATE, WRITE, TRUNCATE_EXISTING)) {
                marshaller.marshal(jaxbElement, out);
            } catch (IOException e) {
                throw new JAXBException(e.getMessage(), e);
            }
        }else if(target instanceof ContentHandler){
            marshaller.marshal(jaxbElement, (ContentHandler)target );
        }else if(target instanceof OutputStream){
            marshaller.marshal(jaxbElement, (OutputStream)target );
        }else if(target instanceof Node){
            marshaller.marshal(jaxbElement, (Node)target );
        }else if(target instanceof Writer){
            marshaller.marshal(jaxbElement, (Writer)target );
        }else if(target instanceof Result){
            marshaller.marshal(jaxbElement, (Result)target );
        }else if(target instanceof XMLEventWriter){
            marshaller.marshal(jaxbElement, (XMLEventWriter)target );
        }else if(target instanceof XMLStreamWriter){
            marshaller.marshal(jaxbElement, (XMLStreamWriter)target );
        }else{
            throw new IllegalArgumentException("target object is not a valid class :" + target.getClass());
        }
    }

    private void marshallV100(final Object target, final Object jaxElement) throws JAXBException {
        final Marshaller marshaller = getJaxbContext100().acquireMarshaller();
        marshall(target, jaxElement, marshaller);
        getJaxbContext100().recycle(marshaller);
    }

    /**
     * This method do the same marshalling process like the first marshallV100 method with an option to format or not the output.
     */
    private void marshallV100(final Object target, final Object jaxElement, final boolean isformatted) throws JAXBException {
        final Marshaller marshaller = getJaxbContext100().acquireMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, isformatted);
        marshall(target, jaxElement, marshaller);
        getJaxbContext100().recycle(marshaller);
    }

    private void marshallV110(final Object target, final Object jaxElement) throws JAXBException {
        final Marshaller marshaller = getJaxbContext110().acquireMarshaller();
        marshall(target, jaxElement, marshaller);
        getJaxbContext110().recycle(marshaller);
    }


    // Styled Layer Descriptor -------------------------------------------------

    /**
     * Read a SLD source and parse it in GT SLD object.
     * Source can be : File, InputSource, InputStream, Node, Reader, Source, URL,
     * XMLEventReader, XMLStreamReader or OnlineResource
     */
    public MutableStyledLayerDescriptor readSLD(final Object source,
            final Specification.StyledLayerDescriptor version) throws JAXBException, FactoryException{

        ensureNonNull("source", source);
        ensureNonNull("version", version);

        final Object obj;

        switch(version){
            case V_1_0_0:
                obj = unmarshallV100(source);
                if (obj instanceof org.geotoolkit.sld.xml.v100.StyledLayerDescriptor) {
                    final org.geotoolkit.sld.xml.v100.StyledLayerDescriptor tempsld = (org.geotoolkit.sld.xml.v100.StyledLayerDescriptor) obj;
                    if ("1.0.0".equals(tempsld.getVersion())) {
                        return getTransformer100().visit((org.geotoolkit.sld.xml.v100.StyledLayerDescriptor) obj);
                    } else {
                        throw new JAXBException("Source is SLD but not in v1.0.0");
                    }
                } else {
                    throw new JAXBException("Source is not a valid OGC SLD v1.0.0");
                }
            case V_1_1_0 :
                obj = unmarshallV110(source);
                if (obj instanceof org.geotoolkit.sld.xml.v110.StyledLayerDescriptor) {
                    final org.geotoolkit.sld.xml.v110.StyledLayerDescriptor tempsld = (org.geotoolkit.sld.xml.v110.StyledLayerDescriptor) obj;
                    if ("1.1.0".equals(tempsld.getVersion())) {
                        return getTransformer110().visit((org.geotoolkit.sld.xml.v110.StyledLayerDescriptor) obj);
                    } else {
                        throw new JAXBException("Source is SLD but not in v1.1.0");
                    }
                } else {
                    throw new JAXBException("Source is not a valid OGC SLD v1.1.0");
                }
            default :
                throw new IllegalArgumentException("Unable to read source, specified version is not supported");
        }
    }

    /**
     * Write a GT SLD.
     * Target can be : File, ContentHandler, OutputStream, Node, Writer, Result,
     * XMLEventWriter, XMLStreamWriter
     */
    public void writeSLD(final Object target, final StyledLayerDescriptor sld,
            final Specification.StyledLayerDescriptor version) throws JAXBException{
        ensureNonNull("target", target);
        ensureNonNull("sld", sld);
        ensureNonNull("version", version);

        final Object jax;

        switch(version){
            case V_1_0_0 :
                jax = getTransformerXMLv100().visit(sld, null);
                marshallV100(target,jax);
                break;
            case V_1_1_0 :
                jax = getTransformerXMLv110().visit(sld, null);
                marshallV110(target,jax);
                break;
            default :
                throw new IllegalArgumentException("Unable to write object, specified version is not supported");
        }
    }

    /**
     * Write a GT SLD with an option to format the output result.
     * Target can be : File, ContentHandler, OutputStream, Node, Writer, Result,
     * XMLEventWriter, XMLStreamWriter
     *
     * @throws jakarta.xml.bind.JAXBException
     */
    public void writeSLD(final Object target, final StyledLayerDescriptor sld,
            final Specification.StyledLayerDescriptor version, final boolean isformatted) throws JAXBException{
        ensureNonNull("target", target);
        ensureNonNull("sld", sld);
        ensureNonNull("version", version);

        final Object jax;

        switch(version){
            case V_1_0_0 :
                jax = getTransformerXMLv100().visit(sld, null);
                marshallV100(target,jax, isformatted);
                break;
            case V_1_1_0 :
                jax = getTransformerXMLv110().visit(sld, null);
                marshallV110(target,jax);
                break;
            default :
                throw new IllegalArgumentException("Unable to write object, specified version is not supported");
        }
    }


    // Symbology Encoding ------------------------------------------------------

    /**
     * Read a SLD UserStyle source and parse it in GT Style object.
     * Source can be : File, InputSource, InputStream, Node, Reader, Source, URL,
     * XMLEventReader, XMLStreamReader or OnlineResource
     */
    public MutableStyle readStyle(final Object source,
            final Specification.SymbologyEncoding version) throws JAXBException, FactoryException{
        ensureNonNull("source", source);
        ensureNonNull("version", version);

        Object obj = source;

        switch(version){
            case SLD_1_0_0 :
                if(!(obj instanceof org.geotoolkit.sld.xml.v100.UserStyle)){
                    obj = unmarshallV100(source);
                }

                if(obj instanceof org.geotoolkit.sld.xml.v100.UserStyle){
                    return getTransformer100().visitUserStyle( (org.geotoolkit.sld.xml.v100.UserStyle) obj);
                }else{
                    throw new JAXBException("Source is not a valid OGC SLD UserStyle v1.0.0");
                }
            case V_1_1_0 :
                if(!(obj instanceof org.geotoolkit.sld.xml.v110.UserStyle)){
                    obj = unmarshallV110(source);
                }

                if(obj instanceof org.geotoolkit.sld.xml.v110.UserStyle){
                    return getTransformer110().visitUserStyle( (org.geotoolkit.sld.xml.v110.UserStyle) obj);
                }else{
                    throw new JAXBException("Source is not a valid OGC SLD UserStyle v1.1.0");
                }
            default :
                throw new IllegalArgumentException("Unable to read source, specified version is not supported");
        }
    }

    /**
     * Write a GT Style.
     * Target can be : File, ContentHandler, OutputStream, Node, Writer, Result,
     * XMLEventWriter, XMLStreamWriter
     */
    public void writeStyle(final Object target, final Style style,
            final Specification.StyledLayerDescriptor version) throws JAXBException{
        ensureNonNull("target", target);
        ensureNonNull("style", style);
        ensureNonNull("version", version);

        final Object jax;

        switch(version){
            case V_1_0_0 :
                jax = getTransformerXMLv100().visit(style, null);
                marshallV100(target,jax);
                break;
            case V_1_1_0 :
                jax = getTransformerXMLv110().visit(style, null);
                marshallV110(target,jax);
                break;
            default :
                throw new IllegalArgumentException("Unable to write object, specified version is not supported");
        }
    }

    /**
     * Read a SE FeatureTypeStyle source and parse it in GT FTS object.
     * Source can be : File, InputSource, InputStream, Node, Reader, Source, URL,
     * XMLEventReader, XMLStreamReader or OnlineResource
     */
    public MutableFeatureTypeStyle readFeatureTypeStyle(final Object source,
            final Specification.SymbologyEncoding version) throws JAXBException, FactoryException{
        ensureNonNull("source", source);
        ensureNonNull("version", version);

        final Object obj;

        switch(version){
            case SLD_1_0_0 :
                obj = unmarshallV100(source);
                if(obj instanceof org.geotoolkit.sld.xml.v100.FeatureTypeStyle){
                    return getTransformer100().visitFTS( (org.geotoolkit.sld.xml.v100.FeatureTypeStyle) obj);
                }else{
                    throw new JAXBException("Source is not a valid OGC SLD FeatureTypeStyle v1.0.0");
                }
            case V_1_1_0 :
                obj = unmarshallV110(source);
                if(obj instanceof org.geotoolkit.se.xml.v110.FeatureTypeStyleType){
                    return getTransformer110().visitFTS(obj);
                }else if(obj instanceof JAXBElement<?>&& (
                        ((JAXBElement<?>)obj).getValue() instanceof org.geotoolkit.se.xml.v110.OnlineResourceType ||
                        ((JAXBElement<?>)obj).getValue() instanceof org.geotoolkit.se.xml.v110.FeatureTypeStyleType ) ){
                    return getTransformer110().visitFTS( ((JAXBElement<?>)obj).getValue() );
                }else{
                    throw new JAXBException("Source is not a valid OGC SE FeatureTypeStyle v1.1.0");
                }
            default :
                throw new IllegalArgumentException("Unable to read source, specified version is not supported");
        }
    }

    /**
     * Write a GT FeatureTypeStyle.
     * Target can be : File, ContentHandler, OutputStream, Node, Writer, Result,
     * XMLEventWriter, XMLStreamWriter
     */
    public void writeFeatureTypeStyle(final Object target, final FeatureTypeStyle fts,
            final Specification.SymbologyEncoding version) throws JAXBException{
        ensureNonNull("target",target);
        ensureNonNull("fts",fts);
        ensureNonNull("version",version);

        Object jax;

        switch(version){
            case SLD_1_0_0 :
                org.geotoolkit.sld.xml.v100.FeatureTypeStyle jaxfts = getTransformerXMLv100().visit(fts, null);
                marshallV100(target,jaxfts);
                break;
            case V_1_1_0 :
                jax = getTransformerXMLv110().visit(fts, null);
                if(jax instanceof org.geotoolkit.se.xml.v110.FeatureTypeStyleType){
                    jax = factorySEv110.createFeatureTypeStyle((org.geotoolkit.se.xml.v110.FeatureTypeStyleType) jax);
                }else if(jax instanceof org.geotoolkit.se.xml.v110.CoverageStyleType){
                    jax = factorySEv110.createCoverageStyle( (org.geotoolkit.se.xml.v110.CoverageStyleType) jax);
                }
                marshallV110(target,jax);
                break;
            default :
                throw new IllegalArgumentException("Unable to write object, specified version is not supported");
        }
    }

    /**
     * Read a SE Rule source and parse it in GT Rule object.
     * Source can be : File, InputSource, InputStream, Node, Reader, Source, URL,
     * XMLEventReader, XMLStreamReader or OnlineResource
     */
    public MutableRule readRule(final Object source,
            final Specification.SymbologyEncoding version) throws JAXBException, FactoryException{
        ensureNonNull("source",source);
        ensureNonNull("version",version);

        final Object obj;

        switch(version){
            case SLD_1_0_0 :
                obj = unmarshallV100(source);
                if(obj instanceof org.geotoolkit.sld.xml.v100.Rule){
                    return getTransformer100().visitRule( (org.geotoolkit.sld.xml.v100.Rule) obj);
                }else{
                    throw new JAXBException("Source is not a valid OGC SLD Rule v1.0.0");
                }
            case V_1_1_0 :
                obj = unmarshallV110(source);
                if(obj instanceof org.geotoolkit.se.xml.v110.RuleType){
                    return getTransformer110().visitRule(obj);
                }else if(obj instanceof JAXBElement<?> && (
                        ((JAXBElement<?>)obj).getValue() instanceof org.geotoolkit.se.xml.v110.OnlineResourceType ||
                        ((JAXBElement<?>)obj).getValue() instanceof org.geotoolkit.se.xml.v110.RuleType ) ){
                    return getTransformer110().visitRule( ((JAXBElement<?>)obj).getValue() );
                }else{
                    throw new JAXBException("Source is not a valid OGC SE Rule v1.1.0");
                }
            default :
                throw new IllegalArgumentException("Unable to read source, specified version is not supported");
        }
    }

    /**
     * Write a GT Rule.
     * Target can be : File, ContentHandler, OutputStream, Node, Writer, Result,
     * XMLEventWriter, XMLStreamWriter
     */
    public void writeRule(final Object target, final Rule rule,
            final Specification.SymbologyEncoding version) throws JAXBException{
        ensureNonNull("target",target);
        ensureNonNull("rule",rule);
        ensureNonNull("version",version);

        Object jax;

        switch(version){
            case SLD_1_0_0 :
                final org.geotoolkit.sld.xml.v100.Rule jaxRule = getTransformerXMLv100().visit(rule, null);
                marshallV100(target,jaxRule);
                break;
            case V_1_1_0 :
                jax = getTransformerXMLv110().visit(rule, null);
                if(jax instanceof org.geotoolkit.se.xml.v110.RuleType){
                    jax = factorySEv110.createRule( (org.geotoolkit.se.xml.v110.RuleType) jax);
                }
                marshallV110(target,jax);
                break;
            default :
                throw new IllegalArgumentException("Unable to write object, specified version is not supported");
        }
    }


    // Filter ------------------------------------------------------------------

    /**
     * Read a Filter source and parse it in GT Filter object.
     * Source can be : File, InputSource, InputStream, Node, Reader, Source, URL,
     * XMLEventReader, XMLStreamReader or OnlineResource
     *
     * @todo implement it correctly for wfs
     */
    public SortProperty readSortBy(final Object source,
            final Specification.Filter version) throws JAXBException{
        ensureNonNull("source",source);
        ensureNonNull("version",version);

        Object obj;

        switch(version){
            case V_1_0_0 :
                throw new JAXBException("SortBy doesnt exist in OGC Filter v1.0.0");
            case V_1_1_0 :
                obj = unmarshallV110(source);

                if(obj instanceof JAXBElement){
                    obj = ((JAXBElement)obj).getValue();
                }

                if(obj instanceof org.geotoolkit.ogc.xml.v110.SortByType){
                    final List<SortProperty> sorts = transformerGTv110.visitSortBy( (org.geotoolkit.ogc.xml.v110.SortByType) obj);
                    return sorts.get(0);
                }else{
                    throw new JAXBException("Source is not a valid OGC SortBy v1.1.0 : " + obj);
                }
            default :
                throw new IllegalArgumentException("Unable to read source, specified version is not supported");
        }
    }

    /**
     * Read a Filter source and parse it in GT Filter object.
     * Source can be : File, InputSource, InputStream, Node, Reader, Source, URL,
     * XMLEventReader, XMLStreamReader or OnlineResource
     */
    public Filter readFilter(final Object source,
            final Specification.Filter version) throws JAXBException, FactoryException{
        ensureNonNull("source",source);
        ensureNonNull("version",version);

        final Object obj;

        switch(version){
            case V_1_0_0 :
                obj = unmarshallV100(source);
                if(obj instanceof org.geotoolkit.ogc.xml.v100.FilterType){
                    return getTransformer100().visitFilter( (org.geotoolkit.ogc.xml.v100.FilterType) obj);
                }else if(obj instanceof JAXBElement<?> &&
                        ((JAXBElement<?>)obj).getValue() instanceof org.geotoolkit.ogc.xml.v100.FilterType){
                    return getTransformer100().visitFilter( (org.geotoolkit.ogc.xml.v100.FilterType) ((JAXBElement<?>)obj).getValue() );
                }else{
                    throw new JAXBException("Source is not a valid OGC Filter v1.0.0");
                }
            case V_1_1_0 :
                obj = unmarshallV110(source);
                if(obj instanceof org.geotoolkit.ogc.xml.v110.FilterType){
                    return getTransformer110().visitFilter( (org.geotoolkit.ogc.xml.v110.FilterType) obj);
                }else if(obj instanceof JAXBElement<?> &&
                         ((JAXBElement<?>)obj).getValue() instanceof org.geotoolkit.ogc.xml.v110.FilterType){
                    return getTransformer110().visitFilter( (org.geotoolkit.ogc.xml.v110.FilterType) ((JAXBElement<?>)obj).getValue() );
                }else{
                    throw new JAXBException("Source is not a valid OGC Filter v1.1.0");
                }
            default :
                throw new IllegalArgumentException("Unable to read source, specified version is not supported");
        }
    }

    /**
     * Write a GT Filter.
     * Target can be : File, ContentHandler, OutputStream, Node, Writer, Result,
     * XMLEventWriter, XMLStreamWriter
     * TODO : unify with {@link FilterVersion}.
     */
    public void writeFilter(final Object target, final Filter filter,
            final Specification.Filter version) throws JAXBException{
        ensureNonNull("target",target);
        ensureNonNull("filter",filter);
        ensureNonNull("version",version);

        Object jax;

        switch(version){
            case V_1_0_0 :
                marshallV100(target, factoryOGCv100.createFilter(getTransformerXMLv100().apply(filter)));
                break;
            case V_1_1_0 :
                marshallV110(target,factoryOGCv110.createFilter(getTransformerXMLv110().apply(filter)));
                break;
            case V_2_0_0 :
                final MarshallerPool pool = FilterMarshallerPool.getInstance(FilterVersion.V200);
                final Marshaller m = pool.acquireMarshaller();
                marshall(target, new ObjectFactory().createFilter(new FilterToOGC200Converter().apply(filter)), m);
                pool.recycle(m);
                break;
            default :
                throw new IllegalArgumentException("Unable to write object, specified version is not supported");
        }
    }

    // OGC property ------------------------------------------------------------
    public ValueReference readPropertyName(final Object source,
            final Specification.Filter version) throws JAXBException{
        ensureNonNull("source",source);
        ensureNonNull("version",version);

        final Object obj;

        switch(version){
            case V_1_0_0 :
                obj = unmarshallV100(source);
                if(obj instanceof PropertyNameType){
                    return getTransformer100().visitPropertyName((org.geotoolkit.ogc.xml.v100.PropertyNameType) obj);
                }else{
                    throw new JAXBException("Source is not a valid OGC PropertyName v1.0.0");
                }
            case V_1_1_0 :
                obj = unmarshallV110(source);
                if(obj instanceof PropertyNameType){
                    return getTransformer110().visitPropertyName((PropertyNameType) obj);
                }else{
                    throw new JAXBException("Source is not a valid OGC PropertyName v1.1.0");
                }
            default :
                throw new IllegalArgumentException("Unable to read source, specified version is not supported");
        }
    }
}
