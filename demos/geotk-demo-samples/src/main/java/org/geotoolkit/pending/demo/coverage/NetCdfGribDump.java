
package org.geotoolkit.pending.demo.coverage;

import java.io.IOException;
import java.io.PrintWriter;
import ucar.nc2.NCdumpW;
import ucar.nc2.NetcdfFile;

/**
 * Example of how to print an NCDump for NetCDF and Grib files.
 */
public class NetCdfGribDump {
    
    public static void main(String[] args) throws IOException {
        final NetcdfFile netcdf = NetcdfFile.open(".../pathtogrib");
        final PrintWriter pw = new PrintWriter(System.out);
        
        NCdumpW.print(netcdf, pw, true, true, false, false, null, null);
    }
    
}
