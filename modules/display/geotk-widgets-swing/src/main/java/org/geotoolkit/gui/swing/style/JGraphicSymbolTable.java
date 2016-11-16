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
package org.geotoolkit.gui.swing.style;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.service.DefaultGlyphService;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.gui.swing.util.ActionCell;
import org.geotoolkit.gui.swing.util.JOptionDialog;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.StyleConstants;
import org.jdesktop.swingx.JXTable;
import org.opengis.metadata.citation.OnlineResource;
import org.opengis.style.ExternalGraphic;
import org.opengis.style.Graphic;
import org.opengis.style.GraphicalSymbol;
import org.opengis.style.Mark;
import org.opengis.style.PointSymbolizer;
import org.opengis.util.InternationalString;

/**
 * graphic symbol table
 *
 * @author Johann Sorel
 * @module
 */
public class JGraphicSymbolTable <T> extends StyleElementEditor<List> {

    private static final Icon ICO_ADD = IconBuilder.createIcon(FontAwesomeIcons.ICON_PLUS, 16, FontAwesomeIcons.DEFAULT_COLOR);
    private static final Icon ICO_EDIT = IconBuilder.createIcon(FontAwesomeIcons.ICON_PENCIL, 16, FontAwesomeIcons.DEFAULT_COLOR);
    private static final ImageIcon ICO_UP = IconBuilder.createIcon(FontAwesomeIcons.ICON_CHEVRON_UP, 16, FontAwesomeIcons.DEFAULT_COLOR);
    private static final ImageIcon ICO_DOWN = IconBuilder.createIcon(FontAwesomeIcons.ICON_CHEVRON_DOWN, 16, FontAwesomeIcons.DEFAULT_COLOR);
    private static final ImageIcon ICO_DELETE = IconBuilder.createIcon(FontAwesomeIcons.ICON_TRASH_O, 16, FontAwesomeIcons.DEFAULT_COLOR);

    private MapLayer layer = null;
    private final GraphicalModel model = new GraphicalModel(null);

