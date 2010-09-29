/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.display2d.service.OutputDef;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.lang.Static;
import org.geotoolkit.util.Converters;
import org.geotoolkit.util.NullArgumentException;

import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;

/**
 * Utility class to generate html or pdf reports using JasperReport library.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
@Static
public final class JasperReportService {

    public static final String MIME_PDF = "application/pdf";
    public static final String MIME_HTML = "text/html";

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
    public static Entry<JasperReport,FeatureType> prepareTemplate(Object jrxml) throws JRException{

        // load and compile the template
        final JasperDesign jasperDesign;
        if(jrxml instanceof File){
            jasperDesign = JRXmlLoader.load((File)jrxml);
        }else if(jrxml instanceof InputStream){
            jasperDesign = JRXmlLoader.load((InputStream)jrxml);
        }else if(jrxml instanceof String){
            jasperDesign = JRXmlLoader.load((String)jrxml);
        }else{
            //last chance : try to convert the source to a file
            final File candidate = Converters.convert(jrxml, File.class);
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
     * provided in the FeatureCollection.
     *
     * @param report : report to generate
     * @param col : feature type must match the given one from the prepareTemplace method.
     * @param parameters : Map of parameters passed to the JasperFillManager.
     * @param output : output definition
     * @throws JRException
     */
    public static void generateReport(JasperReport report, FeatureCollection col, Map parameters,
                                  OutputDef output) throws JRException, DataStoreRuntimeException{
        final FeatureIterator ite = col.iterator();
        try{
            generateReport(report, ite, parameters, output);
        }finally{
            ite.close();
        }
    }

    /**
     * Generate a report from the given JasperReport. It we be filled with the features
     * provided in the FeatureIterator.
     *
     * @param report : report to generate
     * @param ite : feature type must match the given one from the prepareTemplace method.
     * @param parameters : Map of parameters passed to the JasperFillManager.
     * @param output : output definition
     * @throws JRException
     */
    public static void generateReport(JasperReport report, FeatureIterator ite, Map parameters,
                                  OutputDef output) throws JRException, DataStoreRuntimeException{
        final FeatureCollectionDataSource source = new FeatureCollectionDataSource(ite);
        final JasperPrint print = JasperFillManager.fillReport(report, parameters, source);
        generate(print, output);
    }

    /**
     * Write the jasper print in the defined output.
     */
    private static void generate(JasperPrint print, OutputDef output) throws JRException{
        final String mime = output.getMime();
        Object target = output.getOutput();

        if(mime == null){
            throw new NullArgumentException("Mime type can not be null.");
        }
        if(target == null){
            throw new NullArgumentException("Output target can not be null.");
        }

        //we adjust the output target to a knowned type
        if(!(target instanceof OutputStream)){
            //we try to convert it to a file
            target = Converters.convert(target, File.class);
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
    private static FeatureType extractType(JasperDesign design){
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(design.getName());

        //find a description for each field
        final Collection<JRFieldRenderer> renderers = JRMappingUtils.getFieldRenderers();

        fields:
        for(final JRField field : design.getFields()){

            //search for special fields
            for(JRFieldRenderer renderer : renderers){
                if(renderer.canHandle(field)){
                    final PropertyDescriptor desc = renderer.createDescriptor(field);
                    ftb.add(desc);
                    continue fields;
                }
            }

            //handle it as a casual field
            ftb.add(field.getName(),
                    field.getValueClass(),
                    1,1,true,
                    toParameterMap(field.getPropertiesMap()));
        }

        return ftb.buildFeatureType();
    }

    /**
     * Change a jasper report property map in a casual java Map.
     */
    private static Map<Object,Object> toParameterMap(JRPropertiesMap params){
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

}
