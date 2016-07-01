/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.gui.swing.render2d.control.information.presenter;

import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.storage.coverage.CoverageExtractor;
import org.geotoolkit.storage.coverage.CoverageReference;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.display2d.canvas.AbstractGraphicVisitor;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.map.CoverageMapLayer;
import org.apache.sis.referencing.CRS;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

import javax.measure.unit.Unit;
import javax.swing.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

/**
 * @author Quentin Boileau (Geomatys)
 */
public class CoverageDrillingPresenter extends AbstractInformationPresenter {

    public CoverageDrillingPresenter() {
        super(10);
    }

    @Override
    public JComponent createComponent(Object graphic, RenderingContext2D context, SearchAreaJ2D area) {
        if (graphic instanceof ProjectedCoverage) {
            final ProjectedCoverage projectedCoverage = (ProjectedCoverage) graphic;
            final CoverageMapLayer layer = projectedCoverage.getLayer();
            final CoverageReference covRef = layer.getCoverageReference();

            GridCoverageReader reader = null;
            try {
                reader = covRef.acquireReader();
                final GeneralGridGeometry gridGeo = reader.getGridGeometry(covRef.getImageIndex());
                final CoordinateReferenceSystem crs = gridGeo.getCoordinateReferenceSystem();
                final int dimension = crs.getCoordinateSystem().getDimension();
                if (dimension > 2) {
                    return createComponent(projectedCoverage, context, area);
                }
            } catch (CoverageStoreException ex) {
                context.getMonitor().exceptionOccured(ex, Level.INFO);
                return null;
            } finally {
                if (reader != null) {
                    covRef.recycle(reader);
                }
            }
        }
        return null;
    }

    private JComponent createComponent(final ProjectedCoverage projectedCoverage, final RenderingContext2D context, final SearchAreaJ2D area) {

        final JPanel container = new JPanel(new BorderLayout());
        container.add(new JLabel("Loading ..."), BorderLayout.CENTER);
        container.setSize(350, 200);

        new Thread(new Runnable() {
            @Override
            public void run() {
                //extract values
                CoverageExtractor.Ray allValues = null;
                CoordinateReferenceSystem crs = null;
                try {
                    allValues = Bridge.extractData(projectedCoverage, context, area);
                    final CoverageMapLayer layer = projectedCoverage.getLayer();
                    final CoverageReference covRef = layer.getCoverageReference();
                    final GridCoverageReader reader = covRef.acquireReader();
                    final GeneralGridGeometry gridGeo = reader.getGridGeometry(covRef.getImageIndex());
                    crs = gridGeo.getCoordinateReferenceSystem();
                    covRef.recycle(reader);


                    if (allValues == null || allValues.getValues().isEmpty()) {
                        container.removeAll();
                        container.add(new JLabel("No values"));
                    }

                    //analyse
                    Map<Integer, CoordinateReferenceSystem> analyse = ReferencingUtilities.indexedDecompose(crs);

                    TemporalCRS temporalCRS = null;
                    int timeIdx = -1;
                    for (Map.Entry<Integer, CoordinateReferenceSystem> entry : analyse.entrySet()) {
                        if (entry.getValue() instanceof TemporalCRS) {
                            temporalCRS = (TemporalCRS) entry.getValue();
                            timeIdx = entry.getKey();
                            break;
                        }
                    }

                    MathTransform transform = null;
                    double[] timesArray = new double[0];

                    if (temporalCRS != null) {
                        transform = CRS.findOperation(temporalCRS, CommonCRS.Temporal.JAVA.crs(), null).getMathTransform();

                        final Set<Double> times = new TreeSet<Double>();
                        Set<DirectPosition> positions = allValues.getValues().keySet();
                        for (DirectPosition position : positions) {
                            times.add(position.getOrdinate(timeIdx));
                        }

                        timesArray = new double[times.size()];
                        int i = 0;
                        for (Double time : times) {
                            timesArray[i++] = time;
                        }
                    }

                    //return panel
                    container.removeAll();
                    container.add(new ResultPanel(allValues, analyse, transform, timeIdx, timesArray), BorderLayout.CENTER);
                } catch (CoverageStoreException ex) {
                    context.getMonitor().exceptionOccured(ex, Level.INFO);
                    container.removeAll();
                    container.add(new JLabel("Error : " + ex.getMessage()));
                } catch (FactoryException ex) {
                    context.getMonitor().exceptionOccured(ex, Level.INFO);
                    container.removeAll();
                    container.add(new JLabel("Error : " + ex.getMessage()));
                } catch (TransformException ex) {
                    context.getMonitor().exceptionOccured(ex, Level.INFO);
                    container.removeAll();
                    container.add(new JLabel("Error : " + ex.getMessage()));
                }
                container.revalidate();
                container.repaint();
                container.firePropertyChange("update", false, true);
            }
        }).start();
        return container;
    }

