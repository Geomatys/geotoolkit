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
package org.geotoolkit.gui.swing.render2d.control;

import com.vividsolutions.jts.geom.Geometry;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import org.apache.sis.feature.FeatureExt;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.query.QueryUtilities;
import org.geotoolkit.display.container.GraphicContainer;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.gui.swing.render2d.JMap2D;
import org.geotoolkit.gui.swing.render2d.control.selection.DefaultSelectionHandler;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.gui.swing.util.FeatureCollectionListTransferable;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.util.GeotkClipboard;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.filter.Filter;
import org.openide.awt.DropDownButtonFactory;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class JSelectionBar extends AbstractMapControlBar implements ActionListener{

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.gui.swing.render2d.control");

    private static final ImageIcon ICON_SELECT = IconBuilder.createIcon(FontAwesomeIcons.ICON_LOCATION_ARROW, 16, FontAwesomeIcons.DEFAULT_COLOR);
    private static final ImageIcon ICON_INTERSECT = IconBundle.getIcon("16_select_intersect");
    private static final ImageIcon ICON_WITHIN = IconBundle.getIcon("16_select_within");
    private static final ImageIcon ICON_LASSO = IconBundle.getIcon("16_select_lasso");
    private static final ImageIcon ICON_SQUARE = IconBundle.getIcon("16_select_square");
    private static final ImageIcon ICON_GEOGRAPHIC = IconBundle.getIcon("16_zoom_all");
    private static final ImageIcon ICON_VISUAL = IconBundle.getIcon("16_visible");

    private final ButtonGroup groupClip = new ButtonGroup();
    private final ButtonGroup groupZone = new ButtonGroup();
    private final ButtonGroup groupVisit = new ButtonGroup();

    private final JButton guiSelect;
    private final JRadioButtonMenuItem guiIntersect = new JRadioButtonMenuItem(MessageBundle.format("select_intersect"),ICON_INTERSECT);
    private final JRadioButtonMenuItem guiWithin = new JRadioButtonMenuItem(MessageBundle.format("select_within"),ICON_WITHIN);
    private final JRadioButtonMenuItem guiLasso = new JRadioButtonMenuItem(MessageBundle.format("select_lasso"),ICON_LASSO);
    private final JRadioButtonMenuItem guiSquare = new JRadioButtonMenuItem(MessageBundle.format("select_square"),ICON_SQUARE);
    private final JRadioButtonMenuItem guiGeographic = new JRadioButtonMenuItem(MessageBundle.format("select_geographic"),ICON_GEOGRAPHIC);
    private final JRadioButtonMenuItem guiVisual = new JRadioButtonMenuItem(MessageBundle.format("select_visual"),ICON_VISUAL);

    private final DefaultSelectionHandler handler = new DefaultSelectionHandler();

    /**
     * Creates a new instance of JMap2DControlBar
     */
    public JSelectionBar() {
        this(null);
    }

    /**
     * Creates a new instance of JMap2DControlBar
     * @param pane : related Map2D or null
     */
    public JSelectionBar(final JMap2D map) {

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
        menu.add(new JMenuItem(new AbstractAction(MessageBundle.format("copyselection")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyToClipboard(true,false);
            }
        }));
        menu.add(new JMenuItem(new AbstractAction(MessageBundle.format("copyselectionappend")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyToClipboard(true,true);
            }
        }));

        guiSelect = DropDownButtonFactory.createDropDownButton(ICON_SELECT, menu);
        guiSelect.setToolTipText(MessageBundle.format("map_select"));
        setMap(map);

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
        final GraphicContainer container = map.getCanvas().getContainer();

        if(container instanceof ContextContainer2D){
            final ContextContainer2D cc = (ContextContainer2D) container;
            final MapContext context = cc.getContext();

            final List<FeatureCollection> selections = new ArrayList<>();
            final StringBuilder sb = new StringBuilder();
            for(MapLayer layer : context.layers()){
                if(layer instanceof FeatureMapLayer){
                    final FeatureMapLayer fml = (FeatureMapLayer) layer;
                    final Filter selection = fml.getSelectionFilter();
                    if(selection != null && selection != Filter.EXCLUDE){
                        final Query sub = QueryUtilities.subQuery(fml.getQuery(), QueryBuilder.filtered("select", selection));
                        FeatureIterator ite = null;
                        try {
                            final FeatureCollection col = fml.getCollection().subCollection(sub);
                            selections.add(col);
                            if(systemclipboard){
                                ite = col.iterator();
                                while(ite.hasNext()){
                                    final Feature f = ite.next();
                                    final Object gt = FeatureExt.getDefaultGeometryAttributeValue(f);
                                    if(gt instanceof Geometry){
                                        sb.append(gt.toString());
                                        sb.append("\n");
                                    }
                                }
                            }
                        } catch (DataStoreException | FeatureStoreRuntimeException ex) {
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
            }

            //push value in geotk clipboard
            Transferable trs = GeotkClipboard.INSTANCE.getContents(this);
            if(append && trs instanceof FeatureCollectionListTransferable){
                final List lst = ((FeatureCollectionListTransferable)trs).getSelections();
                lst.addAll(selections);
            }else{
                trs = new FeatureCollectionListTransferable(selections);
                GeotkClipboard.INSTANCE.setContents(trs, null);
            }

        }
    }

}