    public JGraphicSymbolTable() {
        super(List.class);
        initComponents();

        tabGraphics.setTableHeader(null);
        tabGraphics.setModel(model);
        tabGraphics.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tabGraphics.getColumn(0).setCellRenderer(new GraphicRenderer());

        tabGraphics.getColumn(1).setCellRenderer(new ActionCell.Renderer(ICO_UP));
        tabGraphics.getColumn(1).setCellEditor(new ActionCell.Editor(ICO_UP) {
            @Override
            public void actionPerformed(final ActionEvent e, Object value) {
                final GraphicalSymbol graphic = (GraphicalSymbol) value;
                model.moveUp(graphic);
            }
        });

        tabGraphics.getColumn(2).setCellRenderer(new ActionCell.Renderer(ICO_DOWN));
        tabGraphics.getColumn(2).setCellEditor(new ActionCell.Editor(ICO_DOWN) {
            @Override
            public void actionPerformed(final ActionEvent e, Object value) {
                final GraphicalSymbol graphic = (GraphicalSymbol) value;
                model.moveDown(graphic);
            }
        });

        tabGraphics.getColumn(3).setCellRenderer(new ActionCell.Renderer(ICO_EDIT));
        tabGraphics.getColumn(3).setCellEditor(new ActionCell.Editor(ICO_EDIT) {
            @Override
            public void actionPerformed(final ActionEvent e, Object value) {
                GraphicalSymbol symbol = (GraphicalSymbol) value;
                final GraphicalSymbol oldMark = symbol;

                final StyleElementEditor editor;
                if(symbol instanceof Mark){
                    editor = new JMarkPane();
                    editor.parse(value);
                }else{
                    editor = new JExternalGraphicPane();
                    editor.parse(value);
                }
                editor.setLayer(layer);

                final int res = JOptionDialog.show(JGraphicSymbolTable.this, editor, JOptionPane.OK_CANCEL_OPTION);
                if(JOptionPane.OK_OPTION == res){
                    symbol = (GraphicalSymbol) editor.create();
                    final List<GraphicalSymbol> symbols = model.getGraphics();
                    symbols.add(symbols.indexOf(oldMark),symbol);
                    symbols.remove(oldMark);
                    model.setGraphics(symbols);
                }
            }
        });

        tabGraphics.getColumn(4).setCellRenderer(new ActionCell.Renderer(ICO_DELETE));
        tabGraphics.getColumn(4).setCellEditor(new ActionCell.Editor(ICO_DELETE) {
            @Override
            public void actionPerformed(final ActionEvent e, Object value) {
                final GraphicalSymbol graphic = (GraphicalSymbol) value;
                model.deleteGraphical(graphic);
            }
        });

        final int width = 30;
        tabGraphics.getColumn(1).setMinWidth(width);
        tabGraphics.getColumn(1).setPreferredWidth(width);
        tabGraphics.getColumn(1).setMaxWidth(width);
        tabGraphics.getColumn(2).setMinWidth(width);
        tabGraphics.getColumn(2).setPreferredWidth(width);
        tabGraphics.getColumn(2).setMaxWidth(width);
        tabGraphics.getColumn(3).setMinWidth(width);
        tabGraphics.getColumn(3).setPreferredWidth(width);
        tabGraphics.getColumn(3).setMaxWidth(width);
        tabGraphics.getColumn(4).setMinWidth(width);
        tabGraphics.getColumn(4).setPreferredWidth(width);
        tabGraphics.getColumn(4).setMaxWidth(width);
        tabGraphics.setTableHeader(null);
        tabGraphics.setRowHeight(30);
        tabGraphics.setFillsViewportHeight(true);
        tabGraphics.setBackground(Color.WHITE);
        tabGraphics.setShowGrid(true);
        tabGraphics.setShowHorizontalLines(true);
        tabGraphics.setShowVerticalLines(false);

         model.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                firePropertyChange(PROPERTY_UPDATED, null, create());
            }
        });

    }

    @Override
    public void setLayer(final MapLayer layer) {
        this.layer = layer;
    }

    @Override
    public MapLayer getLayer() {
        return layer;
    }

    @Override
    public void parse(final List graphics) {
        model.setGraphics(graphics);
    }

    @Override
    public List<GraphicalSymbol> create() {
        return model.getGraphics();
    }

    @Override
    protected Object[] getFirstColumnComponents() {
        return new Object[]{};
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new JPanel();
        guiAddMark = new JButton();
        guiAddExternal = new JButton();
        jScrollPane2 = new JScrollPane();
        tabGraphics = new JXTable();

        setOpaque(false);
        setLayout(new BorderLayout());

        jPanel1.setLayout(new GridLayout(1, 2, 10, 0));

        guiAddMark.setIcon(ICO_ADD);
        guiAddMark.setText(MessageBundle.format("new_mark")); // NOI18N
        guiAddMark.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                guiAddMarkActionPerformed(evt);
            }
        });
        jPanel1.add(guiAddMark);

        guiAddExternal.setIcon(ICO_ADD);
        guiAddExternal.setText(MessageBundle.format("new_external")); // NOI18N
        guiAddExternal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                guiAddExternalActionPerformed(evt);
            }
        });
        jPanel1.add(guiAddExternal);

        add(jPanel1, BorderLayout.SOUTH);

        jScrollPane2.setViewportView(tabGraphics);

        add(jScrollPane2, BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void guiAddMarkActionPerformed(ActionEvent evt) {//GEN-FIRST:event_guiAddMarkActionPerformed
        model.newMark();
    }//GEN-LAST:event_guiAddMarkActionPerformed

    private void guiAddExternalActionPerformed(ActionEvent evt) {//GEN-FIRST:event_guiAddExternalActionPerformed
        model.newExternal();
    }//GEN-LAST:event_guiAddExternalActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton guiAddExternal;
    private JButton guiAddMark;
    private JPanel jPanel1;
    private JScrollPane jScrollPane2;
    private JXTable tabGraphics;
    // End of variables declaration//GEN-END:variables


    private static class GraphicalModel extends AbstractTableModel {

        private final List<GraphicalSymbol> graphics = new ArrayList<GraphicalSymbol>();

        GraphicalModel(final List<GraphicalSymbol> graphs) {
            if (graphs != null) {
                this.graphics.addAll(graphs);
            }
        }

        public void newMark() {
            final GraphicalSymbol m = getStyleFactory().mark(
                    getFilterFactory().literal("circle"),
                    StyleConstants.DEFAULT_FILL,
                    StyleConstants.DEFAULT_STROKE);

            graphics.add(m);
            int last = graphics.size() - 1;
            fireTableRowsInserted(last, last);
        }

        public void newExternal() {
            final GraphicalSymbol m;
            try {
                m = getStyleFactory().externalGraphic(new URL("file:/..."), "image/png");
            } catch (MalformedURLException ex) {
                //won't happen
                throw new RuntimeException(ex.getMessage(),ex);
            }

            graphics.add(m);
            int last = graphics.size() - 1;
            fireTableRowsInserted(last, last);
        }

        public void deleteGraphical(final GraphicalSymbol graphic) {
            final int index = graphics.indexOf(graphic);
            if (index >= 0) {
                graphics.remove(index);
                fireTableRowsDeleted(index, index);
            }
        }

        public void moveUp(final GraphicalSymbol m) {
            int index = graphics.indexOf(m);
            if (index != 0) {
                graphics.remove(m);
                graphics.add(index - 1, m);
                fireTableDataChanged();
            }
        }

        public void moveDown(final GraphicalSymbol m) {
            int index = graphics.indexOf(m);
            if (index != graphics.size() - 1) {
                graphics.remove(m);
                graphics.add(index + 1, m);
                fireTableDataChanged();
            }
        }

        public void setGraphics(final List<GraphicalSymbol> marks) {
            if(this.graphics.equals(marks)){
                //nothing changed
                return;
            }
            this.graphics.clear();
            this.graphics.addAll(marks);
            fireTableDataChanged();
        }

        public List<GraphicalSymbol> getGraphics() {
            return new ArrayList<GraphicalSymbol>(this.graphics);
        }

        @Override
        public int getRowCount() {
            return graphics.size();
        }

        @Override
        public int getColumnCount() {
            return 5;
        }

        @Override
        public boolean isCellEditable(final int rowIndex, final int columnIndex) {
            return columnIndex > 0;
        }

        @Override
        public Class<?> getColumnClass(final int columnIndex) {
            return Mark.class;
        }

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            return graphics.get(rowIndex);
        }
    }

    private static class GraphicRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {

            final JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
            lbl.setIcon(null);

            if(value instanceof GraphicalSymbol){
                final List<GraphicalSymbol> lst = new ArrayList<>();
                lst.add((GraphicalSymbol)value);

                final Graphic gra = GO2Utilities.STYLE_FACTORY.graphic(
                        lst,
                        StyleConstants.DEFAULT_GRAPHIC_OPACITY,
                        GO2Utilities.FILTER_FACTORY.literal(16),
                        StyleConstants.DEFAULT_GRAPHIC_ROTATION,
                        StyleConstants.DEFAULT_ANCHOR_POINT,
                        StyleConstants.DEFAULT_DISPLACEMENT);

                final PointSymbolizer ps = GO2Utilities.STYLE_FACTORY.pointSymbolizer(gra, null);
                final BufferedImage image = DefaultGlyphService.create(ps, new Dimension(18, 18), null);
                lbl.setIcon(new ImageIcon(image));
            }

            if (value instanceof Mark) {
                final Mark m = (Mark) value;
                lbl.setText(m.getWellKnownName().toString());
            } else if (value instanceof ExternalGraphic) {
                final ExternalGraphic m = (ExternalGraphic) value;
                final OnlineResource res = m.getOnlineResource();
                if(res != null && res.getLinkage() != null){
                    final InternationalString name = res.getName();
                    lbl.setText(name != null ? name.toString() : null);
                }else{
                    lbl.setText("");
                }
            }
            return lbl;
        }
    }

}
