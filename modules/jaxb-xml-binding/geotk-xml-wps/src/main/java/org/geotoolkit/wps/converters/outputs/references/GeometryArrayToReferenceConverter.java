/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.wps.converters.outputs.references;

import com.fasterxml.jackson.core.JsonEncoding;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.UUID;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.data.geojson.GeoJSONStreamWriter;
import org.geotoolkit.wps.converters.WPSConvertersUtils;
import org.geotoolkit.wps.io.WPSIO;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.v100.InputReferenceType;
import org.geotoolkit.wps.xml.v100.OutputReferenceType;
import org.geotoolkit.wps.xml.v100.ReferenceType;

/**
 *
 * @author Theo Zozime
 */
public class GeometryArrayToReferenceConverter extends AbstractReferenceOutputConverter<Geometry[]>{

    private static GeometryArrayToReferenceConverter INSTANCE;

    public static synchronized GeometryArrayToReferenceConverter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new GeometryArrayToReferenceConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<Geometry[]> getSourceClass() {
        return Geometry[].class;
    }

    @Override
    public ReferenceType convert(Geometry[] source, Map<String, Object> params) throws UnconvertibleObjectException {
        if (params.get(TMP_DIR_PATH) == null)
            throw new UnconvertibleObjectException("The output directory should be defined");

        if (source == null)
            throw new UnconvertibleObjectException("The output data should be defined");

        final WPSIO.IOType ioType = WPSIO.IOType.valueOf((String)params.get(IOTYPE));
        ReferenceType reference = null;

        if (ioType.equals(WPSIO.IOType.INPUT))
            reference = new InputReferenceType();
        else
            reference = new OutputReferenceType();

        reference.setMimeType((String)params.get(MIME));
        reference.setEncoding((String)params.get(ENCODING));
        reference.setSchema((String)params.get(SCHEMA));

        final String randomFilename = UUID.randomUUID().toString() + ".json";
        final File geometryArrayFile = new File((String)params.get(TMP_DIR_PATH), randomFilename);
        try {

            if (WPSMimeType.APP_GEOJSON.val().equalsIgnoreCase(reference.getMimeType())) {
                try (OutputStream geometryStream = new FileOutputStream(geometryArrayFile)) {
                    GeometryFactory geometryFactory = new GeometryFactory();
                    Geometry toWrite = new GeometryCollection(source, geometryFactory);
                    GeoJSONStreamWriter.writeSingleGeometry(geometryStream, toWrite, JsonEncoding.UTF8, WPSConvertersUtils.FRACTION_DIGITS, true);
                }
                reference.setHref(geometryArrayFile.toURI().toURL().toString());
                return reference;
            }
            else
                throw new UnconvertibleObjectException("Unsupported mime-type for " + this.getClass().getName() +  " : " + reference.getMimeType());
        } catch (FileNotFoundException ex) {
            throw new UnconvertibleObjectException("Unable to find the reference file");
        } catch (IOException ex) {
            throw new UnconvertibleObjectException(ex);
        }
    }

}
