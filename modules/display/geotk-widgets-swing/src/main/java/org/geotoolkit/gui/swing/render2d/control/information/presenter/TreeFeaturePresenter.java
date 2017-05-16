package org.geotoolkit.gui.swing.render2d.control.information.presenter;

import com.vividsolutions.jts.geom.Geometry;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreeModel;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.gui.swing.propertyedit.FeatureTreeModel;
import org.geotoolkit.gui.swing.propertyedit.JFeatureOutLine;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.gui.swing.tree.MutableTreeNode;
import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.RowModel;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class TreeFeaturePresenter extends AbstractInformationPresenter {

    public static final int CELL_HEIGHT = 32;
    public static final int BTN_WIDTH = 25;
    public static final int LABEL_WIDTH = 100;
    public static final int LABEL_HEIGHT = 23;
    public static Border DEFAULT_BORDER = BorderFactory.createEmptyBorder(4, 4, 4, 4);

    public TreeFeaturePresenter() {
        super(0);
    }

    @Override
    public JComponent createComponent(Object graphic, RenderingContext2D context, SearchAreaJ2D area) {

        final Object candidate;
        if (graphic instanceof ProjectedFeature) {
            final ProjectedFeature gra = (ProjectedFeature) graphic;
            candidate = gra.getCandidate();
        } else if (graphic instanceof GraphicJ2D) {
            final GraphicJ2D gra = (GraphicJ2D) graphic;
            candidate = gra.getUserObject();
        } else {
            candidate = null;
        }

        if (candidate instanceof Feature) {
            final TreeModel model = new FeatureTreeModel((Feature) candidate);
            final Outline tree = new Outline(DefaultOutlineModel.createOutlineModel(model, new FeatureRowModel()));
            tree.setRenderDataProvider(new JFeatureOutLine.PropertyDataProvider());
            tree.setRootVisible(false);
            tree.setRowHeight(CELL_HEIGHT);
//            tree.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            tree.setBorder(DEFAULT_BORDER);
            tree.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tree.setShowHorizontalLines(false);
            tree.getColumnModel().getColumn(1).setCellRenderer(new ValueRenderer());
            final ClipCopy btnRenderer = new ClipCopy();

            final TableColumn first  = tree.getColumnModel().getColumn(0);
            final TableColumn second = tree.getColumnModel().getColumn(1);
            final TableColumn third  = tree.getColumnModel().getColumn(2);

            first.setResizable(true);
            first.setMinWidth(LABEL_WIDTH);

            second.setResizable(true);
            second.setMinWidth(LABEL_WIDTH);

            third.setCellRenderer(btnRenderer);
            third.setCellEditor(btnRenderer);
            third.setResizable(false);
            third.setMaxWidth(BTN_WIDTH);
            third.setMinWidth(BTN_WIDTH);
            third.setPreferredWidth(BTN_WIDTH);

            return tree;
        }
        return null;
    }


    public static class FeatureRowModel implements RowModel {

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueFor(Object o, int i) {
            if (o instanceof MutableTreeNode) {
                final MutableTreeNode node = (MutableTreeNode) o;
                if (node.getUserObject() instanceof Property) {
                    return ((Property) node.getUserObject()).getValue();
                }
            }
            return "N/A";
        }

        @Override
        public Class getColumnClass(int i) {
            return Object.class;
        }

        @Override
        public boolean isCellEditable(Object o, int i) {
            if (i < 1) {
                return false;
            }
            return true;
        }

        @Override
        public void setValueFor(Object o, int i, Object o1) {}

        @Override
        public String getColumnName(int i) {
            if (i == 0) {
                return "Value";
            } else {
                return "";
            }
        }
    }


    public static class ValueRenderer implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value != null) {
                final String valueStr = value.toString();
                final JLabel label = new JLabel();
                label.setBorder(DEFAULT_BORDER);
                label.setMinimumSize(new Dimension(LABEL_WIDTH, LABEL_HEIGHT));
                if (value instanceof Geometry) {
                    label.setText(((Geometry) value).getGeometryType());
                } else {
                    label.setText(valueStr);
                }
                label.setToolTipText(valueStr);
                return label;
            }
            return null;
        }
    }


    public static class ClipCopy extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {

        final JButton button;
        StringSelection selection = null;

        public ClipCopy() {
            button = new JButton(IconBuilder.createIcon(FontAwesomeIcons.ICON_FILES_O, BTN_WIDTH, FontAwesomeIcons.DEFAULT_COLOR));
            button.setToolTipText("Copy value to clipboard.");
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setMaximumSize(button.getPreferredSize());
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (selection != null) {
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(selection, selection);
                    }
                }
            });
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return button;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (value != null) {
                selection = new StringSelection(value.toString());
            } else {
                selection = null;
            }
            return button;
        }
    }


}
