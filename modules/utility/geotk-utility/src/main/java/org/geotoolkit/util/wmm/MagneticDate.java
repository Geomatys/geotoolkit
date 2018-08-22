/*
 * Geotoolkit.org - An Open Source Java GIS Toolkit
 * http://www.geotoolkit.org
 *
 * (C) 2018, Geomatys.
 *
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package org.geotoolkit.util.wmm;

/**
 * Java port of the NOAA's MAGtype_Date structure
 * @author Hasdenteufel Eric (Geomatys)
 */
public class MagneticDate {
    int Year;
    int Month;
    int Day;
    double DecimalYear; /* decimal years */

    /**
     *
     * @param Year full year of the magnetic date
     * @param Month  the month of the magnetic date, valid months are '1 to 12'
     * @param Day the day of the magnetic date, valid day are '1 to 31'
     */
    public MagneticDate(int Year, int Month, int Day) {
        this.Year = Year;
        this.Month = Month;
        this.Day = Day;

        if(Month==0) {
            this.DecimalYear = Year;
        } else {

            int temp = 0;
            int ExtraDay =  ((this.Year % 4 == 0 && this.Year % 100 != 0) || this.Year % 400 == 0)? 0 : 1;
            final int[] MonthDays = new int[] {
                0, 31, 28 +ExtraDay, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
            };

            /******************Validation********************************/
            if(this.Month <= 0 || this.Month > 12)
            {
                throw new IndexOutOfBoundsException("The Month parameter is invalid, valid months are '1 to 12'");
            }
            if(this.Day <= 0 || this.Day > MonthDays[this.Month])
            {
                throw new IndexOutOfBoundsException("The day parameter is invalid\nhe number of days in month "+ this.Month +" is "+ MonthDays[this.Month] +"\n");
            }
            /****************Calculation of t***************************/
            for(int i = 1; i <= this.Month; i++) {
                temp += MonthDays[i - 1];
            }
            temp += this.Day;
            this.DecimalYear = this.Year + (temp - 1) / (365.0 + ExtraDay);

        }

    }

    public MagneticDate(double decimalYear) {
        this.DecimalYear = decimalYear;
        this.Year = -1;
        this.Month = -1;
        this.Day = -1;
    }


}
