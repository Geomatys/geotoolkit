/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2015, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.report;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ServiceLoader;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOdtReportConfiguration;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import static org.apache.sis.util.ArgumentChecks.*;
import org.apache.sis.util.ObjectConverters;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.display2d.service.OutputDef;
import org.geotoolkit.lang.Static;
import org.opengis.feature.AttributeType;
import org.opengis.feature.FeatureType;

/**
 * Utility class to generate html or pdf reports using JasperReport library.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public final class JasperReportService extends Static {

    private static final Collection<JRFieldRenderer> RENDERERS;

    public static final String MIME_PDF = "application/pdf";
    public static final String MIME_HTML = "text/html";
    public static final String MIME_ODT = "application/vnd.oasis.opendocument.text";

    static {
        final ServiceLoader<JRFieldRenderer> service = ServiceLoader.load(JRFieldRenderer.class);
        final List<JRFieldRenderer> renderers = new ArrayList<JRFieldRenderer>();
        for(final JRFieldRenderer r : service){
            renderers.add(r);
        }
        RENDERERS = UnmodifiableArrayList.wrap(renderers.toArray(new JRFieldRenderer[renderers.size()]));
    }

    private JasperReportService(){}

    /**
     * Parse the given input to a JasperReport and a feature type describing the remplate record.
     * This feature type must be used to generate the featureCollection that will be used
     * by the generateReport method.
     *
     * @param jrxml
     * @return Entry<JasperReport,FeatureType>
     * @throws JRException
     */
    public static Entry<JasperReport,FeatureType> prepareTemplate(final Object jrxml) throws JRException{

        // load and compile the template
        final JasperDesign jasperDesign;
        if(jrxml instanceof File){
            jasperDesign = JRXmlLoader.load((File)jrxml);
        }else if(jrxml instanceof InputStream){
            jasperDesign = JRXmlLoader.load((InputStream)jrxml);
        }else if(jrxml instanceof URL){
            try {
                jasperDesign = JRXmlLoader.load( ((URL)jrxml).openStream() );
            } catch (IOException ex) {
                throw new JRException(ex);
            }
        }else if(jrxml instanceof String){
            jasperDesign = JRXmlLoader.load((String)jrxml);
        }else{
            //last chance : try to convert the source to a file
            final File candidate = ObjectConverters.convert(jrxml, File.class);
            if(candidate instanceof File){
                jasperDesign = JRXmlLoader.load(candidate);
            }else{
                throw new IllegalArgumentException("Unsupported input type : " + jrxml);
            }
        }

        //generate the report and extract the feature type.
        final JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
        final FeatureType type = extractType(jasperDesign);
        return new SimpleImmutableEntry<JasperReport, FeatureType>(jasperReport, type);
    }

    /**
     * Generate a report from the given JasperReport. It we be filled with the features
     * provided in the Collection.
     *
     * @param report : report to generate
     * @param col : if featureCollection, feature type must match the given one from the prepareTemplace method.
     * @param parameters : Map of parameters passed to the JasperFillManager.
     * @param output : output definition
     * @throws JRException
     */
    public static void generateReport(final JasperReport report, final Collection col, final Map parameters,
                                  final OutputDef output) throws JRException, FeatureStoreRuntimeException{
        final CollectionDataSource source = new CollectionDataSource(col);
        final JasperPrint print = JasperFillManager.fillReport(report, parameters, source);
        generate(print, output);
    }

    /**
     * Generate a report from the given JasperReport. It we be filled with the features
     * provided in the FeatureIterator.
     *
     * @param report : report to generate
     * @param ite : if featureCollection, feature type must match the given one from the prepareTemplace method.
     * @param parameters : Map of parameters passed to the JasperFillManager.
     * @param output : output definition
     * @throws JRException
     */
    public static void generateReport(final JasperReport report, final Iterator ite, final Map parameters,
                                  final OutputDef output) throws JRException, FeatureStoreRuntimeException{
        final CollectionDataSource source = new CollectionDataSource(ite);
        final JasperPrint print = JasperFillManager.fillReport(report, parameters, source);
        generate(print, output);
    }

    /**
     * Write the jasper print in the defined output.
     * @param print
     * @param output : output definition
     * @throws net.sf.jasperreports.engine.JRException
     */
    public static void generate(final JasperPrint print, final OutputDef output) throws JRException{
        final String mime = output.getMime();
        Object target = output.getOutput();

        ensureNonNull("mime", mime);
        ensureNonNull("output target", target);

        //we adjust the output target to a knowned type
        if(!(target instanceof OutputStream)){
            //we try to convert it to a file
            target = ObjectConverters.convert(target, File.class);
        }

        if(mime.equalsIgnoreCase(MIME_PDF)){
            if(target instanceof OutputStream){
                JasperExportManager.exportReportToPdfStream(print, (OutputStream) target);
            }else{
                JasperExportManager.exportReportToPdfFile(print, ((File) target).getPath());
            }
        }else if(mime.equalsIgnoreCase(MIME_HTML)){
            if(target instanceof File){
                JasperExportManager.exportReportToHtmlFile(print, ((File) target).getPath());
            }else{
                throw new IllegalArgumentException("Unsupported output : " + target + " for mime type : "+ mime);
            }
        }else if(mime.equalsIgnoreCase(MIME_ODT)){
            if(target instanceof File){

                final JROdtExporter exporter = new JROdtExporter();
                exporter.setExporterInput(new SimpleExporterInput(print));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput((File)target));
                SimpleOdtReportConfiguration config = new SimpleOdtReportConfiguration();
                exporter.setConfiguration(config);
                exporter.exportReport();

            }else{
                throw new IllegalArgumentException("Unsupported output : " + target + " for mime type : "+ mime);
            }
        }else{
            throw new IllegalArgumentException("Unsupported mime type : " + mime);
        }
    }

    /**
     * Explore the report design and generate a FeatureType that match the records definition.
     *
     * @param design
     * @return FeatureType
     */
    private static FeatureType extractType(final JasperDesign design){
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(design.getName());

        //find a description for each field
        final Collection<JRFieldRenderer> renderers = JasperReportService.getFieldRenderers();

        fields:
        for(final JRField field : design.getFields()){

            //search for special fields
            for(JRFieldRenderer renderer : renderers){
                if(renderer.canHandle(field)){
                    final AttributeType desc = renderer.createDescriptor(field);
                    ftb.addAttribute(desc);
                    continue fields;
                }
            }

            //handle it as a casual field
            ftb.addAttribute(field.getValueClass())
                    .setName(field.getName());
                    //TODO
                    //toParameterMap(field.getPropertiesMap()));
        }

        return ftb.build();
    }

    /**
     * Change a jasper report property map in a casual java Map.
     */
    private static Map<Object,Object> toParameterMap(final JRPropertiesMap params){
        if(params == null){
            return null;
        }
        final String[] propertyNames = params.getPropertyNames();
        if(propertyNames.length == 0){
            return null;
        }

        final Map<Object,Object> map = new HashMap<Object, Object>(propertyNames.length);
        for(String name : propertyNames){
            map.put(name, params.getProperty(name));
        }

        return map;
    }

    public static Collection<JRFieldRenderer> getFieldRenderers(){
        return RENDERERS;
    }

}
