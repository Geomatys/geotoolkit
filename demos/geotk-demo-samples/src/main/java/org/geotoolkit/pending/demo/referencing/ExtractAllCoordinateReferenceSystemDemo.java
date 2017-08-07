
package org.geotoolkit.pending.demo.referencing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import org.apache.sis.io.wkt.Convention;
import org.apache.sis.io.wkt.WKTFormat;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.pending.demo.Demos;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.datum.Datum;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.datum.PrimeMeridian;
import org.opengis.util.FactoryException;

/**
 * this demo shos how to obtain all epsg codes and extract the WKT string for each one.
 * It can used to generate an epsg  property file for embeded applications, like applets.
 */
public class ExtractAllCoordinateReferenceSystemDemo {

    public static void main(String[] args) throws FactoryException, FileNotFoundException, IOException {
        Demos.init();

        //get the EPSG factory, other might exist, CRS, IGNF, AUTO ...
        final CRSAuthorityFactory factory = AuthorityFactoryFinder.getCRSAuthorityFactory("EPSG", null);

        //get allcodes, the EPSG factory contain several types of object, elipsoid, datum, CoordinateSystem, ...
        //we extract each one to make some replacement, to obtain a more compact properties file
        //if you do not care about having something compact, just use the crsCodes
        final Map<String,String> allCodes = toWKTMap(factory, factory.getAuthorityCodes(CoordinateReferenceSystem.class));
        final Map<String,String> csaCodes = toWKTMap(factory, factory.getAuthorityCodes(CoordinateSystemAxis.class));
        final Map<String,String> ellipsoidCodes = toWKTMap(factory, factory.getAuthorityCodes(Ellipsoid.class));
        final Map<String,String> datumCodes = toWKTMap(factory, factory.getAuthorityCodes(Datum.class));
        final Map<String,String> pmCodes = toWKTMap(factory, factory.getAuthorityCodes(PrimeMeridian.class));

        //pack all objects
        System.out.println("Compact CRS-CRS");  compact(allCodes, allCodes);
        System.out.println("Compact CRS-DATUM");compact(allCodes, datumCodes);
        allCodes.putAll(datumCodes);
        System.out.println("Compact CRS-ELLIPSOID");  compact(allCodes, ellipsoidCodes);
        allCodes.putAll(ellipsoidCodes);
        System.out.println("Compact CRS-CSA");  compact(allCodes, csaCodes);
        allCodes.putAll(csaCodes);
        System.out.println("Compact CRS-PM");   compact(allCodes, pmCodes);
        allCodes.putAll(pmCodes);

        //store all WKT in a property file
        final Properties values = new Properties();
        values.putAll(allCodes);

        //write the file
        final File file = new File("epsg.properties");
        final OutputStream stream = new FileOutputStream(file);
        try{
            values.store(stream, "EPSG coordinate reference system list");
        }catch(IOException ex){
            ex.printStackTrace();
        }finally{
            stream.close();
        }
    }

    private static Map<String,String> toWKTMap(final CRSAuthorityFactory factory, final Collection<String> codes){
        final WKTFormat format = new WKTFormat(null,null);
        format.setConvention(Convention.WKT1);
        format.setIndentation(WKTFormat.SINGLE_LINE);
        final Map<String,String> map = new HashMap<String, String>();

        for(final String code : codes){

            try{
                final IdentifiedObject obj = factory.createObject(code);
                final String wkt = format.format(obj);
                map.put(code, wkt);
            }catch(Exception ex){
                //some objects can not be expressed in WKT, we skip them
            }
        }
        return map;
    }

    private static void compact(final Map<String,String> values, final Map<String,String> replacements){

        for(Entry<String,String> replacement : replacements.entrySet()){
            for(Entry<String,String> candidate : values.entrySet()){
                if(candidate.getKey().equals(replacement.getKey())){
                    continue;
                }

                candidate.setValue( candidate.getValue().replace(replacement.getValue(), "$"+replacement.getKey()));

            }
        }

    }


}
