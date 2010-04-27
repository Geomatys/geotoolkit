/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
package org.geotoolkit.gui.swing.coverage;

import java.util.Set;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Arrays;
import java.util.ArrayList;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.FieldPosition;

import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.BorderFactory;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXTitledPanel;

import javax.measure.unit.Unit;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.metadata.extent.GeographicBoundingBox;

import org.geotoolkit.math.XMath;
import org.geotoolkit.util.DateRange;
import org.geotoolkit.util.MeasurementRange;
import org.geotoolkit.measure.Units;
import org.geotoolkit.measure.Angle;
import org.geotoolkit.measure.Latitude;
import org.geotoolkit.measure.Longitude;
import org.geotoolkit.measure.AngleFormat;
import org.geotoolkit.measure.RangeFormat;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.sql.CoverageDatabase;
import org.geotoolkit.coverage.sql.FutureQuery;
import org.geotoolkit.coverage.sql.Layer;
import org.geotoolkit.gui.swing.ExceptionMonitor;
import org.geotoolkit.internal.swing.ArrayListModel;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.resources.Widgets;


/**
 * A widget displaying the {@linkplain CoverageDatabase#getLayers() list of layers} available
 * in a coverage database. In the lower part of the panel, a description of the selected layer
 * is displayed.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.11
 *
 * @since 3.11
 * @module
 */
@SuppressWarnings("serial")
public class LayerList extends JPanel {
    /**
     * The default width and height.
     */
    private static final int WIDTH = 600, HEIGHT = 400;

    /**
     * The database for which to display the list of available layers.
     */
    protected final CoverageDatabase database;

    /**
     * The list of layers.
     */
    private final ArrayListModel<String> layers;

    /**
     * The widget which display the list of selected layers.
     */
    private final JList layerList;

    /**
     * The panel which display the currently selected layer.
     */
    private final JXTitledPanel layerProperties;

    /**
     * The coverage domain.
     */
    private final JLabel west, east, north, south, timeRange, typicalResolution;

    /**
     * The panel which contains the west, east, north and south bounds.
     */
    private final JPanel geographicPanel;

    /**
     * The list of elevations.
     */
    private final ArrayListModel<String> elevations;

    /**
     * A coma-separated list of formats.
     */
    private final JLabel imageFormat;

    /**
     * Range of sample values.
     */
    private final JLabel sampleValueRanges;

    /**
     * The format to use for the time range.
     */
    private final DateFormat dateFormat;

    /**
     * The format to use for elevations. This format is used in a background thread,
     * so access to this instance need to be synchronized.
     */
    private final NumberFormat heightFormat;

    /**
     * The format to use for latitudes and longitudes.
     */
    private final AngleFormat angleFormat;

    /**
     * The format to use for formatting range of values.
     */
    private final RangeFormat rangeFormat;

