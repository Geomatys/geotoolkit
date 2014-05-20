/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.sos.netcdf;

import java.util.List;
import ucar.ma2.Array;
import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayFloat;
import ucar.ma2.ArrayInt;
import ucar.ma2.ArrayLong;
import ucar.ma2.ArrayShort;
import ucar.ma2.DataType;
import ucar.nc2.Dimension;
import ucar.nc2.Variable;
import ucar.nc2.iosp.netcdf3.N3iosp;
import ucar.nc2.time.CalendarDate;
import ucar.nc2.time.CalendarDateUnit;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class NetCDFUtils {

    public static final double FILL_VALUE = Double.parseDouble("1.e+36");
    
    public static double getDoubleValue(final Array array, final int i, final Number fillValue) {
        if (array instanceof ArrayInt.D1) {
            int val = ((ArrayInt.D1)array).get(i);
            if (val != N3iosp.NC_FILL_INT) {
                return val;
            }
        } else if (array instanceof ArrayDouble.D1) {
            double val =  ((ArrayDouble.D1)array).get(i);
            if (val != N3iosp.NC_FILL_DOUBLE && (fillValue == null || !fillValue.equals(val))) {
                return val;
            }
        } else if (array instanceof ArrayFloat.D1) {
            float val =  ((ArrayFloat.D1)array).get(i);
            if (val != N3iosp.NC_FILL_FLOAT && (fillValue == null || !fillValue.equals(val))) {
                return val;
            }
        } else if (array instanceof ArrayShort.D1) {
            short val =  ((ArrayShort.D1)array).get(i);
            if (val != N3iosp.NC_FILL_SHORT && (fillValue == null || !fillValue.equals(val))) {
                return val;
            }
        } else if (array instanceof ArrayLong.D1) {
            long val =  ((ArrayLong.D1)array).get(i);
            if (val != N3iosp.NC_FILL_LONG && (fillValue == null || !fillValue.equals(val))) {
                return val;
            }
        } else {
            throw new IllegalArgumentException("Unexpected Array type for field:" + array.getClass().getName() + " expecting D1");
        }
        return Double.NaN;
    }
    
    public static double getDoubleValue(final Array array, final Number fillValue) {
        if (array instanceof ArrayInt.D0) {
            int val = ((ArrayInt.D0)array).get();
            if (val != N3iosp.NC_FILL_INT && (fillValue == null || !fillValue.equals(val))) {
                return val;
            }
        } else if (array instanceof ArrayDouble.D0) {
            double val =  ((ArrayDouble.D0)array).get();
            if (val != N3iosp.NC_FILL_DOUBLE && (fillValue == null || !fillValue.equals(val))) {
                return val;
            }
        } else if (array instanceof ArrayFloat.D0) {
            float val =  ((ArrayFloat.D0)array).get();
            if (val != N3iosp.NC_FILL_FLOAT && (fillValue == null || !fillValue.equals(val))) {
                return val;
            }
        } else if (array instanceof ArrayShort.D0) {
            short val =  ((ArrayShort.D0)array).get();
            if (val != N3iosp.NC_FILL_SHORT && (fillValue == null || !fillValue.equals(val))) {
                return val;
            }
        } else if (array instanceof ArrayLong.D0) {
            long val =  ((ArrayLong.D0)array).get();
            if (val != N3iosp.NC_FILL_LONG && (fillValue == null || !fillValue.equals(val))) {
                return val;
            }
        } else {
            throw new IllegalArgumentException("Unexpected Array type for field:" + array.getClass().getName() + " expecting D0");
        }
        return Double.NaN;
    }
    
    public static double getDoubleValue(final boolean mainFirst, final Array array, int i, int j, final Number fillValue) {
        if (!mainFirst) {
            final int tmp = i;
            i = j;
            j = tmp;
        }
        
        if (array instanceof ArrayInt.D2) {
            int val = ((ArrayInt.D2)array).get(i, j);
            if (val != N3iosp.NC_FILL_INT && (fillValue == null || !fillValue.equals(val))) {
                return val;
            }
        } else if (array instanceof ArrayDouble.D2) {
            double val = ((ArrayDouble.D2)array).get(i, j);
            if (val != N3iosp.NC_FILL_DOUBLE && (fillValue == null || !fillValue.equals(val))) {
                return val;
            }
        } else if (array instanceof ArrayFloat.D2) {
            float val = ((ArrayFloat.D2)array).get(i, j);
            if (val != N3iosp.NC_FILL_FLOAT && (fillValue == null || !fillValue.equals(val))) {
                return val;
            }
        } else if (array instanceof ArrayShort.D2) {
            short val = ((ArrayShort.D2)array).get(i, j);
            if (val != N3iosp.NC_FILL_SHORT && (fillValue == null || !fillValue.equals(val))) {
                return val;
            }
        } else if (array instanceof ArrayLong.D2) {
            long val = ((ArrayLong.D2)array).get(i, j);
            if (val != N3iosp.NC_FILL_LONG && (fillValue == null || !fillValue.equals(val))) {
                return val;
            }
        } else {
            throw new IllegalArgumentException("Unexpected Array type for field:" + array.getClass().getName() + " expecting D2");
        }
        return Double.NaN;
    }
    
    public static double getDoubleValue(final Array array, int i, int j, int k, final Number fillValue) {
        
        
        if (array instanceof ArrayInt.D3) {
            int val = ((ArrayInt.D3)array).get(i, j, k);
            if (val != N3iosp.NC_FILL_INT && (fillValue == null || !fillValue.equals(val))) {
                return val;
            }
        } else if (array instanceof ArrayDouble.D3) {
            double val = ((ArrayDouble.D3)array).get(i, j, k);
            if (val != N3iosp.NC_FILL_DOUBLE && (fillValue == null || !fillValue.equals(val))) {
                return val;
            }
        } else if (array instanceof ArrayFloat.D3) {
            float val = ((ArrayFloat.D3)array).get(i, j, k);
            if (val != N3iosp.NC_FILL_FLOAT && (fillValue == null || !fillValue.equals(val))) {
                return val;
            }
        } else if (array instanceof ArrayShort.D3) {
            short val = ((ArrayShort.D3)array).get(i, j, k);
            if (val != N3iosp.NC_FILL_SHORT && (fillValue == null || !fillValue.equals(val))) {
                return val;
            }
        } else if (array instanceof ArrayLong.D3) {
            long val = ((ArrayLong.D3)array).get(i, j, k);
            if (val != N3iosp.NC_FILL_LONG && (fillValue == null || !fillValue.equals(val))) {
                return val;
            }
        } else {
            throw new IllegalArgumentException("Unexpected Array type for field:" + array.getClass().getName() + " expecting D3");
        }
        return Double.NaN;
    }
    
    public static long getTimeValue(final String units, final boolean mainFirst, boolean constantT, final Array array, int i, int j) {
        if (constantT) {
            return getTimeValue(units, array, i);
        } else {
            if (!mainFirst) {
                final int tmp = i;
                i = j;
                j = tmp;
            }
            if (array instanceof ArrayInt.D2) {
                final int value =  ((ArrayInt.D2)array).get(i, j);
                return parseTime(value, units);
            } else if (array instanceof ArrayDouble.D2) {
                final Double value = ((ArrayDouble.D2)array).get(i, j);
                return parseTime(value.longValue(), units);
            } else {
                throw new IllegalArgumentException("Unexpected Array type for time field:" + array.getClass().getName());
            }
        }
    }
    
    public static long getTimeValue(final String units, final Array array, final int i) {
        if (array instanceof ArrayInt.D1) {
            final int value = ((ArrayInt.D1)array).get(i);
            return parseTime(value, units);
        } else if (array instanceof ArrayDouble.D1) {
            final Double value = ((ArrayDouble.D1)array).get(i);
            return parseTime(value.longValue(), units);
        } else {
            throw new IllegalArgumentException("Unexpected Array type for time field:" + array.getClass().getName() + " expecting D1");
        }
    }
    
    public static double getZValue(final boolean mainFirst, boolean constantZ, final Array zArray, final int i, final int j, final Number fillValue) {
        if (constantZ) {
            return getDoubleValue(zArray, i, fillValue);
        } else {
            return getDoubleValue(mainFirst, zArray, i, j, fillValue);
        }
    }
    
    public static Type getTypeFromDataType(final DataType type) {
        switch(type) {
            case BOOLEAN:
                return Type.BOOLEAN;
            case BYTE:
                return Type.UNSUPPORTED;
            case CHAR:
                return Type.STRING;
            case SHORT:
                return Type.INT;
            case INT:
                return Type.INT;
            case LONG:
                return Type.INT;
            case FLOAT:
                return Type.DOUBLE;
            case DOUBLE:
                return Type.DOUBLE;
            case SEQUENCE:
                return Type.UNSUPPORTED;
            case STRING:
                return Type.STRING;
            case STRUCTURE:
                return Type.UNSUPPORTED;
            case ENUM1:
                return Type.UNSUPPORTED;
            case ENUM2:
                return Type.UNSUPPORTED;
            case ENUM4:
                return Type.UNSUPPORTED;
            case OPAQUE:
                return Type.UNSUPPORTED;
            /*case OBJECT:
                return Type.UNSUPPORTED;*/
            default: return Type.UNSUPPORTED;
        }
    }
    
    public static Dimension getGoodTimeDimension(final Variable timeVar, final String separatorDim) {
        if (separatorDim == null || timeVar.getDimensions().size() == 1) {
            return timeVar.getDimension(0);
        } else {
            for (Dimension d : timeVar.getDimensions()) {
                if (!d.getFullName().equals(separatorDim)) {
                    return d;
                }
            }
        }
        return null;
    }
    
    /**
     * The method Variable.getDimensionsString() seems to have issues.
     * @param var
     * @return 
     */
    public static String getDimensionString(final Variable var) {
        final List<Dimension> dims = var.getDimensions();
        final StringBuilder result = new StringBuilder();
        for (Dimension dim : dims) {
            result.append(dim.getFullName()).append(' ');
        }
        if (result.length() > 0) {
            result.deleteCharAt(result.length() -1);
        }
        return result.toString();
    }
    
    public static long parseTime(final long l, final String unit) {
        final CalendarDateUnit c = CalendarDateUnit.of("gregorian", unit);
        final CalendarDate date  = c.makeCalendarDate(l);
        return date.getMillis();
    }
}
