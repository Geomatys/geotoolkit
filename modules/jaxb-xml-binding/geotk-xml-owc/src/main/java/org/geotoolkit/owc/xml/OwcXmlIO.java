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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.georss.xml.v100.WhereType;
import org.geotoolkit.gml.xml.v311.DirectPositionType;
import org.geotoolkit.gml.xml.v311.EnvelopeType;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapItem;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.metadata.Citations;
import org.geotoolkit.owc.gtkext.ObjectFactory;
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
import org.geotoolkit.style.DefaultDescription;
import org.geotoolkit.style.MutableStyle;
import org.opengis.geometry.Envelope;
import org.opengis.style.Description;
import org.w3._2005.atom.IdType;
import org.w3._2005.atom.TextTypeType;
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

    private static final ObjectFactory GEOTK_FACTORY = new ObjectFactory();

    private static final OwcExtension[] EXTENSIONS;

    static {
        final ServiceLoader<OwcExtension> loader = ServiceLoader.load(OwcExtension.class);
        final Iterator<OwcExtension> ite = loader.iterator();
        final List<OwcExtension> lst = new ArrayList<>();
        while(ite.hasNext()) lst.add(ite.next());
        //sort by priority
        Collections.sort(lst,Collections.reverseOrder());
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
            final String ogc = IdentifiedObjects.lookupIdentifier(Citations.URN_OGC, aoi.getCoordinateReferenceSystem(), true);
            final WhereType where = GEORSS_FACTORY.createWhereType();
            final DirectPositionType lowerCorner = new DirectPositionType(aoi.getLowerCorner());
            final DirectPositionType upperCorner = new DirectPositionType(aoi.getUpperCorner());
            final EnvelopeType envelopeType = new EnvelopeType(null,
                    lowerCorner, upperCorner, ogc);
            envelopeType.setSrsDimension(2);
            where.setEnvelope(envelopeType);
            feed.getAuthorOrCategoryOrContributor().add(GEORSS_FACTORY.createWhere(where));
        }

        for(final MapItem mapItem : context.items()){
            toEntry(null, mapItem, feed.getAuthorOrCategoryOrContributor());
        }
        return feed;
    }

    private static void toEntry(String parentPath, final MapItem item, List entries){
        final EntryType entry = ATOM_FACTORY.createEntryType();
        entries.add(ATOM_FACTORY.createFeedTypeEntry(entry));

        //store other informations
        final String name = ((parentPath!=null)?parentPath:"") + item.getName();
        final Description description = item.getDescription();

        if(name!=null){
            final IdType atom = new IdType();
            atom.setValue(name);
            entry.getAuthorOrCategoryOrContent().add(ATOM_FACTORY.createEntryTypeId(atom));
        }

        if(description!=null && description.getTitle()!=null){
            final TextType atom = new TextType();
            atom.setType(TextTypeType.TEXT);
            atom.getContent().add(description.getTitle().toString());
            entry.getAuthorOrCategoryOrContent().add(ATOM_FACTORY.createEntryTypeTitle(atom));
        }

        if(description!=null && description.getAbstract()!=null){
            final TextType atom = new TextType();
            atom.setType(TextTypeType.TEXT);
            atom.getContent().add(description.getAbstract().toString());
            entry.getAuthorOrCategoryOrContent().add(ATOM_FACTORY.createEntryTypeSummary(atom));
        }


        if(item instanceof MapLayer){
            final MapLayer layer = (MapLayer) item;

            entry.getAuthorOrCategoryOrContent().add(GEOTK_FACTORY.createVisible(layer.isVisible()));
            entry.getAuthorOrCategoryOrContent().add(GEOTK_FACTORY.createSelectable(layer.isSelectable()));
            entry.getAuthorOrCategoryOrContent().add(GEOTK_FACTORY.createOpacity(layer.getOpacity()));

            OfferingType offering = null;
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
                toEntry(name+"/", child, entries);
            }
            entry.getAuthorOrCategoryOrContent().add(OWC_FACTORY.createOfferingTypeContent(content));
        }

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

    public static MapContext read(final Object input) throws JAXBException, FactoryException, DataStoreException{
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

    private static MapContext read(final FeedType feed) throws JAXBException, FactoryException, DataStoreException{
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
                final MapItem item = readEntry(entry);
                //find insert parent
                final String[] path = item.getName().split("/");
                MapItem parent = context;
                for(int i=0;i<path.length-1;i++){
                    parent = findItem(parent, path[i]);
                }
                item.setName(path[path.length-1]);
                parent.items().add(item);
            }
        }

        return context;
    }

    private static MapItem findItem(MapItem parent, String name){
        for(MapItem mi : parent.items()){
            if(mi.getName().equals(name)){
                return mi;
            }
        }
        //does not exist, create it
        final MapItem np = MapBuilder.createItem();
        parent.items().add(np);
        return np;
    }

    private static MapItem readEntry(final EntryType entry) throws JAXBException, FactoryException, DataStoreException{
        final List<Object> entryContent = entry.getAuthorOrCategoryOrContent();

        String layerName = "";
        String layerTitle = "";
        String layerAbstract = "";
        boolean visible = true;
        boolean selectable = true;
        double layerOpacity = 1.0;
        MapItem mapItem = null;
        MutableStyle baseStyle = null;
        MutableStyle selectionStyle = null;
        final List<MapItem> children = new ArrayList<>();

        for(Object o : entryContent){
            QName name = null;
            if(o instanceof JAXBElement){
                final JAXBElement jax = (JAXBElement) o;
                name = jax.getName();
                o = jax.getValue();

                if(GEOTK_FACTORY._Visible_QNAME.equals(name)){
                    visible = (Boolean)o;
                }else if(GEOTK_FACTORY._Selectable_QNAME.equals(name)){
                    selectable = (Boolean)o;
                }else if(GEOTK_FACTORY._Opacity_QNAME.equals(name)){
                    layerOpacity = (Double)o;
                }
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
            }else if(o instanceof IdType){
                final IdType idType = (IdType) o;
                final String value = idType.getValue();
                layerName = value;
            }else if(o instanceof TextType){
                final TextType tt = (TextType) o;
                if(ATOM_FACTORY._EntryTypeTitle_QNAME.equals(name)){
                    if(!tt.getContent().isEmpty()){
                        layerTitle = (String) tt.getContent().get(0);
                    }
                }else if(ATOM_FACTORY._EntryTypeSummary_QNAME.equals(name)){
                    if(!tt.getContent().isEmpty()){
                        layerAbstract = (String) tt.getContent().get(0);
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
            ((MapLayer)mapItem).setSelectable(selectable);
            ((MapLayer)mapItem).setOpacity(layerOpacity);
        }
        mapItem.setName(layerName);
        mapItem.setDescription(new DefaultDescription(
                new SimpleInternationalString(layerTitle),
                new SimpleInternationalString(layerAbstract)));
        mapItem.setName(layerName);
        mapItem.setVisible(visible);

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