    private class ResultPanel extends JPanel {

        private final Map<DirectPosition, double[]> allValues;
        private final MathTransform temporalToJava;
        private final int timeIdx;
        private final double[] times;
        private final int[] dimIndexes;
        private final NumberFormat nbFormat = new DecimalFormat();
        private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        private final String header;

        private final JPanel contentPane;
        private final JLabel label = new JLabel("0/0");
        private final JTextPane textPane;
        private final AbstractAction nextAction;
        private final AbstractAction previousAction;
        private final JToolBar toolbar;

        private int currentTime = 0;

        private ResultPanel(CoverageExtractor.Ray result, Map<Integer, CoordinateReferenceSystem> crsParts,
                            MathTransform temporalToJava, int timeIdx, double[] times) {
            super(new BorderLayout());

            this.allValues = result.getValues();
            this.temporalToJava = temporalToJava;
            this.timeIdx = timeIdx;
            this.times = times;

            final java.util.List<GridSampleDimension> dimensions = result.getSampleDimensions();
            final Map.Entry<DirectPosition, double[]> firstEntry = allValues.entrySet().iterator().next();
            final DirectPosition firstPos = firstEntry.getKey();
            int nbBand = firstEntry.getValue().length;

            nbFormat.setMaximumFractionDigits(3);
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT0"));

            contentPane = new JPanel(new BorderLayout());

            //CSS
            StyleSheet styles = new StyleSheet();
            styles.addRule("body {padding:10px; background-color:#ffffff; font-family:Monospaced;}");
            styles.addRule("table {margin-left: 15px; border-collapse: collapse; width:100%;}");
            styles.addRule("tr {border-width: 1px; border-style:solid; border-color:black;}");
            styles.addRule("td {border-width: 1px; text-align: center; border-style:solid; border-color:black; padding:5px;}");
            styles.addRule("th {border-width: 1px; text-align: center; border-style:solid; border-color:black; padding:5px;}");
            styles.addRule(".separator {border:2px; width:1px; padding:1px;}");

            textPane = new JTextPane(new HTMLDocument(styles));
            textPane.setEditable(false);
            textPane.setContentType("text/html");
            textPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
            textPane.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            textPane.setStyledDocument(new HTMLDocument(styles));

            final JScrollPane scroll = new JScrollPane(textPane);
            contentPane.add(BorderLayout.CENTER, scroll);

            final StringBuilder sb = new StringBuilder("<html><body>");
            sb.append("<b>").append("λ : ").append(nbFormat.format(firstPos.getOrdinate(0))).append("</b><br/>");
            sb.append("<b>").append("φ : ").append(nbFormat.format(firstPos.getOrdinate(1))).append("</b><br/>");
            sb.append("<hr>");
            sb.append("<table>");
            sb.append("<tr>");

            // index of dimensions not horizontal nor temporal
            LinkedList<Integer> dimIndexesList = new LinkedList<Integer>();
            //CRS dim columns
            int i = 0;
            for (Map.Entry<Integer, CoordinateReferenceSystem> entry : crsParts.entrySet()) {
                final CoordinateReferenceSystem crsPart = entry.getValue();
                if (!CRS.isHorizontalCRS(crsPart) && !(crsPart instanceof TemporalCRS)) {
                    dimIndexesList.add(entry.getKey());
                    CoordinateSystem cs = crsPart.getCoordinateSystem();
                    CoordinateSystemAxis axis = cs.getAxis(0);
                    sb.append("<th>").append(axis.getAbbreviation());
                    if (axis.getUnit() != null) {
                        sb.append("(").append(axis.getUnit()).append(")");
                    }
                    sb.append("</th>");
                }
            }
            dimIndexes = new int[dimIndexesList.size()];
            for (int j = 0; j < dimIndexesList.size(); j++) {
                dimIndexes[j] = dimIndexesList.get(j);
            }

            //band columns
            for (int j = 0; j < nbBand; j++) {
                GridSampleDimension dim = dimensions.get(j);
                Unit<?> units = dim.getUnits();
                if (j == 0) {
                    sb.append("<th class=\"separator\"></th>");
                }
                sb.append("<th>").append("band-").append(j);
                if (units != null) {
                    sb.append("(").append(units).append(")");
                }
                sb.append("</th>");
            }
            sb.append("</tr>");

            header = sb.toString();

            // navigation buttons
            nextAction = new AbstractAction(" > ") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setCurrentTime(++currentTime);
                }
            };

