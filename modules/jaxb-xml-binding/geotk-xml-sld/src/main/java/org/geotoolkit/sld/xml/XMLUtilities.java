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

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;

// JAXB dependencies
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

// JAXP dependencies
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;

// Geotoolkit dependencies
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.ogc.xml.v110.PropertyNameType;
import org.geotoolkit.sld.DefaultSLDFactory;
import org.geotoolkit.sld.MutableSLDFactory;
import org.geotoolkit.sld.MutableStyledLayerDescriptor;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.xml.MarshallerPool;

// GeoAPI dependencies
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.sort.SortBy;
import org.opengis.metadata.citation.OnlineResource;
import org.opengis.util.FactoryException;
import org.opengis.sld.StyledLayerDescriptor;
import org.opengis.style.FeatureTypeStyle;
import org.opengis.style.Rule;
import org.opengis.style.Style;

import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;

/**
 * Utility class to handle XML reading and writing for OGC SLD, SE and Filter.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class XMLUtilities {

    private static final MarshallerPool POOL_100;
    private static final MarshallerPool POOL_110;

    private final FilterFactory2 filterFactory;
    private final MutableStyleFactory styleFactory;
    private final MutableSLDFactory sldFactory;

    private final org.geotoolkit.se.xml.v110.ObjectFactory factorySEv110 = new org.geotoolkit.se.xml.v110.ObjectFactory();
    private final org.geotoolkit.ogc.xml.v100.ObjectFactory factoryOGCv100 = new org.geotoolkit.ogc.xml.v100.ObjectFactory();
    private final org.geotoolkit.ogc.xml.v110.ObjectFactory factoryOGCv110 = new org.geotoolkit.ogc.xml.v110.ObjectFactory();
    
    private SLD100toGTTransformer transformerGTv100 = null;
    private SLD110toGTTransformer transformerGTv110 = null;
    private GTtoSLD100Transformer transformerXMLv100 = null;
    private GTtoSLD110Transformer transformerXMLv110 = null;
    
    static{
        MarshallerPool temp = null;
        try{
            temp = new MarshallerPool(org.geotoolkit.sld.xml.v100.StyledLayerDescriptor.class);
        }catch(JAXBException ex){
            throw new RuntimeException("Could not load jaxbcontext for sld 100.",ex);
        }
        POOL_100 = temp;

        temp = null;
        try{
            temp = new MarshallerPool(org.geotoolkit.sld.xml.v110.StyledLayerDescriptor.class, org.geotoolkit.internal.jaxb.geometry.ObjectFactory.class);
        }catch(JAXBException ex){
            throw new RuntimeException("Could not load jaxbcontext for sld 110.",ex);
        }
        POOL_110 = temp;
    }

    public XMLUtilities() {
        final Hints hints = new Hints();
        hints.put(Hints.STYLE_FACTORY, MutableStyleFactory.class);
        hints.put(Hints.FILTER_FACTORY, FilterFactory2.class);
        this.styleFactory = (MutableStyleFactory)FactoryFinder.getStyleFactory(hints);
        this.filterFactory = (FilterFactory2) FactoryFinder.getFilterFactory(hints);
        this.sldFactory = new DefaultSLDFactory();
    }

    public XMLUtilities(FilterFactory2 filterFactory, MutableStyleFactory styleFactory, MutableSLDFactory sldFactory) {
        this.filterFactory = filterFactory;
        this.styleFactory = styleFactory;
        this.sldFactory = sldFactory;
    }

    public static MarshallerPool getJaxbContext100() {
        return POOL_100;
    }

    public static MarshallerPool getJaxbContext110() {
        return POOL_110;
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

    public SLD110toGTTransformer getTransformer110(Map<String, String> namespaceMapping){
        if (transformerGTv110 == null) {
            transformerGTv110 = new SLD110toGTTransformer(filterFactory, styleFactory, sldFactory, namespaceMapping);
        }
        return transformerGTv110;
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
        if(source instanceof File){
            return unMarshaller.unmarshal( (File)source );
        }else if(source instanceof InputSource){
            return unMarshaller.unmarshal( (InputSource)source );
        }else if(source instanceof InputStream){
            return unMarshaller.unmarshal( (InputStream)source );
        }else if(source instanceof Node){
            return unMarshaller.unmarshal( (Node)source );
        }else if(source instanceof Reader){
            return unMarshaller.unmarshal( (Reader)source );
        }else if(source instanceof Source){
            return unMarshaller.unmarshal( (Source)source );
        }else if(source instanceof URL){
            return unMarshaller.unmarshal( (URL)source );
        }else if(source instanceof XMLEventReader){
            return unMarshaller.unmarshal( (XMLEventReader)source );
        }else if(source instanceof XMLStreamReader){
            return unMarshaller.unmarshal( (XMLStreamReader)source );
        }else if(source instanceof OnlineResource){
            final OnlineResource online = (OnlineResource) source;
            try {
                final URL url = online.getLinkage().toURL();
                return unMarshaller.unmarshal(url);
            } catch (MalformedURLException ex) {
                Logging.getLogger(XMLUtilities.class).log(Level.WARNING, null, ex);
                return null;
            }
            
        }else{
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
        final Unmarshaller unMarshaller = POOL_100.acquireUnmarshaller();
        try {
            return unmarshall(source, unMarshaller);
        } finally {
            POOL_100.release(unMarshaller);
        }
    }
    
    private Object unmarshallV110(final Object source) throws JAXBException{
        if (transformerGTv110 == null) {
            transformerGTv110 = new SLD110toGTTransformer(filterFactory, styleFactory, sldFactory);
        }
        final Unmarshaller unMarshaller = POOL_110.acquireUnmarshaller();
        try {
            return unmarshall(source, unMarshaller);
        } finally {
            POOL_110.release(unMarshaller);
        }
    }
    
    private void marshall(final Object target, final Object jaxbElement, 
            final Marshaller marshaller) throws JAXBException{
        if(target instanceof File){
            marshaller.marshal(jaxbElement, (File)target );
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
        final Marshaller marshaller = POOL_100.acquireMarshaller();
        try {
            marshall(target, jaxElement, marshaller);
        } finally {
            POOL_100.release(marshaller);
        }

    }
    
    /**
     * This method do the same marshalling process like the first marshallV100 method with an option to format or not the output.
     * @param target
     * @param jaxElement
     * @param namespace
     * @param isformatted
     * @throws javax.xml.bind.JAXBException
     */
    private void marshallV100(final Object target, final Object jaxElement, boolean isformatted) throws JAXBException {
        final Marshaller marshaller = POOL_100.acquireMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, isformatted);
        try {
            marshall(target, jaxElement, marshaller);
        } finally {
            POOL_100.release(marshaller);
        }
    }
    
    private void marshallV110(final Object target, final Object jaxElement) throws JAXBException {
        final Marshaller marshaller = POOL_110.acquireMarshaller();
        try {
            marshall(target, jaxElement, marshaller);
        } finally {
            POOL_110.release(marshaller);
        }
    }
    
    
    // Styled Layer Descriptor -------------------------------------------------
    
    /**
     * Read a SLD source and parse it in GT SLD object.
     * Source can be : File, InputSource, InputStream, Node, Reader, Source, URL, 
     * XMLEventReader, XMLStreamReader or OnlineResource
     * 
     * @throws javax.xml.bind.JAXBException
     */
    public MutableStyledLayerDescriptor readSLD(final Object source, 
            final Specification.StyledLayerDescriptor version) throws JAXBException, FactoryException{
        
        if(source == null || version == null) throw new NullPointerException("Source and version can not be null");
        
        final Object obj;
        
        switch(version){
            case V_1_0_0:
                obj = unmarshallV100(source);
                if (obj instanceof org.geotoolkit.sld.xml.v100.StyledLayerDescriptor) {
                    final org.geotoolkit.sld.xml.v100.StyledLayerDescriptor tempsld = (org.geotoolkit.sld.xml.v100.StyledLayerDescriptor) obj;
                    if ("1.0.0".equals(tempsld.getVersion())) {
                        return transformerGTv100.visit((org.geotoolkit.sld.xml.v100.StyledLayerDescriptor) obj);
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
                        return transformerGTv110.visit((org.geotoolkit.sld.xml.v110.StyledLayerDescriptor) obj);
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
     * 
     * @throws javax.xml.bind.JAXBException
     */
    public void writeSLD(final Object target, final StyledLayerDescriptor sld, 
            final Specification.StyledLayerDescriptor version) throws JAXBException{
        if(target == null || sld == null || version == null) throw new NullPointerException("Source, SLD and version can not be null");
        
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
     * @throws javax.xml.bind.JAXBException
     */
    public void writeSLD(final Object target, final StyledLayerDescriptor sld, 
            final Specification.StyledLayerDescriptor version, boolean isformatted) throws JAXBException{
        if(target == null || sld == null || version == null) throw new NullPointerException("Source, SLD and version can not be null");
        
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
     * 
     * @throws javax.xml.bind.JAXBException
     */
    public MutableStyle readStyle(final Object source, 
            final Specification.SymbologyEncoding version) throws JAXBException, FactoryException{
        if(source == null || version == null) throw new NullPointerException("Source and version can not be null");
        
        final Object obj;
        
        switch(version){
            case SLD_1_0_0 :
                obj = unmarshallV100(source);
                if(obj instanceof org.geotoolkit.sld.xml.v100.UserStyle){
                    return transformerGTv100.visitUserStyle( (org.geotoolkit.sld.xml.v100.UserStyle) obj);
                }else{
                    throw new JAXBException("Source is not a valid OGC SLD UserStyle v1.0.0");
                }
            case V_1_1_0 :
                obj = unmarshallV110(source);
                if(obj instanceof org.geotoolkit.sld.xml.v110.UserStyle){
                    return transformerGTv110.visitUserStyle( (org.geotoolkit.sld.xml.v110.UserStyle) obj);
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
     * 
     * @throws javax.xml.bind.JAXBException
     */
    public void writeStyle(final Object target, final Style style, 
            final Specification.StyledLayerDescriptor version) throws JAXBException{
        if(target == null || style == null || version == null) throw new NullPointerException("Source, Style and version can not be null");
        
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
     * 
     * @throws javax.xml.bind.JAXBException
     */
    public MutableFeatureTypeStyle readFeatureTypeStyle(final Object source, 
            final Specification.SymbologyEncoding version) throws JAXBException, FactoryException{
        if(source == null || version == null) throw new NullPointerException("Source and version can not be null");
        
        final Object obj;
        
        switch(version){
            case SLD_1_0_0 :
                obj = unmarshallV100(source);
                if(obj instanceof org.geotoolkit.sld.xml.v100.FeatureTypeStyle){
                    return transformerGTv100.visitFTS( (org.geotoolkit.sld.xml.v100.FeatureTypeStyle) obj);
                }else{
                    throw new JAXBException("Source is not a valid OGC SLD FeatureTypeStyle v1.0.0");
                }
            case V_1_1_0 :
                obj = unmarshallV110(source);
                if(obj instanceof org.geotoolkit.se.xml.v110.FeatureTypeStyleType){
                    return transformerGTv110.visitFTS(obj);
                }else if(obj instanceof JAXBElement<?>&& (
                        ((JAXBElement<?>)obj).getValue() instanceof org.geotoolkit.se.xml.v110.OnlineResourceType ||
                        ((JAXBElement<?>)obj).getValue() instanceof org.geotoolkit.se.xml.v110.FeatureTypeStyleType ) ){
                    return transformerGTv110.visitFTS( ((JAXBElement<?>)obj).getValue() );
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
     * 
     * @throws javax.xml.bind.JAXBException
     */
    public void writeFeatureTypeStyle(final Object target, final FeatureTypeStyle fts, 
            final Specification.SymbologyEncoding version) throws JAXBException{
        if(target == null || fts == null || version == null) throw new NullPointerException("Source, FTS and version can not be null");
        
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
     * 
     * @throws javax.xml.bind.JAXBException
     */
    public MutableRule readRule(final Object source, 
            final Specification.SymbologyEncoding version) throws JAXBException, FactoryException{
        if(source == null || version == null) throw new NullPointerException("Source and version can not be null");
        
        final Object obj;
        
        switch(version){
            case SLD_1_0_0 :
                obj = unmarshallV100(source);
                if(obj instanceof org.geotoolkit.sld.xml.v100.Rule){
                    return transformerGTv100.visitRule( (org.geotoolkit.sld.xml.v100.Rule) obj);
                }else{
                    throw new JAXBException("Source is not a valid OGC SLD Rule v1.0.0");
                }
            case V_1_1_0 :
                obj = unmarshallV110(source);
                if(obj instanceof org.geotoolkit.se.xml.v110.RuleType){
                    return transformerGTv110.visitRule(obj);
                }else if(obj instanceof JAXBElement<?> && (
                        ((JAXBElement<?>)obj).getValue() instanceof org.geotoolkit.se.xml.v110.OnlineResourceType ||
                        ((JAXBElement<?>)obj).getValue() instanceof org.geotoolkit.se.xml.v110.RuleType ) ){
                    return transformerGTv110.visitRule( ((JAXBElement<?>)obj).getValue() );
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
     * 
     * @throws javax.xml.bind.JAXBException
     */
    public void writeRule(final Object target, final Rule rule, 
            final Specification.SymbologyEncoding version) throws JAXBException{
        if(target == null || rule == null || version == null) throw new NullPointerException("Source, FTS and version can not be null");
        
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
     * @throws javax.xml.bind.JAXBException
     */
    public SortBy readSortBy(final Object source,
            final Specification.Filter version) throws JAXBException{
        if(source == null || version == null) throw new NullPointerException("Source and version can not be null");

        final Object obj;

        switch(version){
            case V_1_0_0 :
                throw new JAXBException("SortBy doesnt exist in OGC Filter v1.0.0");
            case V_1_1_0 :
                obj = unmarshallV110(source);
                if(obj instanceof org.geotoolkit.ogc.xml.v110.SortByType){
//                    return transformerGTv110.visitFilter( (org.geotoolkit.ogc.xml.v110.FilterType) obj);
                }else{
                    throw new JAXBException("Source is not a valid OGC SortBy v1.1.0");
                }
            default :
                throw new IllegalArgumentException("Unable to read source, specified version is not supported");
        }

    }

    /**
     * Read a Filter source and parse it in GT Filter object.
     * Source can be : File, InputSource, InputStream, Node, Reader, Source, URL, 
     * XMLEventReader, XMLStreamReader or OnlineResource
     * 
     * @throws javax.xml.bind.JAXBException
     */
    public Filter readFilter(final Object source, 
            final Specification.Filter version) throws JAXBException, FactoryException{
        if(source == null || version == null) throw new NullPointerException("Source and version can not be null");
        
        final Object obj;
        
        switch(version){
            case V_1_0_0 :
                obj = unmarshallV100(source);
                if(obj instanceof org.geotoolkit.ogc.xml.v100.FilterType){
                    return transformerGTv100.visitFilter( (org.geotoolkit.ogc.xml.v100.FilterType) obj);
                }else if(obj instanceof JAXBElement<?> && 
                        ((JAXBElement<?>)obj).getValue() instanceof org.geotoolkit.ogc.xml.v100.FilterType){
                    return transformerGTv100.visitFilter( (org.geotoolkit.ogc.xml.v100.FilterType) ((JAXBElement<?>)obj).getValue() );
                }else{
                    throw new JAXBException("Source is not a valid OGC Filter v1.0.0");
                }
            case V_1_1_0 :
                obj = unmarshallV110(source);
                if(obj instanceof org.geotoolkit.ogc.xml.v110.FilterType){
                    return transformerGTv110.visitFilter( (org.geotoolkit.ogc.xml.v110.FilterType) obj);
                }else if(obj instanceof JAXBElement<?> && 
                         ((JAXBElement<?>)obj).getValue() instanceof org.geotoolkit.ogc.xml.v110.FilterType){
                    return transformerGTv110.visitFilter( (org.geotoolkit.ogc.xml.v110.FilterType) ((JAXBElement<?>)obj).getValue() );
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
     * 
     * @throws javax.xml.bind.JAXBException
     */
    public void writeFilter(final Object target, final Filter filter, 
            final Specification.Filter version) throws JAXBException{
        if(target == null || filter == null || version == null) throw new NullPointerException("Source, FTS and version can not be null");
        
        Object jax;
        
        switch(version){
            case V_1_0_0 :
                jax = getTransformerXMLv100().visit(filter);
                if(jax instanceof org.geotoolkit.ogc.xml.v100.FilterType){
                    jax = factoryOGCv100.createFilter( (org.geotoolkit.ogc.xml.v100.FilterType) jax);
                }
                marshallV100(target, jax);
                break;
            case V_1_1_0 :
                jax = getTransformerXMLv110().visit(filter);
                if(jax instanceof org.geotoolkit.ogc.xml.v110.FilterType){
                    jax = factoryOGCv110.createFilter( (org.geotoolkit.ogc.xml.v110.FilterType) jax);
                }
                marshallV110(target,jax);
                break;
            default :
                throw new IllegalArgumentException("Unable to write object, specified version is not supported");
        }
        
    }

    // OGC property ------------------------------------------------------------
    public PropertyName readPropertyName(final Object source,
            final Specification.Filter version) throws JAXBException{
        if(source == null || version == null) throw new NullPointerException("Source and version can not be null");

        final Object obj;

        switch(version){
            case V_1_0_0 :
                obj = unmarshallV100(source);
                if(obj instanceof PropertyNameType){
                    return transformerGTv100.visitPropertyName((org.geotoolkit.ogc.xml.v100.PropertyNameType) obj);
                }else{
                    throw new JAXBException("Source is not a valid OGC PropertyName v1.0.0");
                }
            case V_1_1_0 :
                obj = unmarshallV110(source);
                if(obj instanceof PropertyNameType){
                    return transformerGTv110.visitPropertyName((PropertyNameType) obj);
                }else{
                    throw new JAXBException("Source is not a valid OGC PropertyName v1.1.0");
                }
            default :
                throw new IllegalArgumentException("Unable to read source, specified version is not supported");
        }
    }

}
