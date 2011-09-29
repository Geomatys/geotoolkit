/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.gui.swing.go2.control.edition;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.memory.mapping.MappingUtils;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.GeotkClipboard;
import org.geotoolkit.util.logging.Logging;

import org.opengis.feature.Feature;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.opengis.util.NoSuchIdentifierException;

/**
 * Allow to copy geometry from clipboards
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class JClipboardPanel extends javax.swing.JPanel {

    private static final Logger LOGGER = Logging.getLogger(JClipboardPanel.class);
    
    public static final String GEOMETRY_PROPERTY = "geometry";
    
    private final WKTReader reader = new WKTReader();
    
    private Geometry original = null;
    private Geometry current = null;
    private CoordinateReferenceSystem crs = null;
    
    public JClipboardPanel() {
        initComponents();
    }
    
    public void setGeometry(Geometry geom){
        this.original = geom;
        this.current = null;
    }
    
    public Geometry getGeometry(){
        if(current == null){
            return original;
        }else{
            return current;
        }
    }

    public void setCrs(CoordinateReferenceSystem crs) {
        this.crs = crs;
    }

    public CoordinateReferenceSystem getCrs() {
        return crs;
    }
    
    private boolean checkClipboard(boolean system){
        
        Object candidate = null;
        if(system){
            //extract geometry from system clipboard
            candidate = GeotkClipboard.getSystemClipboardValue();
            candidate = adapt(candidate);
        }else{
            //extract geometry from application clipboard
            final Transferable trs = GeotkClipboard.INSTANCE.getContents(this);
            for(DataFlavor df : trs.getTransferDataFlavors()){                
                try {
                    candidate = trs.getTransferData(df);
                    candidate = adapt(candidate);
                } catch (UnsupportedFlavorException ex) {
                    LOGGER.log(Level.FINE, ex.getMessage(),ex);
                    continue;
                } catch (IOException ex) {
                    LOGGER.log(Level.FINE, ex.getMessage(),ex);
                    continue;
                }
                
                if(candidate != null){
                    //we found something in the clipboard which can be matched as a geometry
                    break;
                }                                
            }
        }
        
        
        if(candidate instanceof Geometry){
            current = (Geometry) candidate;
            
            //reproject geometry if necessary
            if(crs != null){
                try{
                    final CoordinateReferenceSystem currentCRS = JTS.findCoordinateReferenceSystem(current);
                    
                    if(currentCRS != null){
                        if(!CRS.equalsIgnoreMetadata(currentCRS, crs)){
                            final MathTransform trs = CRS.findMathTransform(currentCRS, crs);
                            current = JTS.transform(current, trs);
                        }
                    }
                    
                }catch(FactoryException ex){
                    LOGGER.log(Level.FINE, ex.getMessage(),ex);
                }catch(TransformException ex){
                    LOGGER.log(Level.FINE, ex.getMessage(),ex);
                }
            }
            
            return true;
        }
        
        //ensure the geometry type match
        if(current != null && original != null){
            current = MappingUtils.convertType(current,original.getClass());
        }
        
        return false;
    }
    
    /**
     * Try to convert extract a geometry from the given object
     * @return Geometry or null if failed to adapt candidate to a geometry
     */
    private Geometry adapt(Object candidate){        
        if(candidate == null){
            return null;
        }        
        if(candidate instanceof Geometry){
            //already a geometry
            return (Geometry) candidate;
        }
        
        Geometry result = null;
        
        if(candidate instanceof String){
            try {
                //try to parse it from WKT
                result = reader.read((String)candidate);
            } catch (ParseException ex) {
                LOGGER.log(Level.FINE, ex.getMessage(),ex);
            }
        }else if(candidate instanceof FeatureCollection){
            final FeatureCollection col = (FeatureCollection) candidate;
            //we must merge all geometries
            //we use the regroup process without any attribute define
            FeatureIterator ite = null;
            try{
                final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("vector", "regroup");
                final ParameterValueGroup in = desc.getInputDescriptor().createValue();
                in.parameter("feature_in").setValue(col);
                org.geotoolkit.process.Process proc = desc.createProcess(in);
                final FeatureCollection<?> featureListOut = (FeatureCollection<?>) proc.call().parameter("feature_out").getValue();
                ite = featureListOut.iterator();
                //we should have only one feature in this collection
                while(ite.hasNext()){
                    candidate = FeatureUtilities.deepCopy(ite.next());
                }
                
            }catch(NoSuchIdentifierException ex){
                LOGGER.log(Level.FINE, ex.getMessage(),ex);
            }catch(ProcessException ex){
                LOGGER.log(Level.FINE, ex.getMessage(),ex);
            }finally{
                if(ite != null){
                    ite.close();
                }
            }
        }else if(candidate instanceof Collection){
            final List<Geometry> geometries = new ArrayList<Geometry>();
            //try to extract a single geometry from each element
            for(Object o : (Collection)candidate){
                Geometry g = adapt(o);
                if(g != null){
                    geometries.add(g);
                }
            }
            
            //then merge the geometries
            if(!geometries.isEmpty()){
                result = geometries.get(0);
                
                while(geometries.size()>1){
                    Geometry second = geometries.remove(1);
                    final CoordinateReferenceSystem crs1 = getCRS(result);
                    final CoordinateReferenceSystem crs2 = getCRS(second);
                    if(crs1 != null && crs2 != null){
                        if(!CRS.equalsIgnoreMetadata(crs1, crs2)){
                            //reproject second geometry
                            try {
                                final MathTransform trs = CRS.findMathTransform(crs2, crs1);
                                second = JTS.transform(second, trs);
                            } catch (FactoryException ex) {
                                LOGGER.log(Level.FINE, ex.getMessage(),ex);
                            } catch (TransformException ex) {
                                LOGGER.log(Level.FINE, ex.getMessage(),ex);
                            }
                        }
                        
                        result = result.union(second);
                        JTS.setCRS(result, crs1);
                        
                    }else{
                        result = result.union(second);
                        if(crs1 != null){
                            JTS.setCRS(result, crs1);
                        }else if(crs2 != null){
                            JTS.setCRS(result, crs2);
                        }
                    }
                    
                }                
                
            }            
        }
        
        if(candidate instanceof Feature){
            final Feature f = (Feature) candidate;
            if(f.getDefaultGeometryProperty() != null){
                result = (Geometry) f.getDefaultGeometryProperty().getValue();
                //make a copy and ensure the crs is set
                result = (Geometry) result.clone();
                JTS.setCRS(result, f.getDefaultGeometryProperty().getType().getCoordinateReferenceSystem());
            }
        }
        
        
        return result;
    }
    
    private static CoordinateReferenceSystem getCRS(final Geometry geom){
        try {
            return JTS.findCoordinateReferenceSystem(geom);
        } catch (NoSuchAuthorityCodeException ex) {
            LOGGER.log(Level.FINE, ex.getMessage(),ex);
        } catch (FactoryException ex) {
            LOGGER.log(Level.FINE, ex.getMessage(),ex);
        }
        return null;
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        guiRollback = new javax.swing.JButton();
        guiApply = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        guiRollback.setText(MessageBundle.getString("paste")); // NOI18N
        guiRollback.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasteFromSys(evt);
            }
        });

        guiApply.setText(MessageBundle.getString("paste")); // NOI18N
        guiApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasteFromApp(evt);
            }
        });

        jLabel1.setText(MessageBundle.getString("applicationclipboard")); // NOI18N

        jLabel2.setText(MessageBundle.getString("systemclipboard")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                        .addComponent(guiApply))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                        .addComponent(guiRollback))))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {guiApply, guiRollback});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(guiApply)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(guiRollback, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void pasteFromApp(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pasteFromApp
        final Geometry old = getGeometry();
        if(checkClipboard(false)){
            firePropertyChange(GEOMETRY_PROPERTY, old, getGeometry());
        }
    }//GEN-LAST:event_pasteFromApp

    private void pasteFromSys(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pasteFromSys
        final Geometry old = getGeometry();
        if(checkClipboard(true)){
            firePropertyChange(GEOMETRY_PROPERTY, old, getGeometry());
        }
    }//GEN-LAST:event_pasteFromSys

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton guiApply;
    private javax.swing.JButton guiRollback;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables
}
