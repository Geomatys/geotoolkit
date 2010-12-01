/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Johann Sorel
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

package org.geotoolkit.gui.swing.propertyedit;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.storage.DataStoreException;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class JFeatureCollectionOutline extends JPanel{

    private final JFeatureOutLine outline = new JFeatureOutLine();
    private final JLabel lbl = new JLabel("");
    private final JButton previous = new JButton(new AbstractAction(" < ") {
            @Override
            public void actionPerformed(ActionEvent e) {
                previous();
            }
        });
    private final JButton next = new JButton(new AbstractAction(" > ") {
            @Override
            public void actionPerformed(ActionEvent e) {
                next();
            }
        });

    private FeatureCollection collection = null;
    private int index = -1;
    private int size = -1;
    
    public JFeatureCollectionOutline() {
        super(new BorderLayout());
        
        final JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));

        bottom.add(previous);
        bottom.add(next);
        bottom.add(lbl);
        
        add(BorderLayout.CENTER,new JScrollPane(outline));
        add(BorderLayout.SOUTH,bottom);
    }
    
    public void setTarget(FeatureCollection collection){
        this.collection = collection;
        index = -1;
        size = collection.size();
        if(size > 0){
            next();
        }
    }
    
    public FeatureCollection getTarget(){
        return collection;
    }
    
    private void next(){
        if(index >= size){
            return;
        }
        index++;
        updateVisibleFeature();
    }
    
    private void previous(){
        if(index <= 0){
            return;
        }
        index--;
        updateVisibleFeature();
    }
    
    private void updateVisibleFeature(){
        lbl.setText(index+1 +" / " + size);

        next.setEnabled(index+1 < size && size > 0);
        previous.setEnabled(index > 0 && size > 0);


        final QueryBuilder qb = new QueryBuilder(collection.getFeatureType().getName());
        qb.setStartIndex(index);
        qb.setMaxFeatures(1);
        try {
            final FeatureCollection col = collection.subCollection(qb.buildQuery());
            final FeatureIterator ite = col.iterator();
            try{
                if(ite.hasNext()){
                    outline.setEdited(ite.next());
                }
            }finally{
                ite.close();
            }
        } catch (DataStoreException ex) {
            Logger.getLogger(JFeatureCollectionOutline.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