    /**
     * Creates a new {@code LayerList} instance for the given database.
     *
     * @param database The database for which to display the list of available layers.
     */
    public LayerList(final CoverageDatabase database) {
        super(new BorderLayout());
        this.database = database;
        /*
         * Localized resources.
         */
        final Locale locale = getLocale();
        final Vocabulary resources = Vocabulary.getResources(locale);
        dateFormat   = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
        heightFormat = NumberFormat.getNumberInstance(locale);
        angleFormat  = AngleFormat.getInstance(locale);
        rangeFormat  = RangeFormat.getInstance(locale);
        /*
         * List of layers.
         */
        layers = new ArrayListModel<String>();
        layerList = new JList(layers);
        layerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setLayerNames(database.getLayers());
        layerList.addListSelectionListener(new ListSelectionListener() {
            @Override public void valueChanged(final ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()) {
                    setSelectedLayer((String) layerList.getSelectedValue());
                }
            }
        });
        final JXTitledPanel layerPanel = new JXTitledPanel(
                resources.getString(Vocabulary.Keys.LAYERS), new JScrollPane(layerList));
        /*
         * List of available elevations.
         */
        elevations = new ArrayListModel<String>();
        final JList elevationList = new JList(elevations);
        elevationList.setLayoutOrientation(JList.VERTICAL_WRAP);
        elevationList.setVisibleRowCount(0); // Will be calculated from the list height.
        final ListCellRenderer renderer = elevationList.getCellRenderer();
        if (renderer instanceof JLabel) {
            ((JLabel) renderer).setHorizontalAlignment(JLabel.RIGHT);
        }
        /*
         * The envelope of the currently selected layer.
         */
        west  = new JLabel((String) null, JLabel.CENTER);
        east  = new JLabel((String) null, JLabel.CENTER);
        north = new JLabel((String) null, JLabel.CENTER);
        south = new JLabel((String) null, JLabel.CENTER);
        geographicPanel = new JPanel(new GridLayout(3, 3));
        geographicPanel.add(new JPanel()); geographicPanel.add(north);
        geographicPanel.add(new JPanel()); geographicPanel.add(west);
        geographicPanel.add(new JPanel()); geographicPanel.add(east);
        geographicPanel.add(new JPanel()); geographicPanel.add(south);
        geographicPanel.add(new JPanel());
        geographicPanel.setVisible(false);
        geographicPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(resources.getString(Vocabulary.Keys.GEOGRAPHIC_COORDINATES)),
                BorderFactory.createEmptyBorder(9, 9, 9, 9)));
        /*
         * The time range of the currently selected layer.
         * The resolution of the currently selected layer.
         */
        timeRange = createField(resources, Vocabulary.Keys.TIME_RANGE);
        typicalResolution = createField(resources, Vocabulary.Keys.RESOLUTION);
        final JPanel domainPane = combine(geographicPanel, timeRange, typicalResolution);
        /*
         * The list of formats.
         * The range of sample values.
         */
        imageFormat = createField(resources, Vocabulary.Keys.DECODERS);
        sampleValueRanges = createField(resources, Vocabulary.Keys.VALUE_RANGE);
        final JPanel formatPane = combine(imageFormat, sampleValueRanges);
        /*
         * Properties of the currently selected layer.
         * Include the list of available elevations.
         */
        final JTabbedPane propertiesTabs = new JTabbedPane();
        propertiesTabs.addTab(resources.getString(Vocabulary.Keys.DOMAIN), domainPane);
        propertiesTabs.addTab(resources.getString(Vocabulary.Keys.FORMAT), formatPane);
        propertiesTabs.addTab(resources.getString(Vocabulary.Keys.ALTITUDES), new JScrollPane(elevationList));
        layerProperties = new JXTitledPanel(getPropertiesTitle(null), propertiesTabs);
        /*
         * The central component. Include the list of layers
         * and the properties of the selected layer.
         */
        final JSplitPane layerAndProps = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, layerPanel, layerProperties);
        layerAndProps.setContinuousLayout(true);
        layerAndProps.setResizeWeight(1);
        layerAndProps.setDividerLocation(WIDTH - 320);
        layerAndProps.setBorder(BorderFactory.createEmptyBorder(9, 9, 9, 9));
        /*
         * The buttons bar.
         */
        final JButton refresh  = new JButton(resources.getString(Vocabulary.Keys.REFRESH));
        final JButton showCov  = new JButton(resources.getString(Vocabulary.Keys.IMAGE_LIST));
        final JButton addLayer = new JButton(resources.getString(Vocabulary.Keys.ADD));
        final JPanel buttonBar = new JPanel(new GridLayout(1, 3));
        refresh.addActionListener(new ActionListener() {
            @Override public void actionPerformed(final ActionEvent event) {
                refresh();
            }
        });
        buttonBar.add(refresh);
        buttonBar.add(showCov);
        buttonBar.add(addLayer);
        showCov .setEnabled(false);
        addLayer.setEnabled(false);
        buttonBar.setBorder(BorderFactory.createEmptyBorder(9, 15, 9, 15));
        /*
         * Put the components in this panel.
         */
        add(layerAndProps, BorderLayout.CENTER);
        add(buttonBar, BorderLayout.AFTER_LAST_LINE);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
    }

    /**
     * Creates an initially hiden field in which to write a layer properties.
     *
     * @return The field where to write the property value.
     */
    private static JLabel createField(final Vocabulary resources, final int key) {
        final JLabel label = new JLabel();
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(resources.getString(key)),
                BorderFactory.createEmptyBorder(0, 15, 0, 0)));
        label.setVisible(false);
        return label;
    }

    /**
     * Put the given component together in a single panel.
     */
    private static JPanel combine(final JComponent... components) {
        final JPanel pane = new JPanel(new GridBagLayout());
        final GridBagConstraints c = new GridBagConstraints();
        c.anchor  = GridBagConstraints.WEST;
        c.fill    = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets.left = c.insets.right = 6;
        c.gridx=0; c.gridy=0; c.insets.top = 12;
        for (final JComponent component : components) {
            pane.add(component, c);
            c.gridy++;
        }
        c.weighty=1; c.fill=GridBagConstraints.BOTH;
        pane.add(new JPanel(), c); // For filling empty space.
        return pane;
    }

    /**
     * Refreshes the list of layer names. This method shall be invoked if the
     * database content changed. The refresh action will be run in a background thread.
     */
    public void refresh() {
        setLayerNames(database.getLayers());
        setSelectedLayer((String) layerList.getSelectedValue());
    }

    /**
     * Refreshes the list of layer names with the result of the given task.
     * The given taks shall be the value returned by {@link CoverageDatabase#getLayers()}.
     *
     * @param task The task which is computing the list of layer names.
     */
    private void setLayerNames(final FutureQuery<Set<String>> task) {
        task.invokeAfterCompletion(new Runnable() {
            /** The names of all layers. */
            private String[] names;

            /** Get the names of all layers, then pass them to the JList in the Swing thread. */
            @Override public synchronized void run() {
                if (names == null) try {
                    final Set<String> layerNames = task.result();
                    names = layerNames.toArray(new String[layerNames.size()]);
                    Arrays.sort(names, String.CASE_INSENSITIVE_ORDER);
                } catch (CoverageStoreException ex) {
                    ExceptionMonitor.show(LayerList.this, ex);
                    return;
                }
                if (!EventQueue.isDispatchThread()) {
                    EventQueue.invokeLater(this);
                } else {
                    layers.setElements(names);
                }
            }
        });
    }

    /**
     * Returns the title to write in the properties panel for the given layer name.
     *
     * @param  layer The name of the layer for which to get the title, or {@code null}.
     * @return The title (never null).
     */
    private String getPropertiesTitle(String layer) {
        final Vocabulary resources = Vocabulary.getResources(getLocale());
        if (layer == null) {
            layer = '(' + resources.getString(Vocabulary.Keys.NONE) + ')';
        }
        return resources.getString(Vocabulary.Keys.PROPERTIES_OF_$1, layer);
    }

    /**
     * Sets the values to show in the <cite>layer properties</cite> panel.
     *
     * @param layer      The name of the currently selected layer, or {@code null} if none.
     * @param bbox       The geographic bounding box, or {@code null} if none.
     * @param startTime  The start time to format, or {@code null} if none.
     * @param endTime    The end time to format, or {@code null} if none.
     * @param resolution The typical resolution, or {@code null} if none.
     */
    final void setProperties(final String layer, final GeographicBoundingBox bbox,
            final Date startTime, final Date endTime, final double[] resolution,
            final Set<String> formats, final List<MeasurementRange<?>> ranges)
    {
        layerProperties.setTitle(getPropertiesTitle(layer));
        if (bbox != null) {
            west .setText(angleFormat.format(new Longitude(bbox.getWestBoundLongitude())));
            east .setText(angleFormat.format(new Longitude(bbox.getEastBoundLongitude())));
            north.setText(angleFormat.format(new Latitude (bbox.getNorthBoundLatitude())));
            south.setText(angleFormat.format(new Latitude (bbox.getSouthBoundLatitude())));
        }
        geographicPanel.setVisible(bbox != null);
        String text = null;
        if (startTime != null && endTime != null) {
            text = Widgets.getResources(getLocale()).getString(Widgets.Keys.TIME_RANGE_$2,
                    dateFormat.format(startTime), dateFormat.format(endTime));
        }
        setText(timeRange, text);
        /*
         * Resolution.
         */
        text = null;
        if (resolution != null) {
            final CoordinateSystem cs = database.getCoordinateReferenceSystem().getCoordinateSystem();
            final StringBuffer buffer = new StringBuffer();
            for (int i=0; i<resolution.length; i++) {
                final double r = resolution[i];
                if (!Double.isNaN(r)) {
                    final CoordinateSystemAxis axis = cs.getAxis(i);
                    final Unit<?> unit = axis.getUnit();
                    final Object value = Units.isAngular(unit) ? new Angle(r) : Double.valueOf(r);
                    if (buffer.length() != 0) {
                        buffer.append(" \u00D7 "); // Multiplication symbol.
                    }
                    angleFormat.format(value, buffer, null);
                    if (value instanceof Number) {
                        // Do not format the unit symbol for angles.
                        buffer.append(' ').append(unit);
                    }
                }
            }
            text = buffer.toString();
        }
        setText(typicalResolution, text);
        /*
         * Format.
         */
        text = null;
        if (formats != null && !formats.isEmpty()) {
            text = formats.toString();
            text = text.substring(1, text.length() - 1); // For removing the brackets.
        }
        setText(imageFormat, text);
        text = null;
        if (ranges != null && !ranges.isEmpty()) {
            boolean hasMore = false;
            final StringBuffer buffer = new StringBuffer("<html>");
            final FieldPosition pos = new FieldPosition(0);
            for (final MeasurementRange<?> range : ranges) {
                if (hasMore) {
                    buffer.append("<br>");
                }
                rangeFormat.format(range, buffer, pos);
                hasMore = true;
            }
            text = buffer.append("</html>").toString();
        }
        setText(sampleValueRanges, text);
    }

    /**
     * Sets the text of the given label to the given value, and set the visibility state
     * depending on whatever the given text is null or not.
     */
    private static void setText(final JLabel label, final String text) {
        label.setText(text);
        label.setVisible(text != null);
    }

    /**
     * Sets the layer to describe in the bottom panel.
     * This method does not change the selection in the list.
     *
     * @param layer The name of the layer to describe.
     */
    private void setSelectedLayer(final String layer) {
        if (layer == null) {
            setProperties(null, null, null, null, null, null, null);
            return;
        }
        final FutureQuery<Layer> task = database.getLayer(layer);
        task.invokeAfterCompletion(new Runnable() {
            private String name;
            private GeographicBoundingBox bbox;
            private Date startTime, endTime;
            private double[] resolution;
            private String[] elevations;
            private Set<String> formats;
            private List<MeasurementRange<?>> ranges;

            /**
             * Get the info from the layer, then pass them in the Swing thread to the widgets.
             */
            @Override
            public synchronized void run() {
                if (name == null) try {
                    final Layer layer = task.result();
                    name       = layer.getName();
                    bbox       = layer.getGeographicBoundingBox();
                    resolution = layer.getTypicalResolution();
                    formats    = layer.getImageFormats();
                    ranges     = layer.getSampleValueRanges();
                    final DateRange timeRange = layer.getTimeRange();
                    if (timeRange != null) {
                        startTime = timeRange.getMinValue();
                        endTime   = timeRange.getMaxValue();
                    }
                    final Set<Number> z = layer.getAvailableElevations();
                    if (z != null && !z.isEmpty()) {
                        int numDigits = 0;
                        for (final Number value : z) {
                            final int n = XMath.countDecimalFractionDigits(value.doubleValue());
                            if (n > numDigits) {
                                numDigits = n;
                                if (numDigits > 6) {
                                    numDigits = 6;
                                    break;
                                }
                            }
                        }
                        final FieldPosition pos = new FieldPosition(0);
                        final StringBuffer buffer = new StringBuffer();
                        final List<String> fz = new ArrayList<String>(z.size());
                        synchronized (heightFormat) {
                            heightFormat.setMinimumFractionDigits(numDigits);
                            heightFormat.setMaximumFractionDigits(numDigits);
                            for (final Number value : z) {
                                heightFormat.format(value, buffer, pos).append("    ");
                                fz.add(buffer.toString());
                                buffer.setLength(0);
                            }
                        }
                        elevations = fz.toArray(new String[fz.size()]);
                    }
                } catch (CoverageStoreException ex) {
                    ExceptionMonitor.show(LayerList.this, ex);
                    return;
                }
                if (!EventQueue.isDispatchThread()) {
                    EventQueue.invokeLater(this);
                } else {
                    setProperties(name, bbox, startTime, endTime, resolution, formats, ranges);
                    LayerList.this.elevations.setElements(elevations);
                }
            }
        });
    }
}
