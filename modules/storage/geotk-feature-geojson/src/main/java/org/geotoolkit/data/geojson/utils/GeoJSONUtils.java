package org.geotoolkit.data.geojson.utils;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.io.wkt.Convention;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.data.geojson.binding.GeoJSONObject;
import org.geotoolkit.io.wkt.WKTFormat;
import org.geotoolkit.lang.Static;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.util.FileUtilities;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.logging.Level;

import static org.geotoolkit.data.geojson.utils.GeoJSONMembres.*;
import static org.geotoolkit.data.geojson.utils.GeoJSONTypes.*;

/**
 * @author Quentin Boileau (Geomatys)
 */
public final class GeoJSONUtils extends Static {

    /**
     * Fallback CRS
     */
    private static final CoordinateReferenceSystem DEFAULT_CRS = CommonCRS.WGS84.geographic();

    /**
     * Parse LinkedCRS (href + type).
     * @param href
     * @param crsType
     * @return CoordinateReferenceSystem or null.
     */
    public static CoordinateReferenceSystem parseCRS(String href, String crsType) {
        String wkt = null;
        try {
            URI uri = new URI(href);
            InputStream stream = uri.toURL().openStream();
            wkt = FileUtilities.getStringFromStream(stream);
        } catch (URISyntaxException | IOException e) {
            GeoJSONParser.LOGGER.log(Level.WARNING, "Can't access to linked CRS "+href, e);
        }

        if (wkt != null) {
            WKTFormat format = new WKTFormat();
            if (crsType.equals(CRS_TYPE_OGCWKT)) {
                format.setConvention(Convention.WKT1);
            } else if (crsType.equals(CRS_TYPE_ESRIWKT)) {
                format.setConvention(Convention.WKT1_COMMON_UNITS);
            }
            try {
                Object obj = format.parseObject(wkt);
                if (obj instanceof CoordinateReferenceSystem) {
                    return (CoordinateReferenceSystem) obj;
                } else {
                    GeoJSONParser.LOGGER.log(Level.WARNING, "Parsed WKT is not a CRS "+wkt);
                }
            } catch (ParseException e) {
                GeoJSONParser.LOGGER.log(Level.WARNING, "Can't parse CRS WKT " + crsType+ " : "+wkt, e);
            }
        }

        return null;
    }

    /**
     * Convert a CoordinateReferenceSystem to a identifier string like
     * urn:ogc:def:crs:EPSG::4326
     * @param crs
     * @return
     */
    public static String toURN(CoordinateReferenceSystem crs) {
        ArgumentChecks.ensureNonNull("crs", crs);

        try {
            if (org.geotoolkit.referencing.CRS.equalsIgnoreMetadata(crs, CommonCRS.WGS84.normalizedGeographic())) {
                return "urn:ogc:def:crs:OGC:1.3:CRS84";
            }

            int code = IdentifiedObjects.lookupEpsgCode(crs, true);
            return "urn:ogc:def:crs:EPSG::"+code;
        } catch (FactoryException e) {
            GeoJSONParser.LOGGER.log(Level.WARNING, "Unable to extract epsg code from given CRS "+crs, e);
        }
        return null;
    }

    /**
     * Try to extract/parse the CoordinateReferenceSystem from a GeoJSONObject.
     * Use WGS_84 as fallback CRS.
     * @param obj GeoJSONObject
     * @return GeoJSONObject CoordinateReferenceSystem or fallback CRS (WGS84).
     * @throws MalformedURLException
     * @throws DataStoreException
     */
    public static CoordinateReferenceSystem getCRS(GeoJSONObject obj) throws MalformedURLException, DataStoreException {
        CoordinateReferenceSystem crs = null;
        try {
            if (obj.getCrs() != null) {
                crs = obj.getCrs().getCRS();
            }
        } catch (FactoryException e) {
            throw new DataStoreException(e.getMessage(), e);
        }

        if (crs == null) {
            crs = DEFAULT_CRS;
        }
        return crs;
    }

    /**
     * Utility method Create geotk Envelope if bbox array is filled.
     * @return Envelope or null.
     */
    public static Envelope getEnvelope(GeoJSONObject obj, CoordinateReferenceSystem crs) {

        double[] bbox = obj.getBbox();
        if (bbox != null) {
            GeneralEnvelope env = new GeneralEnvelope(crs);
            int dim = bbox.length/2;
            if (dim == 2) {
                env.setRange(0, bbox[0], bbox[2]);
                env.setRange(1, bbox[1], bbox[3]);
            } else if (dim == 3) {
                env.setRange(0, bbox[0], bbox[3]);
                env.setRange(1, bbox[1], bbox[4]);
            }
            return env;
        }
        return null;
    }

    /**
     * Return file name without extension
     * @param file candidate
     * @return String
     */
    public static String getNameWithoutExt(File file) {
        String ext = extension(file);
        String typeName;
        if (ext != null) {
            typeName = file.getName().replace("."+ext, "");
        } else {
            typeName = file.getName();
        }
        return typeName;
    }

    /**
     * Returns the filename extension from a {@link String}, {@link File}, {@link URL} or
     * {@link URI}. If no extension is found, returns an empty string.
     *
     * @param  path The path as a {@link String}, {@link File}, {@link URL} or {@link URI}.
     * @return The filename extension in the given path, or an empty string if none.
     */
    public static String extension(final Object path) {
        final String name;
        final int base;
        if (path instanceof File) {
            name = ((File) path).getName();
            base = 0;
        } else {
            if (path instanceof URL) {
                name = ((URL) path).getPath();
            } else if (path instanceof URI) {
                name = ((URI) path).getPath();
            } else {
                name = path.toString();
            }
            base = name.lastIndexOf('/');
        }
        final int i = name.lastIndexOf('.');
        return (i > base) ? name.substring(i+1).trim() : "";
    }

    /**
     * Write an empty FeatureCollection in a file
     * @param f output file
     * @throws IOException
     */
    public static void writeEmptyFeatureCollection(File f) throws IOException {
        JsonGenerator writer = GeoJSONParser.FACTORY.createGenerator(f, JsonEncoding.UTF8);

        //start write feature collection.
        writer.writeStartObject();
        writer.writeStringField(TYPE, FEATURE_COLLECTION);
        writer.writeArrayFieldStart(FEATURES);
        writer.writeEndArray();
        writer.writeEndObject();
        writer.flush();
        writer.close();
    }
}
