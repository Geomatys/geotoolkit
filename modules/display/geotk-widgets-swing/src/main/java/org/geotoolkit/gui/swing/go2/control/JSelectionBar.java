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
package org.geotoolkit.gui.swing.go2.control;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.query.QueryUtilities;
import org.geotoolkit.display.container.AbstractContainer2D;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.go2.control.selection.DefaultSelectionHandler;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.GeotkClipboard;
import org.geotoolkit.util.logging.Logging;
import org.opengis.filter.Filter;

/**
 * 
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class JSelectionBar extends AbstractMapControlBar implements ActionListener{

    private static final Logger LOGGER = Logging.getLogger(JSelectionBar.class);
    
    private static final ImageIcon ICON_SELECT = IconBundle.getIcon("16_select");
    private static final ImageIcon ICON_CONFIG = IconBundle.getIcon("16_vertical_next");
    private static final ImageIcon ICON_INTERSECT = IconBundle.getIcon("16_select_intersect");
    private static final ImageIcon ICON_WITHIN = IconBundle.getIcon("16_select_within");
    private static final ImageIcon ICON_LASSO = IconBundle.getIcon("16_select_lasso");
    private static final ImageIcon ICON_SQUARE = IconBundle.getIcon("16_select_square");
    private static final ImageIcon ICON_GEOGRAPHIC = IconBundle.getIcon("16_zoom_all");
    private static final ImageIcon ICON_VISUAL = IconBundle.getIcon("16_visible");

    private final ButtonGroup groupClip = new ButtonGroup();
    private final ButtonGroup groupZone = new ButtonGroup();
    private final ButtonGroup groupVisit = new ButtonGroup();

    private final JButton guiSelect = new JButton(ICON_SELECT);
    private final JLabel guiConfig = new JLabel(" ",ICON_CONFIG, SwingConstants.RIGHT);
    private final JRadioButtonMenuItem guiIntersect = new JRadioButtonMenuItem(MessageBundle.getString("select_intersect"),ICON_INTERSECT);
    private final JRadioButtonMenuItem guiWithin = new JRadioButtonMenuItem(MessageBundle.getString("select_within"),ICON_WITHIN);
    private final JRadioButtonMenuItem guiLasso = new JRadioButtonMenuItem(MessageBundle.getString("select_lasso"),ICON_LASSO);
    private final JRadioButtonMenuItem guiSquare = new JRadioButtonMenuItem(MessageBundle.getString("select_square"),ICON_SQUARE);
    private final JRadioButtonMenuItem guiGeographic = new JRadioButtonMenuItem(MessageBundle.getString("select_geographic"),ICON_GEOGRAPHIC);
    private final JRadioButtonMenuItem guiVisual = new JRadioButtonMenuItem(MessageBundle.getString("select_visual"),ICON_VISUAL);

    private final DefaultSelectionHandler handler = new DefaultSelectionHandler();

    /**
     * Creates a new instance of JMap2DControlBar
     */
    public JSelectionBar() {
        this(null);

        guiSelect.setToolTipText(MessageBundle.getString("map_select"));
        guiConfig.setToolTipText(MessageBundle.getString("map_select_config"));
    }

    /**
     * Creates a new instance of JMap2DControlBar
     * @param pane : related Map2D or null
     */
    public JSelectionBar(final JMap2D map) {
        setMap(map);

        final JPopupMenu menu = new JPopupMenu();
        menu.add(guiLasso);
        menu.add(guiSquare);
        menu.add(new JSeparator(SwingConstants.HORIZONTAL));
        menu.add(guiIntersect);
        menu.add(guiWithin);
        menu.add(new JSeparator(SwingConstants.HORIZONTAL));
        menu.add(guiGeographic);
        menu.add(guiVisual);
        menu.add(new JSeparator(SwingConstants.HORIZONTAL));
        menu.add(new JMenuItem(new AbstractAction(MessageBundle.getString("copyselection")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyToClipboard(false,false);
            }
        }));
        menu.add(new JMenuItem(new AbstractAction(MessageBundle.getString("copyselectionappend")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyToClipboard(false,true);
            }
        }));

        guiConfig.setComponentPopupMenu(menu);
        guiConfig.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if(event.getButton() == MouseEvent.BUTTON1){
                    menu.show(guiConfig.getParent(), guiConfig.getX(), guiConfig.getY()+guiConfig.getHeight());
                }
            }
            @Override
            public void mousePressed(MouseEvent arg0) {}
            @Override
            public void mouseReleased(MouseEvent arg0) {}
            @Override
            public void mouseEntered(MouseEvent arg0) {}
            @Override
            public void mouseExited(MouseEvent arg0) {}
        });
        handler.setMenu(menu);

        guiIntersect.setSelected(true);
        groupClip.add(guiIntersect);
        groupClip.add(guiWithin);

        guiSquare.setSelected(true);
        groupZone.add(guiLasso);
        groupZone.add(guiSquare);

        guiVisual.setSelected(true);
        groupVisit.add(guiVisual);
        groupVisit.add(guiGeographic);

        guiSelect.addActionListener(this);
        guiIntersect.addActionListener(this);
        guiWithin.addActionListener(this);
        guiLasso.addActionListener(this);
        guiSquare.addActionListener(this);
        guiGeographic.addActionListener(this);
        guiVisual.addActionListener(this);

        add(guiSelect);
        add(guiConfig);
        
    }

    @Override
    public void setMap(final JMap2D map2d) {
        super.setMap(map2d);
        guiSelect.setEnabled(map != null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(map == null) return;
        handler.setMap(map);
        handler.setGeographicArea(guiGeographic.isSelected());
        handler.setSquareArea(guiSquare.isSelected());
        handler.setWithinArea(guiWithin.isSelected());
        map.setHandler(handler);
    }

    private void copyToClipboard(boolean systemclipboard, boolean append){
        final AbstractContainer2D container = map.getCanvas().getContainer();

        if(container instanceof ContextContainer2D){
            final ContextContainer2D cc = (ContextContainer2D) container;
            final MapContext context = cc.getContext();
            
            final List<FeatureCollection> selections = new ArrayList<FeatureCollection>();
            final StringBuilder sb = new StringBuilder();
            for(MapLayer layer : context.layers()){
                if(layer instanceof FeatureMapLayer){
                    final FeatureMapLayer fml = (FeatureMapLayer) layer;
                    final Filter selection = fml.getSelectionFilter();
                    if(selection != null && selection != Filter.EXCLUDE){
                        final Query sub = QueryUtilities.subQuery(fml.getQuery(), QueryBuilder.filtered(new DefaultName("select"), selection));
                        FeatureIterator ite = null;
                        try {
                            final FeatureCollection col = fml.getCollection().subCollection(sub);
                            selections.add(col);
                            if(systemclipboard){
                                ite = col.iterator();
                                while(ite.hasNext()){
                                    sb.append(ite.next());
                                    sb.append("\n");
                                }
                            }
                        } catch (DataStoreException ex) {
                            LOGGER.log(Level.WARNING, ex.getMessage(),ex);
                        } catch (DataStoreRuntimeException ex) {
                            LOGGER.log(Level.WARNING, ex.getMessage(),ex);
                        }finally {
                            if(ite != null){
                                ite.close();
                            }
                        }
                    }
                }
            }
            
            if(systemclipboard){
                GeotkClipboard.setSystemClipboardValue(sb.toString());
            }else{
                Transferable trs = GeotkClipboard.INSTANCE.getContents(this);
                
                if(append && trs instanceof FeatureCollectionListTransferable){
                    final List lst = ((FeatureCollectionListTransferable)trs).selections;
                    lst.addAll(selections);                    
                }else{
                    trs = new FeatureCollectionListTransferable(selections);
                    GeotkClipboard.INSTANCE.setContents(trs, null);
                }
            }
            
        }
    }
    
    
    private static class FeatureCollectionListTransferable implements Transferable{

        private static final String MIME = "geotk/featurecollectionList";
        private static final DataFlavor FLAVOR = new DataFlavor(List.class,MIME);
        private final List<FeatureCollection> selections;

        public FeatureCollectionListTransferable(List<FeatureCollection> selections) {
            this.selections = selections;
        }
        
        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{FLAVOR};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return FLAVOR.match(flavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            return selections;
        }
    
    }
    
}
