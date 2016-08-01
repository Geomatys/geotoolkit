/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

package org.geotoolkit.pending.demo.clients.wps;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.lang.Setup;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.util.NullProgressListener;
import org.geotoolkit.wps.WebProcessingClient;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

import javax.imageio.ImageIO;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.referencing.CommonCRS;

/**
 * A simple example of web processing querying.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 15/05/13
 */
public class WPSClientDemo {

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.pending.demo.clients.wps");

    private final static String SERVICE_URL = "http://demo.geomatys.com/cstl-trunk/WS/wps/default";

    // Identifier of the process we want.
    private final static String PROCESS_ID = "urn:ogc:cstl:wps:jts:buffer";

    //Expected input parameter IDs
    private static enum INPUT {
        geom("urn:ogc:cstl:wps:jts:buffer:input:geom"),
        distance("urn:ogc:cstl:wps:jts:buffer:input:distance");

        public final String name;
        private INPUT(String toSet) {
            name = toSet;
        }
    }

    //Expected output parameter IDs
    private static enum OUTPUT {
        result_geom("urn:ogc:cstl:wps:jts:buffer:output:result_geom");

        public final String name;
        private OUTPUT(String toSet) {
            name = toSet;
        }
    }

    public static void main(String[] args) throws Exception {

        //allow reprojection even if grid or bursawolf parameters are missing
        Hints.putSystemDefault(Hints.LENIENT_DATUM_SHIFT, Boolean.TRUE);

        //force loading all image readers/writers
        ImageIO.scanForPlugins();

        //global initialization
        Setup.initialize(null);

        // Instantiate client :
        final URL wpsURL = new URL(SERVICE_URL);
        final WebProcessingClient wpsClient =
                new WebProcessingClient(wpsURL, WebProcessingClient.WPSVersion.v100.getCode());

        // Once initialized, we can ask a description of wanted process, using its id.
        ProcessDescriptor desc = wpsClient.getDescriptor(PROCESS_ID);

        //We can check process input & output.
        ParameterDescriptorGroup inputDesc = desc.getInputDescriptor();
        ParameterDescriptorGroup outputDesc = desc.getOutputDescriptor();

        // Iterate on wanted parameters. If one is missing, an exception is raised.
        LOGGER.log(Level.INFO, "INPUT : \n");
        for (INPUT in : INPUT.values()) {
            final GeneralParameterDescriptor current = inputDesc.descriptor(in.name);
            LOGGER.log(Level.INFO, "Parameter : "+current.getName().getCode()+" : \n\t"+current.getRemarks());
        }

        LOGGER.log(Level.INFO, "OUTPUT : \n");
        for (OUTPUT out : OUTPUT.values()) {
            final GeneralParameterDescriptor current = outputDesc.descriptor(out.name);
            LOGGER.log(Level.INFO, "Parameter : " + current.getName().getCode() + " : \n\t" + current.getRemarks());
        }

        GeometryFactory factory = new GeometryFactory();
        Geometry geometry = factory.toGeometry(new Envelope(1.0, 2.0, 43.3, 43.9));
        geometry.setUserData(CommonCRS.WGS84.geographic());
        final double bufDistance = 0.5;

        // Instantiate inputs. Those objects get the same behaviour that the descriptors. If we cannot find a specific
        // parameter, an exception is thrown.
        ParameterValueGroup input = inputDesc.createValue();
        input.parameter(INPUT.geom.name).setValue(geometry);
        input.parameter(INPUT.distance.name).setValue(bufDistance);

        // If queried WPS supports storage, we ask output as reference. It should cause asynchronous processing server-side.
        if (wpsClient.supportStorage(PROCESS_ID)) {
            wpsClient.setOutputsAsReference(PROCESS_ID, true);
        }

        // Process execution. It's synchronous here (talking only of client side, WPS can execute asynchronous process).
        // For asynchronous execution, we must execute it in a thread, using a process listener to get process state.
        org.geotoolkit.process.Process toExecute = desc.createProcess(input);

        toExecute.addListener(new NullProgressListener());

        ParameterValueGroup output = toExecute.call();

        // Get output values :
        for(OUTPUT out : OUTPUT.values()) {
            LOGGER.log(Level.INFO, String.format("%s parameter value : %s", out.name(), output.parameter(out.name).getValue()));
        }
    }

}
