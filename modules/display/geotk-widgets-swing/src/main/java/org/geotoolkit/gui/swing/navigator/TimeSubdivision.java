/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.gui.swing.navigator;

import java.awt.FontMetrics;
import java.util.*;

import org.geotoolkit.temporal.object.TemporalConstants;

/**
 * A single temporal subdivision used by DateRenderer.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface TimeSubdivision {

    /**
     * @return unit text
     */
    public String getUnitText();

    /**
     * We expect subdivisions to be regular.
     * (can be unexact like are months and years)
     *
     * @return number of milliseconds to match one unit.
     *     example : 1 minute would be 60000.
     */
    public double getUnitLength();
    
    /**
     * Intermediate division will not be displayed in the lower bar.
     * @return true if division is intermediate.
     */
    public boolean isIntermediate();

    /**
     * @return maximum size of a text element
     */
    public double getTextLength(FontMetrics fm);

    /**
     * @param milliseconds
     * @return Text for this time unit.
     */
    public String getText(long milliseconds);

    /**
     *
     * @param begin
     * @param end
     * @return steps between the two values
     */
    public long[] getSteps(long begin, long end);


    public static class Year implements TimeSubdivision {

        @Override
        public String getUnitText() {
            return "";
        }

        @Override
        public double getUnitLength() {
            return TemporalConstants.YEAR_MS;
        }

        @Override
        public boolean isIntermediate() {
            return false;
        }
        
        @Override
        public double getTextLength(FontMetrics fm) {
            return fm.stringWidth("99999");
        }

        @Override
        public String getText(long milliseconds) {
            int value = (int) (milliseconds/TemporalConstants.YEAR_MS);
            return String.valueOf(1970+value);
        }

        @Override
        public long[] getSteps(long begin, long end) {
            final GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(begin);
            calendar.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            calendar.set(Calendar.MONTH, 0);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.MILLISECOND, 1);

            final List<Long> steps = new ArrayList<Long>();
            steps.add(calendar.getTimeInMillis());
            calendar.add(Calendar.YEAR, 1);
            while(calendar.getTimeInMillis() <= end){
                steps.add(calendar.getTimeInMillis());
                calendar.add(Calendar.YEAR, 1);
            }

            final long[] array = new long[steps.size()];
            for(int i=0,n=steps.size(); i<n; i++){
                array[i] = steps.get(i);
            }
            return array;
        }

    }

    public static class Month implements TimeSubdivision {

        @Override
        public String getUnitText() {
            return "";
        }

        @Override
        public double getUnitLength() {
            return TemporalConstants.MONTH_MS;
        }

        @Override
        public boolean isIntermediate() {
            return false;
        }
        
        @Override
        public double getTextLength(FontMetrics fm) {
            return fm.stringWidth("AAAAAA"); //should not be longer
        }

        @Override
        public String getText(long milliseconds) {
            final GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(milliseconds);
            calendar.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            return calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
        }

        @Override
        public long[] getSteps(long begin, long end) {
            final GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(begin);
            calendar.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.MILLISECOND, 1);

            final List<Long> steps = new ArrayList<Long>();
            steps.add(calendar.getTimeInMillis());
            calendar.add(Calendar.MONTH, 1);
            while(calendar.getTimeInMillis() <= end){
                steps.add(calendar.getTimeInMillis());
                calendar.add(Calendar.MONTH, 1);
            }

            final long[] array = new long[steps.size()];
            for(int i=0,n=steps.size(); i<n; i++){
                array[i] = steps.get(i);
            }
            return array;
        }

    }

    public static class Day implements TimeSubdivision {

        @Override
        public String getUnitText() {
            return "d";
        }

        @Override
        public double getUnitLength() {
            return TemporalConstants.DAY_MS;
        }

        @Override
        public boolean isIntermediate() {
            return false;
        }
        
        @Override
        public double getTextLength(FontMetrics fm) {
            return fm.stringWidth("AA"); //31 day max
        }

        @Override
        public String getText(long milliseconds) {
            final GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(milliseconds);
            calendar.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            return String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        }

        @Override
        public long[] getSteps(long begin, long end) {
            final GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(begin);
            calendar.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.MILLISECOND, 1);

            final List<Long> steps = new ArrayList<Long>();
            steps.add(calendar.getTimeInMillis());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            while(calendar.getTimeInMillis() <= end){
                steps.add(calendar.getTimeInMillis());
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            final long[] array = new long[steps.size()];
            for(int i=0,n=steps.size(); i<n; i++){
                array[i] = steps.get(i);
            }
            return array;
        }

    }

    public static class Hour implements TimeSubdivision {

        @Override
        public String getUnitText() {
            return "h";
        }

        @Override
        public double getUnitLength() {
            return TemporalConstants.HOUR_MS;
        }

        @Override
        public boolean isIntermediate() {
            return false;
        }
        
        @Override
        public double getTextLength(FontMetrics fm) {
            return fm.stringWidth("AA"); //24 max
        }

        @Override
        public String getText(long milliseconds) {
            final GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(milliseconds);
            calendar.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            return String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        }

        @Override
        public long[] getSteps(long begin, long end) {
            final GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(begin);
            calendar.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.MILLISECOND, 1);

            final List<Long> steps = new ArrayList<Long>();
            steps.add(calendar.getTimeInMillis());
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            while(calendar.getTimeInMillis() <= end){
                steps.add(calendar.getTimeInMillis());
                calendar.add(Calendar.HOUR_OF_DAY, 1);
            }

            final long[] array = new long[steps.size()];
            for(int i=0,n=steps.size(); i<n; i++){
                array[i] = steps.get(i);
            }
            return array;
        }

    }

    public static class Quarter implements TimeSubdivision {

        @Override
        public String getUnitText() {
            return "m";
        }

        @Override
        public double getUnitLength() {
            return TemporalConstants.MINUTE_MS * 15;
        }

        @Override
        public boolean isIntermediate() {
            return true;
        }
        
        @Override
        public double getTextLength(FontMetrics fm) {
            return fm.stringWidth("AA"); //60 max
        }

        @Override
        public String getText(long milliseconds) {
            final GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(milliseconds);
            calendar.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            return String.valueOf(calendar.get(Calendar.MINUTE));
        }

        @Override
        public long[] getSteps(long begin, long end) {
            final GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(begin);
            calendar.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.MILLISECOND, 1);

            final List<Long> steps = new ArrayList<Long>();
            steps.add(calendar.getTimeInMillis());
            calendar.add(Calendar.MINUTE, 15);
            while(calendar.getTimeInMillis() <= end){
                steps.add(calendar.getTimeInMillis());
                calendar.add(Calendar.MINUTE, 15);
            }

            final long[] array = new long[steps.size()];
            for(int i=0,n=steps.size(); i<n; i++){
                array[i] = steps.get(i);
            }
            return array;
        }

    }
    
    public static class Minute implements TimeSubdivision {

        @Override
        public String getUnitText() {
            return "m";
        }

        @Override
        public double getUnitLength() {
            return TemporalConstants.MINUTE_MS;
        }

        @Override
        public boolean isIntermediate() {
            return true;
        }
        
        @Override
        public double getTextLength(FontMetrics fm) {
            return fm.stringWidth("AA"); //60 max
        }

        @Override
        public String getText(long milliseconds) {
            final GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(milliseconds);
            calendar.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            return String.valueOf(calendar.get(Calendar.MINUTE));
        }

        @Override
        public long[] getSteps(long begin, long end) {
            final GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(begin);
            calendar.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.MILLISECOND, 1);

            final List<Long> steps = new ArrayList<Long>();
            steps.add(calendar.getTimeInMillis());
            calendar.add(Calendar.MINUTE, 1);
            while(calendar.getTimeInMillis() <= end){
                steps.add(calendar.getTimeInMillis());
                calendar.add(Calendar.MINUTE, 1);
            }

            final long[] array = new long[steps.size()];
            for(int i=0,n=steps.size(); i<n; i++){
                array[i] = steps.get(i);
            }
            return array;
        }

    }

}