            previousAction = new AbstractAction(" < ") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setCurrentTime(--currentTime);
                }
            };

            final GridBagConstraints cst = new GridBagConstraints();
            toolbar = new JToolBar();
            toolbar.setLayout(new GridBagLayout());
            toolbar.setFloatable(false);
            cst.gridx = 0;
            toolbar.add(new JButton(previousAction), cst);
            cst.gridx = 1;
            toolbar.add(new JButton(nextAction), cst);
            cst.gridx = 2;
            cst.weightx = 1;
            toolbar.add(label, cst);

            if (times.length > 0) {
                setCurrentTime(currentTime);
            } else {
                toolbar.setVisible(false);
                setCurrentTime(-1);
            }
            contentPane.add(toolbar, BorderLayout.SOUTH);
            this.add(contentPane, BorderLayout.CENTER);
        }

        private void setCurrentTime(int index) {
            textPane.setText("");
            Double time = index != -1 ? times[index] : null;

            StringBuilder sb = new StringBuilder(header);

            for (Map.Entry<DirectPosition, double[]> entry : allValues.entrySet()) {
                final DirectPosition position = entry.getKey();

                if (time == null || position.getOrdinate(timeIdx) == time) {
                    sb.append("<tr>");
                    //crs dims
                    for (int i = 0; i < dimIndexes.length; i++) {
                        sb.append("<td>").append(position.getOrdinate(dimIndexes[i])).append("</td>");
                    }

                    //bands
                    double[] values = entry.getValue();
                    for (int i = 0; i < values.length; i++) {
                        if (i == 0) {
                            sb.append("<td class=\"separator\"></td>");
                        }
                        sb.append("<td>").append(values[i]).append("</td>");
                    }
                    sb.append("</tr>");
                }
            }
            sb.append("</table>");
            sb.append("</body></html>");

            //update content
            textPane.setText(sb.toString());

            //update time selectors
            if (index != -1) {
                previousAction.setEnabled(index != 0);
                nextAction.setEnabled(index < (times.length - 1));

                if (temporalToJava != null) {
                    try {
                        final double[] value = new double[]{time};
                        temporalToJava.transform(value, 0, value, 0, 1);
                        Date date = new Date(Double.valueOf(value[0]).longValue());
                        String dateStr = dateFormat.format(date);
                        label.setText("  " + (currentTime + 1) + "/" + times.length + " : " + dateStr);
                    } catch (TransformException e) {
                        e.printStackTrace();
                    }
                }
            }

            //force size and repaint
            this.setSize(contentPane.getSize());
            this.revalidate();
            this.repaint();
        }
    }

    private abstract static class Bridge extends AbstractGraphicVisitor {
        public static CoverageExtractor.Ray extractData(final ProjectedCoverage projectedCoverage,
                                                         final RenderingContext2D context,
                                                         final SearchAreaJ2D queryArea) throws TransformException, CoverageStoreException {
            return rayExtraction(projectedCoverage, context, queryArea);
        }
    }
}
