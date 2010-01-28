/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.style.bank;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.sld.MutableLayer;
import org.geotoolkit.sld.MutableLayerStyle;
import org.geotoolkit.sld.MutableNamedLayer;
import org.geotoolkit.sld.MutableStyledLayerDescriptor;
import org.geotoolkit.sld.MutableUserLayer;
import org.geotoolkit.sld.xml.Specification.StyledLayerDescriptor;
import org.geotoolkit.sld.xml.Specification.SymbologyEncoding;
import org.geotoolkit.sld.xml.XMLUtilities;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.util.logging.Logging;
import org.opengis.referencing.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class XMLStyleBank implements StyleBank{

    private static final Logger LOGGER = Logging.getLogger(XMLStyleBank.class);
    private static final MutableStyleFactory SF = (MutableStyleFactory)FactoryFinder.getStyleFactory(
                            new Hints(Hints.STYLE_FACTORY, MutableStyleFactory.class));
    private static final Collection<String> MASKS = new ArrayList<String>();

    static{
        MASKS.add(".xml");
        MASKS.add(".sld");
    }

    private final XMLUtilities sldParser = new XMLUtilities();
    private final File folder;
    private ElementNode rootNode;


    public XMLStyleBank(File folder) {
        this.folder = folder;
    }

    @Override
    public synchronized ElementNode getRoot() {
        if(rootNode == null){
            rootNode = visit(folder, null);

        }

        return rootNode;
    }

    private ElementNode visit(File file, AbstractElementNode parent) {

        if (file.isDirectory()) {
            DefaultGroupNode node = new DefaultGroupNode(file.getName(), null);
            if(parent == null){
                parent = node;
            }else{
                parent.add(node);
            }

            final File[] list = file.listFiles();
            if (list != null) {
                for (int i = 0; i < list.length; i++) {
                    visit(list[i],node);
                }
            }
        }else if(parent != null){
            if(test(file)){
                XMLElementNode node = new XMLElementNode(file);
                parent.add(node);
            }
        }else{
            throw new IllegalArgumentException("XML Style bank must point to a Folder.");
        }

        return parent;
    }

    private boolean test(File candidate){
        final String fullName = candidate.getName();
        final String lowerCase = fullName.toLowerCase();

        //mac files
        if(lowerCase.startsWith(".")) return false;

        for(final String mask : MASKS){
            if(lowerCase.endsWith(mask)){
                return true;
            }
        }
        return false;
    }


    private MutableStyle parse(final File f) {
        MutableStyle value = null;

        if(f != null){
            final String baseErrorMsg = "[XML Style Bank]> SLD Style ";
            //try SLD 1.1
            try {
                final MutableStyledLayerDescriptor sld = sldParser.readSLD(f, StyledLayerDescriptor.V_1_1_0);
                value = getFirstStyle(sld);
                if(value != null){
                    LOGGER.log(Level.FINE, baseErrorMsg + f + " is an SLD 1.1.0");
                    return value;
                }
            } catch (JAXBException ex) { /* dont log*/ }
            catch (FactoryException ex) { /* dont log*/ }

            //try SLD 1.0
            try {
                final MutableStyledLayerDescriptor sld = sldParser.readSLD(f, StyledLayerDescriptor.V_1_0_0);
                value = getFirstStyle(sld);
                if(value != null){
                    LOGGER.log(Level.FINE, baseErrorMsg + f + " is an SLD 1.0.0");
                    return value;
                }
            } catch (JAXBException ex) { /*dont log*/ }
            catch (FactoryException ex) { /* dont log*/ }

            //try UserStyle SLD 1.1
            try {
                value = sldParser.readStyle(f, SymbologyEncoding.V_1_1_0);
                if(value != null){
                    LOGGER.log(Level.FINE, baseErrorMsg + f + " is a UserStyle SLD 1.1.0");
                    return value;
                }
            } catch (JAXBException ex) { /*dont log*/ }
            catch (FactoryException ex) { /* dont log*/ }

            //try UserStyle SLD 1.0
            try {
                value = sldParser.readStyle(f, SymbologyEncoding.SLD_1_0_0);
                if(value != null){
                    LOGGER.log(Level.FINE, baseErrorMsg + f + " is a UserStyle SLD 1.0.0");
                    return value;
                }
            } catch (JAXBException ex) { /*dont log*/ }
            catch (FactoryException ex) { /* dont log*/ }

            //try FeatureTypeStyle SE 1.1
            try {
                final MutableFeatureTypeStyle fts = sldParser.readFeatureTypeStyle(f, SymbologyEncoding.V_1_1_0);
                value = SF.style();
                value.featureTypeStyles().add(fts);
                if(value != null){
                    LOGGER.log(Level.FINE, baseErrorMsg + f + " is FeatureTypeStyle SE 1.1");
                    return value;
                }
            } catch (JAXBException ex) { /*dont log*/ }
            catch (FactoryException ex) { /* dont log*/ }

            //try FeatureTypeStyle SLD 1.0
            try {
                final MutableFeatureTypeStyle fts = sldParser.readFeatureTypeStyle(f, SymbologyEncoding.SLD_1_0_0);
                value = SF.style();
                value.featureTypeStyles().add(fts);
                if(value != null){
                    LOGGER.log(Level.FINE, baseErrorMsg + f + " is an FeatureTypeStyle SLD 1.0");
                    return value;
                }
            } catch (JAXBException ex) { /*dont log*/ }
            catch (FactoryException ex) { /* dont log*/ }

            LOGGER.log(Level.WARNING, baseErrorMsg + f + " could not be parsed");
        }

        return value;
    }

    private static MutableStyle getFirstStyle(final MutableStyledLayerDescriptor sld){
        if(sld == null) return null;
        for(final MutableLayer layer : sld.layers()){
            if(layer instanceof MutableNamedLayer){
                final MutableNamedLayer mnl = (MutableNamedLayer) layer;
                for(final MutableLayerStyle stl : mnl.styles()){
                    if(stl instanceof MutableStyle){
                        return (MutableStyle) stl;
                    }
                }
            }else if(layer instanceof MutableUserLayer){
                final MutableUserLayer mnl = (MutableUserLayer) layer;
                for(final MutableStyle stl : mnl.styles()){
                    return stl;
                }
            }
        }
        return null;
    }

    private final class XMLElementNode extends AbstractElementNode{

        private final File f;

        private XMLElementNode(File file){
            super(file.getName().substring(0, file.getName().length()-4), null, ElementType.STYLE);
            this.f = file;
        }
        
        @Override
        protected Object createUserObject() {
            return parse(f);
        }

    }

}
