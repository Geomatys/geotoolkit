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
package org.geotoolkit.owc.xml;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.georss.xml.v100.WhereType;
import org.geotoolkit.gml.xml.v311.DirectPositionType;
import org.geotoolkit.gml.xml.v311.EnvelopeType;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapItem;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.owc.xml.v10.ContentType;
import org.geotoolkit.owc.xml.v10.OfferingType;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.opengis.util.FactoryException;
import org.w3._2005.atom.EntryType;
import org.w3._2005.atom.FeedType;
import org.w3._2005.atom.LinkType;
import org.w3._2005.atom.TextType;
import static org.geotoolkit.owc.xml.OwcMarshallerPool.*;
import org.geotoolkit.owc.xml.v10.StyleSetType;
import org.geotoolkit.sld.xml.Specification;
import org.geotoolkit.sld.xml.StyleXmlIO;
import org.geotoolkit.sld.xml.v110.UserStyle;
import org.geotoolkit.style.MutableStyle;
import org.opengis.geometry.Envelope;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;

/**
 * Read and write MapContext objects.
 * 
 * @author Samuel Andrés (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class OwcXmlIO {
    
    private static final OwcExtension[] EXTENSIONS;
    
    static {
        final ServiceLoader<OwcExtension> loader = ServiceLoader.load(OwcExtension.class);
        final Iterator<OwcExtension> ite = loader.iterator();
        final List<OwcExtension> lst = new ArrayList<>();
        while(ite.hasNext()) lst.add(ite.next());
        EXTENSIONS = lst.toArray(new OwcExtension[0]);
    }
    
    public static OwcExtension[] getExtensions() {
        return EXTENSIONS.clone();
    }

    public static void write(final Object output, final MapContext context) throws PropertyException, JAXBException, FactoryException{
        final FeedType feed = write(context);
        final MarshallerPool pool = OwcMarshallerPool.getPool();
        
        final Marshaller marshaller = pool.acquireMarshaller();
        try{
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            if(output instanceof ContentHandler) marshaller.marshal(feed, (ContentHandler)output);
            else if(output instanceof File) marshaller.marshal(feed, (File)output);
            else if(output instanceof Node) marshaller.marshal(feed, (Node)output);
            else if(output instanceof OutputStream) marshaller.marshal(feed, (OutputStream)output);
            else if(output instanceof Result) marshaller.marshal(feed, (Result)output);
            else if(output instanceof Writer) marshaller.marshal(feed, (Writer)output);
            else if(output instanceof XMLEventWriter) marshaller.marshal(feed, (XMLEventWriter)output);
            else if(output instanceof XMLStreamWriter) marshaller.marshal(feed, (XMLStreamWriter)output);
            else{
                throw new JAXBException("Unsupported output type : "+output);
            }
        }finally{
            pool.recycle(marshaller);
        }
    }
    
    private static FeedType write(final MapContext context) throws FactoryException{
        final FeedType feed = ATOM_FACTORY.createFeedType();
        
        final LinkType link = ATOM_FACTORY.createLinkType();
        link.setRel("profile");
        link.setHref("http://www.opengis.net/spec/owc-atom/1.0/req/core");
        link.setTitle(context.getName()==null ? "" : context.getName());
        feed.getAuthorOrCategoryOrContributor().add(ATOM_FACTORY.createFeedTypeLink(link));
        
        final TextType title = ATOM_FACTORY.createTextType();
        title.getContent().add(context.getName()==null ? "" : context.getName());
        feed.getAuthorOrCategoryOrContributor().add(ATOM_FACTORY.createFeedTypeTitle(title));
        
        final Envelope aoi = context.getAreaOfInterest();
        if(aoi!=null){
            final Integer epsg = IdentifiedObjects.lookupEpsgCode(aoi.getCoordinateReferenceSystem(), true);
            final WhereType where = GEORSS_FACTORY.createWhereType();
            final DirectPositionType lowerCorner = new DirectPositionType(aoi.getLowerCorner());
            final DirectPositionType upperCorner = new DirectPositionType(aoi.getUpperCorner());
            final EnvelopeType envelopeType = new EnvelopeType(null, 
                    lowerCorner, upperCorner, "EPSG:"+epsg);
            envelopeType.setSrsDimension(2);
            where.setEnvelope(envelopeType);
            feed.getAuthorOrCategoryOrContributor().add(GEORSS_FACTORY.createWhere(where));
        }
        
        for(final MapItem mapItem : context.items()){
            feed.getAuthorOrCategoryOrContributor().add(ATOM_FACTORY.createFeedTypeEntry(toEntry(mapItem)));
        }
        return feed;
    }
        
    private static EntryType toEntry(final MapItem item){
        final EntryType entry = ATOM_FACTORY.createEntryType();
        
        if(item instanceof MapLayer){
            OfferingType offering = null;
            final MapLayer layer = (MapLayer) item;
            for(OwcExtension ext : getExtensions()){
                if(ext.canHandle(layer)){
                    offering = ext.createOffering(layer);
                    entry.getAuthorOrCategoryOrContent().add(OWC_FACTORY.createOffering(offering));
                    break;
                }
            }
            
            //store styles
            if(offering!=null){
                if(layer.getStyle()!=null){
                    final StyleSetType styleBase = toStyleSet(layer.getStyle(), true);
                    offering.getOperationOrContentOrStyleSet().add(OWC_FACTORY.createOfferingTypeStyleSet(styleBase));
                }
                if(layer.getSelectionStyle()!=null){
                    final StyleSetType styleSelection = toStyleSet(layer.getSelectionStyle(), false);
                    offering.getOperationOrContentOrStyleSet().add(OWC_FACTORY.createOfferingTypeStyleSet(styleSelection));
                }
            }
            
        }else{
            final ContentType content = OWC_FACTORY.createContentType();
            content.setType(item.getName());
            //encode children
            for(MapItem child : item.items()){
                content.getContent().add(ATOM_FACTORY.createEntry(toEntry(child)));
            }
            entry.getAuthorOrCategoryOrContent().add(OWC_FACTORY.createOfferingTypeContent(content));
        }
        
        return entry;
    }
    
    private static StyleSetType toStyleSet(MutableStyle style, boolean def){
        final StyleSetType styleSet = OWC_FACTORY.createStyleSetType();
        styleSet.setDefault(def);
        
        final ContentType content = OWC_FACTORY.createContentType();
        final StyleXmlIO io = new StyleXmlIO();
        final UserStyle jaxbStyle = io.getTransformerXMLv110().visit(style, null);
        content.getContent().add(jaxbStyle);
        
        styleSet.getNameOrTitleOrAbstract().add(OWC_FACTORY.createStyleSetTypeContent(content));
        
        return styleSet;
    }
    
    public static MapContext read(final Object input) throws JAXBException, FactoryException{
        final MarshallerPool pool = OwcMarshallerPool.getPool();
        final Unmarshaller unmarshaller = pool.acquireUnmarshaller();
        
        final FeedType feed;
        try{
            final Object jax;
            if(input instanceof File) jax = unmarshaller.unmarshal((File)input);
            else if(input instanceof Node) jax = unmarshaller.unmarshal((Node)input);
            else if(input instanceof InputSource) jax = unmarshaller.unmarshal((InputSource)input);
            else if(input instanceof InputStream) jax = unmarshaller.unmarshal((InputStream)input);
            else if(input instanceof Source) jax = unmarshaller.unmarshal((Source)input);
            else if(input instanceof Reader) jax = unmarshaller.unmarshal((Reader)input);
            else if(input instanceof XMLEventReader) jax = unmarshaller.unmarshal((XMLEventReader)input);
            else if(input instanceof XMLStreamReader) jax = unmarshaller.unmarshal((XMLStreamReader)input);
            else{
                throw new JAXBException("Unsupported input type : "+input);
            }
            
            feed = (FeedType) ((JAXBElement)jax).getValue();
        }finally{
            pool.recycle(unmarshaller);
        }
        return read(feed);
    }
    
    private static MapContext read(final FeedType feed) throws JAXBException, FactoryException{
        final MapContext context = MapBuilder.createContext();
        
        for(Object o : feed.getAuthorOrCategoryOrContributor()){
            if(o instanceof JAXBElement){
                o = ((JAXBElement)o).getValue();
            }
            
            if(o instanceof TextType){
                final TextType title = (TextType) o;
                title.getContent();
            }else if(o instanceof WhereType){
                final WhereType where = (WhereType) o;
                final EnvelopeType envelopeType = where.getEnvelope();
                context.setAreaOfInterest(envelopeType);
            }else if(o instanceof EntryType){
                final EntryType entry = (EntryType) o;
                context.items().add(readEntry(entry));
            }
        }
        
        return context;
    }
        
    private static MapItem readEntry(final EntryType entry) throws JAXBException, FactoryException{
        final List<Object> entryContent = entry.getAuthorOrCategoryOrContent();
        
        MapItem mapItem = null;
        MutableStyle baseStyle = null;
        MutableStyle selectionStyle = null;
        final List<MapItem> children = new ArrayList<>();
        
        for(Object o : entryContent){
            if(o instanceof JAXBElement){
                o = ((JAXBElement)o).getValue();
            }
            
            if(o instanceof OfferingType){
                final OfferingType offering = (OfferingType) o;
                for(OwcExtension ext : getExtensions()){
                    if(ext.getCode().equals(offering.getCode())){
                        mapItem = ext.createLayer(offering);
                        break;
                    }
                }
                
                //search for styles
                baseStyle = readStyle(offering,true);
                selectionStyle = readStyle(offering,false);
                
            }else if(o instanceof ContentType){
                //decode children
                final ContentType content = (ContentType) o;
                final List<Object> contentContent = content.getContent();
                for(Object co : contentContent){
                    if(co instanceof JAXBElement){
                        co = ((JAXBElement)o).getValue();
                    }
                    if(co instanceof EntryType){
                        children.add(readEntry((EntryType)co));
                    }
                }
            }
        }
        
        if(mapItem==null){
            mapItem = MapBuilder.createItem();
        }else if(mapItem instanceof MapLayer){
            if(baseStyle!=null){
                ((MapLayer)mapItem).setStyle(baseStyle);
            }
            if(selectionStyle!=null){
                ((MapLayer)mapItem).setSelectionStyle(selectionStyle);
            }
        }
        mapItem.items().addAll(children);
        
        return mapItem;
    }
    
    private static MutableStyle readStyle(OfferingType offering, boolean def) throws JAXBException, FactoryException{
        final List<Object> content = offering.getOperationOrContentOrStyleSet();
        for(Object co : content){
            if(co instanceof JAXBElement) co = ((JAXBElement)co).getValue();
            if(!(co instanceof StyleSetType)) continue;
            
            final StyleSetType sst = (StyleSetType)co;
            if(sst.isDefault() != def) continue;
            
            final List<Object> ssc = sst.getNameOrTitleOrAbstract();
            
            for(Object ss : ssc){
                if(ss instanceof JAXBElement) ss = ((JAXBElement)ss).getValue();
                if(!(ss instanceof ContentType)) continue;
                
                final ContentType ct = (ContentType) ss;
                final List<Object> subcs = ct.getContent();
                for(Object subc : subcs){
                    if(subc instanceof JAXBElement) subc = ((JAXBElement)subc).getValue();
                    if(!(subc instanceof UserStyle)) continue;
                    
                    final StyleXmlIO io = new StyleXmlIO();
                    return io.readStyle(subc, Specification.SymbologyEncoding.V_1_1_0);
                }
            }
        }
        return null;
    } 
    
}
