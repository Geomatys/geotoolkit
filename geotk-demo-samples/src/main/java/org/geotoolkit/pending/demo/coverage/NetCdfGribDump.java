
package org.geotoolkit.pending.demo.coverage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import ucar.nc2.NCdumpW;
import ucar.nc2.NetcdfFile;

/**
 * Example of how to print an NCDump for NetCDF and Grib files.
 */
public class NetCdfGribDump {

    public static void main(String[] args) throws IOException, URISyntaxException {

        Path dataResources = Paths.get(NetCdfGribDump.class.getResource("/data/grib/Atlantic.wave.grb").toURI());

        final NetcdfFile netcdf = NetcdfFile.open(dataResources.toAbsolutePath().toString());
        final PrintWriter pw = new PrintWriter(System.out);

        NCdumpW.print(netcdf, pw, false, true, false, false, null, null);
    }

}
