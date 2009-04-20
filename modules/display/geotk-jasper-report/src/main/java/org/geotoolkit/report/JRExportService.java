/*
 *    GeoTools - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2004-2008, Geotools Project Managment Committee (PMC)
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
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.geotoolkit.map.MapContext;

/**
 * Jasper report service to export map context as documents.
 * This service can only handle template matching the automatic mapping system.
 * Field names must match the existing mappers favorites names.
 *
 * @author Johann Sorel (Geomatys)
 */
public class JRExportService {

    private static JRExportService INSTANCE = null;

    /**
     * Export the context as a pdf in the given stream.
     *
     * @param context : Mapcontext used a Jasper report record
     * @param template : jasper report template
     * @param output : Output Stream
     * @param jrParameters : map of the parameters for jasper fill manager
     * @throws net.sf.jasperreports.engine.JRException
     */
    public void exportAsPdf(final MapContext context, final File template, final OutputStream output, final Map jrParameters) throws JRException{

        final JasperPrint jasperPrint = generate(context, template, jrParameters);
        // write the pdf in the stream
        JasperExportManager.exportReportToPdfStream(jasperPrint, output);
    }

    /**
     * Export the context as html.
     *
     * @param context : Mapcontext used a Jasper report record
     * @param template : jasper report template
     * @param output : output file
     * @param jrParameters : map of the parameters for jasper fill manager
     * @throws net.sf.jasperreports.engine.JRException
     */
    public void exportAsHtml(final MapContext context, final File template, final String output, final Map jrParameters) throws JRException{

        final JasperPrint jasperPrint = generate(context, template, jrParameters);
        // write the html file
        JasperExportManager.exportReportToHtmlFile(jasperPrint, output);
    }

    private JasperPrint generate(final MapContext context, final File template, final Map jrParameters) throws JRException{

        // load and compile the template
        final JasperDesign jasperDesign = JRXmlLoader.load(template);
        final JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

        // find the fields mapping
        final JRMappedDataSource<MapContext> source = new JRMappedDataSource<MapContext>();
        final Collection<MapContext> contexts       = Collections.singleton(context);

        source.setIterator(contexts.iterator());
        source.findMapping(jasperDesign);

        return JasperFillManager.fillReport(jasperReport, jrParameters, source);
    }

    public static JRExportService getInstance(){
        if(INSTANCE == null){
            INSTANCE = new JRExportService();
        }
        return INSTANCE;
    }

}
