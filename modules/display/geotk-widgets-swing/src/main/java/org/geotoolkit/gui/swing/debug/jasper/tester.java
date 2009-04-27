/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.gui.swing.debug.jasper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.io.IOException;
import net.sf.jasperreports.engine.JRException;

import org.geotoolkit.gui.swing.debug.ContextBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.report.JRExportService;
import org.opengis.geometry.Envelope;

/**
 *
 * @author sorel
 */
public class tester {

    public static void main(String[] args) throws JRException, FileNotFoundException, IOException {

        JRExportService service = JRExportService.getInstance();
        MapContext context = ContextBuilder.buildSmallVectorContext();

        Envelope env = context.getBounds();
        context.setAreaOfInterest(env);

        File template = new File("/home/sorel/DEV/temp/TestReport.jrxml");
        File output = new File("/home/sorel/mymap.pdf");

        service.exportAsPdf(context, template, new FileOutputStream(output), null);

    }

    private void test(){
        
//        // - Chargement et compilation du rapport
//        JasperDesign jasperDesign = JRXmlLoader.load(new File("/home/sorel/DEV/temp/TestReport.jrxml"));
//        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
//
//
//        // - Paramètres à envoyer au rapport
//        Map parameters = new HashMap();
//        parameters.put("TITLE", "My map title");
//
//        // - Execution du rapport
//        MappedJRDataSource<MapContext> source = new MappedJRDataSource<MapContext>();
//
//        final Collection<MapContext> contexts = new ArrayList<MapContext>();
//        contexts.add(ContextBuilder.buildSmallVectorContext());
//        contexts.add(ContextBuilder.buildBigRoadContext());
//        contexts.add(ContextBuilder.buildSmallVectorContext());
//        source.setIterator(contexts.iterator());
//
//        final Map<String,JRMapper<?, ? super MapContext>> mapping = source.mapping();
//        mapping.put("nameField",                        new StaticStringMapperFactory().createMapper("a static text value"));
//        mapping.put("GO2-Map",         new CanvasMapperFactory().createMapper());
//        mapping.put("GO2-North-Arrow", new NorthArrowMapperFactory().createMapper());
//        mapping.put("GO2-Scale-Bar",   new NorthArrowMapperFactory().createMapper());
//
//        for(JRMapperFactory mapper : ServiceLoader.load(JRMapperFactory.class)){
//            System.out.println(mapper.getTitle().toString());
//        }
//
//
////        JRDataSource source = new ContextReportFiller(ContextBuilder.buildRealCityContext());
//        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, source);
//
//        // - preview du rapport
//        System.out.println("DISPLAY ------------------------------------------------");
//        JasperViewer viewer = new JasperViewer(jasperPrint);
//        viewer.setVisible(true);
//
//        // - Création du rapport au format PDF
////        JasperExportManager.exportReportToPdfFile(jasperPrint, "/home/sorel/classic.pdf");

    }


}
