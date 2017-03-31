package org.geotoolkit.processing.vector.drift;

import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;
import org.geotoolkit.image.palette.PaletteFactory;
import ucar.ma2.Array;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriter;
import ucar.nc2.Variable;


final class Output {
    /**
     * Whether the output probability should use a logarithmic scale.
     */
    private static final boolean LOG_SCALE = false;

    private final int x, y, width, height;

    private final float[] prob;

    private final float min, max;

    Output(final double[] data, final int w, final int h) {
        int xmin = w;
        int ymin = h;
        int xmax = 0;
        int ymax = 0;
        for (int i=0,y=0; y<h; y++) {
            for (int x=0; x<w; x++) {
                if (data[i++] != 0) {
                    if (x < xmin) xmin = x;
                    if (x > xmax) xmax = x;
                    if (y < ymin) ymin = y;
                    if (y > ymax) ymax = y;
                }
            }
        }
        x      = xmin;
        y      = ymin;
        width  = (xmax - xmin) + 1;
        height = (ymax - ymin) + 1;
        prob   = new float[width * height];
        float zmin = Float.POSITIVE_INFINITY;
        float zmax = 0;
        for (int i=0,y=ymin; y <= ymax; y++) {
            int j = y * w + xmin;
            final int upper = j + width;
            while (j < upper) {
                final float v = (float) data[j++];
                prob[i++] = v;
                if (v != 0) {
                    if (v < zmin) zmin = v;
                    if (v > zmax) zmax = v;
                }
            }
        }
        min = zmin;
        max = zmax;
    }

    void writePNG(final Path directory, final String filename) throws IOException {
        double scale  = min;
        double offset = max;
        if (LOG_SCALE) {
            offset = Math.log(offset);
            scale  = Math.log(scale);
        }
        scale = 255 / (scale - offset);
        final BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED, (IndexColorModel)
                PaletteFactory.getDefault().getPalettePadValueFirst("yellow-green-blue", 256).getColorModel());
        final WritableRaster raster = img.getRaster();
        for (int i=0,y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                float v = prob[i++];
                if (v != 0) {
                    if (LOG_SCALE) v = (float) Math.log(v);
                    raster.setSample(x, y, 0, 1 + (int) Math.round((v - offset) * scale));
                }
            }
        }
        ImageIO.write(img, "png", directory.resolve(filename).toFile());
    }

    /**
     * Writes all data in a NetCDF files with two variable: one for the data per day, and one for the overall data.
     *
     * @param outputs    list of data per day, except the last element which must be the overall data.
     * @param startTime  start time in milliseconds since Java epoch.
     */
    static void write(final List<Output> outputs, final int epsgCode, final long startTime,
            final double prjX, final double prjY, final double resolutionX, final double resolutionY,
            final String outputPath)
            throws IOException, InvalidRangeException
    {
        int  xmin = Integer.MAX_VALUE;
        int  ymin = Integer.MAX_VALUE;
        float min = Float.MAX_VALUE;
        int  xmax = 0;
        int  ymax = 0;
        float max = 0;
        for (final Output o : outputs) {
            if (o.x            < xmin) xmin = o.x;
            if (o.y            < ymin) ymin = o.y;
            if (o.x + o.width  > xmax) xmax = o.x + o.width;
            if (o.y + o.height > ymax) ymax = o.y + o.height;
            if (o.min          <  min)  min = o.min;
            if (o.max          >  max)  max = o.max;
        }
        final int width  = xmax - xmin;
        final int height = ymax - ymin;
        final int nt = outputs.size() - 1;
        final NetcdfFileWriter file = NetcdfFileWriter.createNew(NetcdfFileWriter.Version.netcdf3, outputPath);
        final Dimension tdim = file.addDimension(null, "time", nt);
        final Dimension ydim = file.addDimension(null, "y",    height);
        final Dimension xdim = file.addDimension(null, "x",    width);
        final Variable  tvar = file.addVariable (null, "time", DataType.DOUBLE, Collections.singletonList(tdim));
        final Variable  yvar = file.addVariable (null, "y",    DataType.DOUBLE, Collections.singletonList(ydim));
        final Variable  xvar = file.addVariable (null, "x",    DataType.DOUBLE, Collections.singletonList(xdim));

        file.addVariableAttribute(xvar, new Attribute("units", "m"));
        file.addVariableAttribute(yvar, new Attribute("units", "m"));
        file.addVariableAttribute(tvar, new Attribute("units", "days since 1970-01-01T00:00:00Z"));
        file.addVariableAttribute(xvar, new Attribute("_CoordinateAxisType", "GeoX"));
        file.addVariableAttribute(yvar, new Attribute("_CoordinateAxisType", "GeoY"));
        file.addVariableAttribute(tvar, new Attribute("_CoordinateAxisType", "Time"));

        final Variable perDay = file.addVariable(null, "prob_per_day", DataType.FLOAT, Arrays.asList(tdim, ydim, xdim));
        file.addVariableAttribute(perDay, new Attribute("valid_min",  min / max));
        file.addVariableAttribute(perDay, new Attribute("valid_max",  1f));         // max / max
        file.addVariableAttribute(perDay, new Attribute("_FillValue", 0f));
        file.addVariableAttribute(perDay, new Attribute("EPSG_code",  epsgCode));

        final Variable overall = file.addVariable(null, "prob_overall", DataType.FLOAT, Arrays.asList(ydim, xdim));
        file.addVariableAttribute(overall, new Attribute("valid_min",  min / max));
        file.addVariableAttribute(overall, new Attribute("valid_max",  1f));
        file.addVariableAttribute(overall, new Attribute("_FillValue", 0f));
        file.addVariableAttribute(overall, new Attribute("EPSG_code",  epsgCode));
        file.create();

        file.write(tvar, Array.makeArray(DataType.DOUBLE, nt, startTime / (24*60*60*1000L), 1));
        file.write(yvar, Array.makeArray(DataType.DOUBLE, height, prjY + ymin * resolutionY, resolutionY));
        file.write(xvar, Array.makeArray(DataType.DOUBLE, width,  prjX + xmin * resolutionX, resolutionX));
        final int lengthOneDay = height * width;
        float[] data = new float[(nt+1) * lengthOneDay];
        for (int i=0; i<=nt; i++) {
            final Output o = outputs.get(i);
            int src = 0;
            int dst = i * lengthOneDay + width * (o.y - ymin) + (o.x - xmin);
            for (int y=0; y < o.height; y++) {
                for (int x=0; x < o.width; x++) {
                    data[dst + x] = o.prob[src++] / max;
                }
                dst += width;
            }
        }
        final float[] d1 = Arrays.copyOf(data, nt * lengthOneDay);
        final float[] d2 = Arrays.copyOfRange(data, nt * lengthOneDay, (nt+1) * lengthOneDay);
        file.write(perDay,  Array.factory(DataType.FLOAT, new int[] {nt, height, width}, d1));
        file.write(overall, Array.factory(DataType.FLOAT, new int[] {    height, width}, d2));
        file.close();
    }
}
