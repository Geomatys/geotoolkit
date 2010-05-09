/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
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
package org.geotoolkit.gui.swing.crschooser;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.geotoolkit.internal.SwingUtilities;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.referencing.CRS;

import org.jdesktop.swingx.JXList;

import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * CRS list
 * 
 * @author Johann Sorel
 * @module pending
 */
public class JCRSList extends JComponent{

    private final AuthorityFactory factory;
    
    private CodeList codeList;    
    private JXList liste = new JXList();
    private CoordinateReferenceSystem selectedCRS = null;
    
    public JCRSList(){

        //obtain the factory only for epsg codes
//        String authority = "EPSG";
//        this.factory = FallbackAuthorityFactory.create(CRSAuthorityFactory.class,
//             filter(AuthorityFactoryFinder.getCRSAuthorityFactories(null), authority));

        this.factory = CRS.getAuthorityFactory(Boolean.FALSE);

        try{
            this.codeList = new CodeList(factory, CoordinateReferenceSystem.class);
            liste.setModel(codeList);
        }catch(FactoryException e){
            e.printStackTrace();
        }
                        
        liste.getSelectionModel().setSelectionMode(liste.getSelectionModel().SINGLE_SELECTION);
        
        setLayout(new GridLayout());
        add(BorderLayout.CENTER,new JScrollPane(liste));
        
        liste.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                int index = e.getFirstIndex();
                
                if(index>=0){
                    try{
                        selectedCRS = (CoordinateReferenceSystem) getSelectedItem();
                    }catch(FactoryException ex){
                        ex.printStackTrace();
                    }
                }
            }
        });        
        
    }
    
    public void addListSelectionListener(ListSelectionListener lst){
        liste.getSelectionModel().addListSelectionListener(lst);
    }
    
    public void removeListSelectionListener(ListSelectionListener lst){
        liste.getSelectionModel().removeListSelectionListener(lst);
    }
    
    
    public void setCRS(CoordinateReferenceSystem crs){
        selectedCRS = crs;       
    }
            
    
    public void searchCRS(String searchword){
        filter(searchword);
    }
    
    public CoordinateReferenceSystem getCRS(){
        return selectedCRS;
    }
    
    
    /**
     * Returns the code for the selected object, or {@code null} if none.
     * @return 
     */
    public String getSelectedCode() {
        final Code code = (Code) liste.getSelectedValue();
        return (code != null) ? code.code : null;
    }

    /**
     * Returns the selected object, usually as a {@link CoordinateReferenceSystem}.
     *
     * @return 
     * @throws FactoryException if the factory can't create the selected object.
     */
    public IdentifiedObject getSelectedItem() throws FactoryException {
        final String code = getSelectedCode();
        return (code != null) ? factory.createObject(code) : null;
    }
    
    
    /**
     * Display only the CRS name that contains the specified keywords. The {@code keywords}
     * argument is a space-separated list, usually provided by the user after he pressed the
     * "Search" button.
     *
     * @param keywords space-separated list of keywords to look for.
     */
    private void filter(String keywords) {
        ListModel model = codeList;
        if (keywords != null) {
            final Locale locale = Locale.getDefault();
            keywords = keywords.toLowerCase(locale).trim();
            final String[] tokens = keywords.split("\\s+");
            if (tokens.length != 0) {
                final DefaultListModel filtered;
                model = filtered = new DefaultListModel();
                final int size = codeList.getSize();
        scan:   for (int i=0; i<size; i++) {
                    final Code code = (Code) codeList.getElementAt(i);
                    final String name = code.toString().toLowerCase(locale);
                    for (int j=0; j<tokens.length; j++) {
                        if (name.indexOf(tokens[j]) < 0) {
                            continue scan;
                        }
                    }
                    filtered.addElement(code);
                }
            }
        }
        liste.setModel(model);
    }
    
    
    
    /**
     * Returns a collection containing only the factories of the specified authority.
     */
    private static Collection<CRSAuthorityFactory> filter(
            final Collection<? extends CRSAuthorityFactory> factories, final String authority)
    {
        final List<CRSAuthorityFactory> filtered = new ArrayList<CRSAuthorityFactory>();
        for (final CRSAuthorityFactory factory : factories) {
            if (Citations.identifierMatches(factory.getAuthority(), authority)) {
                filtered.add(factory);
            }
        }
        return filtered;
    }
    
}
