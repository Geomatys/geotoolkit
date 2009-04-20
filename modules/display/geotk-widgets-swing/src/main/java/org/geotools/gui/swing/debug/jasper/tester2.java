/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotools.gui.swing.debug.jasper;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import org.geotools.gui.swing.debug.ContextBuilder;
import org.geotools.gui.swing.report.JRConfigTree;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.report.JRMapper;
import org.geotoolkit.report.JRMappedDataSource;

/**
 *
 * @author sorel
 */
public class tester2 extends JFrame{

    private final JRConfigTree tree = new JRConfigTree();
    private final JPanel panel = new JPanel(new BorderLayout());
    private Component comp = null;

    public tester2() throws JRException {

        // - Chargement et compilation du rapport
        JasperDesign jasperDesign = JRXmlLoader.load(new File("/home/sorel/DEV/temp/TestReport.jrxml"));
        final JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

        tree.setDesign(jasperDesign);


        tree.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {

                changeDetail(null);
                int index = tree.getSelectedRow();

                if(index != -1){
                    Object obj = tree.getValueAt(index, 1);

                    if(obj instanceof JRMapper){
                        JRMapper mapper = (JRMapper)obj;
                        changeDetail(mapper.getComponent());
                    }
                }

            }
        });

        tree.getModel().addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                changeDetail(null);
                int index = tree.getSelectedRow();

                if(index != -1){
                    Object obj = tree.getValueAt(index, 1);

                    if(obj instanceof JRMapper){
                        JRMapper mapper = (JRMapper)obj;
                        changeDetail(mapper.getComponent());
                    }
                }
            }
        });

        JScrollPane pane = new JScrollPane(tree);

        panel.add(BorderLayout.WEST, pane);

        panel.add(BorderLayout.SOUTH, new JButton(new AbstractAction("show report") {

            @Override
            public void actionPerformed(ActionEvent e) {

                JRMappedDataSource<MapContext> source = new JRMappedDataSource<MapContext>();

                final Collection<MapContext> contexts = new ArrayList<MapContext>();
                contexts.add(ContextBuilder.buildRealCityContext());
//                contexts.add(ContextBuilder.buildSmallVectorContext());
//                contexts.add(ContextBuilder.buildBigRoadContext());
//                contexts.add(ContextBuilder.buildSmallVectorContext());
                source.setIterator(contexts.iterator());

                Map<Object,Object> parameters = tree.getParameters();
                Map<String,JRMapper<?,? super MapContext>> mapping = tree.getMapping();

                source.mapping().putAll(mapping);

                try {
                    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, source);

                    // - preview du rapport
                    JasperViewer viewer = new JasperViewer(jasperPrint);
                    viewer.setVisible(true);

                } catch (JRException ex) {
                    Logger.getLogger(tester2.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }));

        setContentPane(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void changeDetail(Component candidate){
        if(comp != null){
            panel.remove(comp);
            comp = null;
        }

        comp = candidate;

        if(comp != null){
            panel.add(BorderLayout.CENTER,comp);
        }

        panel.revalidate();
        panel.repaint();
    }


    public static void main(String[] args) throws JRException {
        new tester2();
    }

}
