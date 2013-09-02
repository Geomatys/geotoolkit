/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
package org.geotoolkit.data.s57.internal;

import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.data.s57.annexe.S57PropertyType;

/**
 * TODO keep this for convenience if needed to recreate files for mariner extension.
 *
 * @author Johann Sorel (Geomatys)
 */
public class MarinerPropertyType {

    public static void main(String[] args) {

        //29
        final List<S57PropertyType> types = new ArrayList<>();
        S57PropertyType type;

        //----------------------------------------------------------------------
        type = new S57PropertyType();
        type.acronym = "catclr";
        type.code = 8194;
        type.fullName = "Category of clearing line";
        type.type = "E";
        type.expecteds.add("0=undefined");
        type.expecteds.add("1=NMT (not more than)");
        type.expecteds.add("2=NLT (not less than)");
        type.definition = "";
        type.references = "IEC 61174 Annex E Section 15";
        type.minimum = null;
        type.maximum = null;
        type.remarks = "The attribute “category of clearing line” describes the condition associated with the clearing line: \"NMT\"		means that in order to clear the danger, the bearing of the mark should be \" not more than\" the indicated value.\"NLT\"		means the bearing of the mark should not be \"not less than\" the indicated value.";
        type.indication = "";
        type.format = "";
        type.exemple = "";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57PropertyType();
        type.acronym = "catcur";
        type.code = 8195;
        type.fullName = "Category of current and tidal stream";
        type.type = "E";
        type.expecteds.add("0=undefined");
        type.expecteds.add("1=predicted");
        type.expecteds.add("2=actual");
        type.definition = "";
        type.references = "IEC 61174 Annex E Section 11";
        type.minimum = null;
        type.maximum = null;
        type.remarks = "Current and tidal stream may be predicted from tidal database or measured from available sensor information.";
        type.indication = "";
        type.format = "";
        type.exemple = "";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57PropertyType();
        type.acronym = "catnot";
        type.code = 8196;
        type.fullName = "Category of mariners' note";
        type.type = "E";
        type.expecteds.add("0=undefined");
        type.expecteds.add("1=information");
        type.expecteds.add("2=caution");
        type.definition = "";
        type.references = "";
        type.minimum = null;
        type.maximum = null;
        type.remarks = "The attribute “category of mariners’ note” depends on the importance of the information: A caution contains information about a danger, or instructions or advice. Information is any note containing other information.";
        type.indication = "";
        type.format = "";
        type.exemple = "";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57PropertyType();
        type.acronym = "catpst";
        type.code = 8197;
        type.fullName = "Category of past track";
        type.type = "E";
        type.expecteds.add("0=undefined");
        type.expecteds.add("1=primary");
        type.expecteds.add("2=secondary");
        type.definition = "";
        type.references = "IEC 61174 Annex E Section 2";
        type.minimum = null;
        type.maximum = null;
        type.remarks = "The attribute “category of past track” indicates whether the past track is derived from the primary or the secondary position finding system.";
        type.indication = "";
        type.format = "";
        type.exemple = "";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57PropertyType();
        type.acronym = "cogcrs";
        type.code = 8215;
        type.fullName = "Course over ground";
        type.type = "F";
        type.expecteds.add("");
        type.definition = "";
        type.references = "IEC 61174 Annex E Section 3, 3.3 – 3.5 & 3.6 – 3.8";
        type.minimum = null;
        type.maximum = null;
        type.remarks = "Distinction: 	'headng', ‘ctwcrs’.The attribute “course over ground “ specifies the value of the true course over the ground (course made good). For ownship,  ECDIS or the position finding system should automatically input the specific value for the course made good from measurements.";
        type.indication = "";
        type.format = "";
        type.exemple = "Specific value for course over the ground  in degrees.";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57PropertyType();
        type.acronym = "ctwcrs";
        type.code = 8216;
        type.fullName = "Course through water";
        type.type = "I";
        type.expecteds.add("");
        type.definition = "Unit	degrees (deg). one deg";
        type.references = "IEC 61174 Annex E";
        type.minimum = null;
        type.maximum = null;
        type.remarks = "The attribute “ctwcrs” is used to construct a water stabilized vector for own-ship or other vessel,  if selected by the mariner.The ECDIS should input the specific value for “ctwcrs”for own-ship from the compass.Distinction: ‘headng’ (“ctwcrs”is a smoothed version of the “headng»)‘cogcrs’";
        type.indication = "";
        type.format = "xxx";
        type.exemple = "315 for a course through the water of 315 deg.";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57PropertyType();
        type.acronym = "curstr";
        type.code = 8199;
        type.fullName = "Current strength";
        type.type = "F";
        type.definition = "Specific value for the strength of the current or tidal stream in knots.";
        type.references = "IEC 61174 Annex E Section 11";
        type.minimum = null;
        type.maximum = null;
        type.remarks = "The attribute “strength of current” specifies the strength (speed) of the current or tidal stream in knots. This may be deduced from sensor information, or predicted.";
        type.indication = "";
        type.format = "";
        type.exemple = "";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57PropertyType();
        type.acronym = "cursty";
        type.code = 8217;
        type.fullName = "Cursor style";
        type.type = "E";
        type.expecteds.add("1=solid cursor, style A");
        type.expecteds.add("2=open centred cursor, style B");
        type.definition = "";
        type.references = "IEC 61174 Annex E, section 5";
        type.minimum = null;
        type.maximum = null;
        type.remarks = "IEC 61174 defines the above two options for cursor style.";
        type.indication = "";
        type.format = "";
        type.exemple = "";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57PropertyType();
        type.acronym = "headng";
        type.code = 8211;
        type.fullName = "Heading";
        type.type = "F";
        type.definition = "Specific value for true heading in degrees.";
        type.references = "";
        type.minimum = null;
        type.maximum = null;
        type.remarks = "Distinction: ‘cogcrs’,‘ctwcrs’Heading: the horizontal direction in which a ship actually points or heads at any instant, expressed in angular units from a reference direction, usually from 000° at the reference direction clockwise through 360°. (Bowditch, American Practical Navigator, Vol.2, DMA, Pub.No.9, 1981).For the ECDIS application true north is the reference direction. The heading will normally be input automatically from the compass.";
        type.indication = "";
        type.format = "";
        type.exemple = "";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57PropertyType();
        type.acronym = "inptid";
        type.code = 8201;
        type.fullName = "Input identifier";
        type.type = "S";
        type.definition = "Specific value of input identifier (name or initials).";
        type.references = "Requirements of IMO PS 1.6";
        type.minimum = null;
        type.maximum = null;
        type.remarks = "Distinction: 'OBJNAM' The attribute “input identifier” is used for identification of the originator of a certain mariners' object, e.g. to identify the mariner who marked a danger highlight or who planned a route.";
        type.indication = "";
        type.format = "";
        type.exemple = "";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57PropertyType();
        type.acronym = "legchr";
        type.code = 8203;
        type.fullName = "Leg characteristic";
        type.type = "E";
        type.expecteds.add("0=undefined");
        type.expecteds.add("1=rhumb line");
        type.expecteds.add("2=great circle (geodesic)");
        type.definition = "";
        type.references = "";
        type.minimum = null;
        type.maximum = null;
        type.remarks = "The attribute “leg characteristic” specifies whether a leg is a rhumb line or a great circle.";
        type.indication = "";
        type.format = "";
        type.exemple = "";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57PropertyType();
        type.acronym = "loctim";
        type.code = 8202;
        type.fullName = "Local time";
        type.type = "A";
        type.definition = "Specific value for time indication in format HHMM or MM.";
        type.references = "IEC 61174 Annex E Section 2, 7, 8, 9, 11, 17";
        type.minimum = null;
        type.maximum = null;
        type.remarks = "Distinction: 'plndat' The attribute “local time” represents the local time of making an observation or of predicted arrival at a point, etc.";
        type.indication = "";
        type.format = "";
        type.exemple = "";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57PropertyType();
        type.acronym = "pfmeth";
        type.code = 8204;
        type.fullName = "Position finding method";
        type.type = "E";
        type.expecteds.add("0=undefined");
        type.expecteds.add("1=dead reckoning");
        type.expecteds.add("2=estimated");
        type.expecteds.add("3=visual");
        type.expecteds.add("4=astronomical");
        type.expecteds.add("5=RADAR");
        type.expecteds.add("6=Decca");
        type.expecteds.add("7=GPS");
        type.expecteds.add("8=Glonass");
        type.expecteds.add("9=Loran/Tchaika");
        type.expecteds.add("10=MFDF");
        type.expecteds.add("11=Omega");
        type.expecteds.add("12=Transit/Tsikada");
        type.expecteds.add("13=dGPS");
        type.expecteds.add("14=dGlonass");
        type.expecteds.add("15=dOmega");
        type.expecteds.add("16=dLoran");
        type.expecteds.add("17=dDecca");
        type.expecteds.add("18=Hi-Fix");
        type.expecteds.add("19=Syledis");
        type.expecteds.add("20=Microwave");
        type.expecteds.add("21=Radar transponder");
        type.definition = "";
        type.references = "IEC 61174 Annex E Section 7 and 8";
        type.minimum = null;
        type.maximum = null;
        type.remarks = "The attribute “position finding method” specifies the type of navigational sensor or the method by which a certain position is derived.";
        type.indication = "";
        type.format = "";
        type.exemple = "";
        types.add(type);



        //----------------------------------------------------------------------
        type = new S57PropertyType();
        type.acronym = "plnspd";
        type.code = 8205;
        type.fullName = "Planned speed to make good";
        type.type = "F";
        type.definition = "Specific value of the planned speed in knots.";
        type.references = "IEC 61174 Annex E Section 14";
        type.minimum = null;
        type.maximum = null;
        type.remarks = "Distinction: ‘sogspd’ ‘stwspd’ The attribute “planned speed” specifies the planned speed to make good for a certain leg in knots	. The planned speed should be shown in a box.";
        type.indication = "";
        type.format = "";
        type.exemple = "";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57PropertyType();
        type.acronym = "plndat";
        type.code = 8206;
        type.fullName = "Planned date";
        type.type = "F";
        type.definition = "Specific value of the planned date and time in a standard time and date format including the time zone as a option, e.g., \"20/1115\" (+5).";
        type.references = "IEC 61174 Annex E Section 16";
        type.minimum = null;
        type.maximum = null;
        type.remarks = "Distinction: 	'loctim', 'RECDAT'. The attribute “planned date” specifies the planned date  and time according to a schedule.";
        type.indication = "";
        type.format = "";
        type.exemple = "";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57PropertyType();
        type.acronym = "rudang";
        type.code = 8207;
        type.fullName = "Rudder angle";
        type.type = "I";
        type.definition = "Specific value for rudder angle in degrees starboard or port.";
        type.references = "IEC 61174 Annex E Section 19";
        type.minimum = null;
        type.maximum = null;
        type.remarks = "The attribute “rudder angle” specifies the value of the actual or desired rudder angle in degrees.";
        type.indication = "";
        type.format = "";
        type.exemple = "";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57PropertyType();
        type.acronym = "select";
        type.code = 8208;
        type.fullName = "Selection";
        type.type = "E";
        type.expecteds.add("0=undefined");
        type.expecteds.add("1=planned");
        type.expecteds.add("2=alternate");
        type.definition = "";
        type.references = "IEC 61174 Annex E Sections 14-17";
        type.minimum = null;
        type.maximum = null;
        type.remarks = "The attribute “selection” specifies whether a leg or waypoint is part of the planned route or part of the alternate route.";
        type.indication = "";
        type.format = "";
        type.exemple = "";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57PropertyType();
        type.acronym = "shpbrd";
        type.code = 8209;
        type.fullName = "Ship's breadth (beam)";
        type.type = "F";
        type.definition = "Specific value for the ship's breadth (beam) in metres.";
        type.references = "IEC 61174 Annex E Section 1";
        type.minimum = null;
        type.maximum = null;
        type.remarks = "Distinction: 'shplen' . The attribute “ship's breadth” specifies the value of the own ship's breadth in metres.";
        type.indication = "";
        type.format = "";
        type.exemple = "";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57PropertyType();
        type.acronym = "shplen";
        type.code = 8210;
        type.fullName = "Ship's length over all";
        type.type = "F";
        type.definition = "Specific value for ship's length over all in metres.";
        type.references = "IEC 61174 Annex E Section 1";
        type.minimum = null;
        type.maximum = null;
        type.remarks = "Distinction: 'shpbrd'. The attribute “ship's length over all” specifies the value of the own ship's length over all in metres.";
        type.indication = "";
        type.format = "";
        type.exemple = "";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57PropertyType();
        type.acronym = "sogspd";
        type.code = 8218;
        type.fullName = "Speed over ground";
        type.type = "F";
        type.definition = "Unit	: Knots (kn) Resolution: 0.1 kn";
        type.references = "IEC 61174 Annex E Section 3, 3.3 – 3.5 & 3.6 – 3.8";
        type.minimum = null;
        type.maximum = null;
        type.remarks = "Distinction: ‘plnspd’ ‘stwspd’ . The attribute “speed over ground” specifies the value of the ship's speed made good over the ground.";
        type.indication = "";
        type.format = "xx.x";
        type.exemple = "12.5 for a speed over the ground of 12.5 kn.";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57PropertyType();
        type.acronym = "stwspd";
        type.code = 8219;
        type.fullName = "Speed through water";
        type.type = "F";
        type.definition = "Unit	: knots (kn) Resolution: 0.1 kn";
        type.references = "IEC 61174 Annex E Sections 3, 3.3 – 3.5 & 3.6 – 3.8";
        type.minimum = null;
        type.maximum = null;
        type.remarks = "The attribute “stwspd” is used to construct a water stabilized vector for own-ship or other vessel,  if selected by the mariner. The ECDIS should input the specific value for “stwspd” for own-ship from a water-track speed log.. Distinction:	‘plnspd’ ‘sogspd’";
        type.indication = "";
        type.format = "xx.x";
        type.exemple = "12.5 a speed through the water of 12.5";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57PropertyType();
        type.acronym = "transf";
        type.code = 8212;
        type.fullName = "Transfer status of position line";
        type.type = "E";
        type.expecteds.add("0=undefined");
        type.expecteds.add("1=original position");
        type.expecteds.add("2=transferred position");
        type.definition = "";
        type.references = "IEC 61174 Annex E Section 8, 9";
        type.minimum = null;
        type.maximum = null;
        type.remarks = "The attribute “transfer status of position line” identifies whether a line of position is transferred or not.";
        type.indication = "";
        type.format = "";
        type.exemple = "";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57PropertyType();
        type.acronym = "trnrad";
        type.code = 8213;
        type.fullName = "Turning radius";
        type.type = "F";
        type.definition = "Specific value of the turning radius in nautical miles.";
        type.references = "Requirements of IMO PS 1.6";
        type.minimum = null;
        type.maximum = null;
        type.remarks = "The attribute “turning radius” specifies the turning radius at a waypoint in nautical miles.";
        type.indication = "";
        type.format = "";
        type.exemple = "";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57PropertyType();
        type.acronym = "usrmrk";
        type.code = 8214;
        type.fullName = "Users' remark";
        type.type = "S";
        type.definition = "Text string for users' remark.";
        type.references = "Requirements of IMO PS 1.6";
        type.minimum = null;
        type.maximum = null;
        type.remarks = "The attribute “users' remark” contains information, or annotation made by the mariner.";
        type.indication = "";
        type.format = "";
        type.exemple = "";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57PropertyType();
        type.acronym = "vecper";
        type.code = 8220;
        type.fullName = "Vector-length time-period";
        type.type = "I";
        type.definition = "Unit: minutes (min) Resolution: 1 min";
        type.references = "IEC 61174 Annex E";
        type.minimum = null;
        type.maximum = null;
        type.remarks = "The attribute “vecper” defines the time period which,  multiplied by the speed (“sogspd” or “stwspd”),  determines the length of the own-ship or other vessel vectors.";
        type.indication = "";
        type.format = "xxx";
        type.exemple = "12 for a 12 min vector-length period";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57PropertyType();
        type.acronym = "vecmrk";
        type.code = 8221;
        type.fullName = "Vector time-mark interval";
        type.type = "E";
        type.expecteds.add("1=time-mark every minute,  with a highlighted time-mark every six minutes.");
        type.expecteds.add("2=only highlighted time-mark every six minutes.");
        type.definition = "";
        type.references = "IEC 61174 Annex E";
        type.minimum = null;
        type.maximum = null;
        type.remarks = "The attribute “vecmrk” defines whether the mariner has selected one minute or six-minute time marks.";
        type.indication = "";
        type.format = "";
        type.exemple = "";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57PropertyType();
        type.acronym = "vecstb";
        type.code = 8222;
        type.fullName = "Vector Stabilization";
        type.type = "E";
        type.expecteds.add("1=ground stabilized");
        type.expecteds.add("2=water stabilized");
        type.definition = "";
        type.references = "IEC 61174 Annex E Sections 3 and 3.3 – 3.5";
        type.minimum = null;
        type.maximum = null;
        type.remarks = "The attribute “vecstb” describes whether an own-ship or other vessel vector is ground or water stabilized.";
        type.indication = "";
        type.format = "";
        type.exemple = "";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57PropertyType();
        type.acronym = "vesrce";
        type.code = 8223;
        type.fullName = "Vessel report source";
        type.type = "E";
        type.expecteds.add("1=ARPA target");
        type.expecteds.add("2=AIS vessel report");
        type.expecteds.add("3=VTS report");
        type.definition = "";
        type.references = "IEC 61174 Annex E";
        type.minimum = null;
        type.maximum = null;
        type.remarks = "The attribute “vesrce” identifies the source of information about a vessel other than own-ship.";
        type.indication = "";
        type.format = "";
        type.exemple = "";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57PropertyType();
        type.acronym = "vestat";
        type.code = 8224;
        type.fullName = "Vessel Status";
        type.type = "E";
        type.expecteds.add("1=active,  meaning target shown with large symbol plus vector, heading line and rate or direction of turn indication if available.");
        type.expecteds.add("2=sleeping,  meaning target shown by oriented small vessel symbol, indicating presence and orientation but no additional information.");
        type.expecteds.add("3=selected, meaning selected manually for the display of detailed information in a separate data display area.");
        type.expecteds.add("4=dangerous, meaning a target whose data contravenes pre-set CPA and/or TCPA limits.");
        type.expecteds.add("5=lost, meaning a symbol representing the last valid position of an AIS target before data reception was lost.");
        type.definition = "";
        type.references = "IEC 61174 Annex E";
        type.minimum = null;
        type.maximum = null;
        type.remarks = "The attribute “vestat” defines whether an AIS vessel report is ‘active’, ‘sleeping’, 'selected',  'dangerous', or 'lost' as defined above.";
        type.indication = "";
        type.format = "";
        type.exemple = "";
        types.add(type);


        for(S57PropertyType t : types){
            System.out.println(t.toFormattedString());
        }

    }


}
