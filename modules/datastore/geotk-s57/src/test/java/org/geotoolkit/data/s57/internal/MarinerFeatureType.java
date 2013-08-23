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
import org.geotoolkit.data.s57.annexe.S57FeatureType;

/**
 * TODO keep this for convenience if needed to recreate files for mariner extension.
 *
 * @author Johann Sorel (Geomatys)
 */
public class MarinerFeatureType {

    public static void main(String[] args) throws Exception {

        final List<S57FeatureType> types = new ArrayList<>();
        S57FeatureType type;

        //----------------------------------------------------------------------
        type = new S57FeatureType();
        type.acronym      = "clrlin";
        type.code         = 8193;
        type.fullName     = "Clearing line";
        type.description  = "Clearing line: A straight line constructed through suitably selected clearing" +
                        "marks so as to pass clear of certain dangers to navigation. (Bowditch," +
                        "American Practical Navigator, Vol.2, DMA, Pub.No.9, 1981).";
        type.remarks      = "Distinction: 'ebline', 'poslin'" +
                        "The attribute 'category of clearing line' carries the annotation:" +
                        "\"NMT\" {bearing}" +
                        "\"NLT\" {bearing}" +
                        "\"not more than {bearing}\" or" +
                        "\"not less than {bearing}\"" +
                        "which means that the target object should always be kept outside the limits of" +
                        "this constraint." +
                        "The bearing is calculated from the ship to the mark.";
        type.reference    = "IEC 61174 Annex E Section 15";
        type.attA.add("catclr");
        type.attB.add("SCAMIN");
        type.attC.add("inptid");
        type.geometricPrimitive = "Line;";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57FeatureType();
        type.acronym      = "cursor";
        type.code         = 8194;
        type.fullName     = "Cursor";
        type.description  = "A 'cursor' is used as a pointer for various purposes, e.g. establishing a waypoint; obtaining further information about a feature that the cursor is pointing at, such as a light; reading geographical coordinates; for bearing and distance measurements; and so on. It is moved by a pointing device such as trackball, mouse or arrow keys. While on the screen area of seachart presentation, the cursor refers to an absolute geographical position.";
        type.remarks      = "Distinction: 'refpnt'";
        type.reference    = "IEC 61174 Annex E Section 5";
        type.attA.add("cursty");
        type.geometricPrimitive = "Point;";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57FeatureType();
        type.acronym      = "dnghlt";
        type.code         = 8195;
        type.fullName     = "Danger highlight";
        type.description  = "A 'danger highlight' is used by the mariner to draw attention to a hazard he believes to be dangerous to his ship.";
        type.remarks      = "Distinction: 'events', 'marfea', 'mnufea', 'refpnt'. The symbol is a transparent red square or freely drawn polygon positioned by the mariner. It may be flashing, at the mariner's discretion. All underlying chart data should be clearly visible.";
        type.reference    = "IEC 61174 Annex E Section 12";
        type.attB.add("SCAMIN");
        type.attC.add("inptid");
        type.geometricPrimitive = "Point, Area;";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57FeatureType();
        type.acronym      = "ebline";
        type.code         = 8196;
        type.fullName     = "Electronic bearing line";
        type.description  = "An 'electronic bearing line' is a rotatable line used for bearing marking or measurement.";
        type.remarks      = "Distinction: 'clrlin', 'poslin'. The electronic bearing line may be ship centred or freely movable.";
        type.reference    = "IEC 61174 Annex E Section 4";
        type.geometricPrimitive = "Line;";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57FeatureType();
        type.acronym      = "events";
        type.code         = 8197;
        type.fullName     = "Event";
        type.description  = "An event marks the ship's position at the instant the mariner detects an event.";
        type.remarks      = "Distinction: 'marnot'. The event object is solely applicable during route monitoring. The symbol may be numbered and have additional text such as time or \"MOB\" (man over board) associated with it.";
        type.reference    = "IEC 61174 Annex E Section 6";
        type.attA.add("OBJNAM");
        type.attB.add("SCAMIN");
        type.attB.add("usrmrk");
        type.attC.add("inptid");
        type.attC.add("loctim");
        type.geometricPrimitive = "Point;";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57FeatureType();
        type.acronym      = "leglin";
        type.code         = 8198;
        type.fullName     = "Leg";
        type.description  = "A 'leg' is a line which connects two waypoints. A series of waypoints and legs makes up a route.";
        type.remarks      = "The attribute “select” should be used to distinguish planned and alternate routes and their legs.The attribute “legchr” should be used to distinguish great circle routes.Course and distance of a legline are calculated from the geometric primitive and therefore need no attribute.The colour for the selected planned route is PLRTE.  For the alternate planned route it is APLRT. As required by the mariner, the ECDIS voyage planning system should:(a.) Annotate each leg with planned course (always 3 digits) and speed to make good. Speed is shown in a box ()  {SY(PLNSPD03) or SY(PLNSPD04)}.(b.) Mark the distance to run in nautical miles () SY (PLNPOSO2).(c.)	Mark the planned position, with date and time enclosed in an ellipse.   {SY(PLNPOS01)}.A leg which belongs to an alternate route is displayed as a orange  dotted line  (APLRT). A leg which belongs to a planned route is displayed as a heavy red dotted line (PLRTE).";
        type.reference    = "IEC 61174 Annex E Section 14, 15, 16, 17";
        type.attA.add("legchr");
        type.attA.add("plnspd");
        type.attA.add("select");
        type.attC.add("inptid");
        type.geometricPrimitive = "Line;";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57FeatureType();
        type.acronym      = "marfea";
        type.code         = 8199;
        type.fullName     = "Mariners' feature";
        type.description  = "A 'mariners' feature' is a feature added to the SENC by the mariner. Examples are a mariners' caution or information symbol referring to a real object, or additional chart information from his own observation,  or from a pilot or other reliable source.";
        type.remarks      = "Distinction: 'dnghlt', 'marnot', 'mnufea', 'positn'. A \"danger highlight\" shall solely be used to define an existing chart feature believed to be dangerous. The mariners' feature is used by the mariner also for observation report purposes. Whilst a “mariners' note” consists of text written on the display, the “mariners' feature” may consist of symbols, lines or areas drawn interactively by the mariner, as he does on the paper chart. The colour allocated to mariners' features is NINFO. point feature:	an exclamation mark in a circle or a small letter \"I\" in a box or any chart symbol in orange. line feature:	solid or dashed line, NINFO,   1 or 2 pixels wide. area feature:	if a filled area is required,  use area fill, , 75% transparency,  ADINF {AC(ADINF,3)}.Chart features drawn by the mariner should be distinguished as described in Part1 Section 8.7 of this document.";
        type.reference    = "To meet the requirement of IMO PS 1.6, and of IHO S-52 sections 1.1 and 5.5";
        type.attA.add("OBJNAM");
        type.attB.add("SCAMIN");
        type.attB.add("usrmrk");
        type.attC.add("inptid");
        type.attC.add("RECDAT");
        type.attC.add("loctim");
        type.geometricPrimitive = "Area; Line; Point;";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57FeatureType();
        type.acronym      = "marnot";
        type.code         = 8200;
        type.fullName     = "Mariners' Note";
        type.description  = "A “mariners' note” is textual information defined by the mariner which is related to a certain geographic position.";
        type.remarks      = "Distinction: 'events', 'marfea', 'mnufea'. The attribute “category of mariners' note” ('catnot') classifies the stored textual information according to the importance, that is whether it is a \"caution\" or \"information\" note.An exclamation mark in a circle or a small letter \"I\" in a rectangle,  SY(CHINFO08) or SY(CONF.), colour NINFO.";
        type.reference    = "To meet the requirements of IMO PS 1.6";
        type.attA.add("catnot");
        type.attB.add("SCAMIN");
        type.attB.add("usrmrk");
        type.attC.add("inptid");
        type.attC.add("RECDAT");
        type.geometricPrimitive = "Point;";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57FeatureType();
        type.acronym      = "mnufea";
        type.code         = 8201;
        type.fullName     = "Manufacturers' feature";
        type.description  = "A feature or information added by the manufacturer of the ECDlS. Examples are a caution or information symbol for cursor picking to read out the information on the alphanumeric display; additional chart information not available in the ENC; manufacturers' value-added feature; etc.";
        type.remarks      = "Distinction : 'marfea', ' marnot', ' dnghlt'. The colour allocated to all manufacturers' information is ADINF.Point feature: 	the caution or information symbol; SY(CHINFO10) or SY(CHINFO11) or manufacturer-provided chart information, distinguished from HO data by the   colour ADINF. Line feature: 	solid line, 1 or 2 pixels wide, for manufacturers' non-chart information; or manufacturer-provided chart information in HO chart linestyles,  distinguished from HO data as described in Part 1 Section 8.7 of this document. Area feature: 	area outlined by a solid line, 2 pixels wide,  for manufacturers' non-chart information; or manufacturers' chart information symbolized as for HO chart areas, distinguished from HO data  as described in Part 1 Section 8.7 of this document. Note that manufacturers' areas, whether non-chart or chart areas,  should not use area colour fill.";
        type.reference    = "To implement IMO PS section 2 : \"The SENC may also contain information from other sources\", and IHO S-52 section 2 : \"Additional information obtained from other sources should be distinguished from HO data.\"";
        type.attA.add("OBJNAM");
        type.attB.add("SCAMIN");
        type.attB.add("usrmrk");
        type.attC.add("RECDAT");
        type.geometricPrimitive = "Area; Line; Point;";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57FeatureType();
        type.acronym      = "ownshp";
        type.code         = 8202;
        type.fullName     = "Own ship";
        type.description  = "The \"own ship\" is the ECDIS user's ship and the pivot point is the conning position, which is where the mariner controls the ship. The conning position and the position of the navigation system antenna will seldom coincide.";
        type.remarks      = "Distinction: 'vessel'. The ship's position must first be adjusted for the offset between the navigating antenna and the conning position. Two symbols are available for own ship, at mariners' option:(a.)	Two concentric circles. (b.)	A symbol representative of the own ship's outline, drawn to indicate the length and breadth (beam) of the ship at the scale of the ECDIS display.This scaled symbol should only be used if the scaled ship's length is not less than 6 mm.In drawing the outline of the ship, the offset of the conning position from the artificial pivot point in the centre of the scaled ship symbol must be taken into account.  Both these symbols include the optional heading and beam bearing lines..The symbolization of own ship is ruled by a conditional symbology procedure which also applies the course & speed  vector  and selects the own ship symbol or the scaled ship symbol depending on the mariner's selection, so long as the scaled symbol is at least 6 mm long.";
        type.reference    = "IEC 61174 Annex E Section 1";
        type.attA.add("cogcrs");
        type.attA.add("headng");
        type.attA.add("sogspd");
        type.attA.add("shpbrd");
        type.attA.add("shplen");
        type.attA.add("ctwcrs");
        type.attA.add("stwspd");
        type.attA.add("vecper");
        type.attA.add("vecmrk");
        type.attA.add("vecstb");
        type.geometricPrimitive = "Point;";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57FeatureType();
        type.acronym      = "pastrk";
        type.code         = 8203;
        type.fullName     = "Past track";
        type.description  = "[Past‑]Track: The actual path of a vessel over the ground, such as may be determined by tracking.  (Bowditch, American Practical Navigator, Vol.2, DMA, Pub.No.9, 1981).";
        type.remarks      = "The past track refers to the own ship. The attribute 'category of past track' indicates whether the past track is derived from the primary or the secondary navigational sensor.The past track is symbolized by a solid line, colour PSTRK or SYTRK, depending on the attribute catpst. The ECDIS should provide labeled time marks, as selected by the mariner. Time to be HHMM or MM.";
        type.reference    = "IEC 61174 Annex E Section 2";
        type.attA.add("catpst");
        type.attA.add("pfmeth");
        type.attC.add("RECDAT");
        type.attC.add("loctim");
        type.geometricPrimitive = "Line;";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57FeatureType();
        type.acronym      = "plnpos";
        type.code         = 8204;
        type.fullName     = "Planned position";
        type.description  = "A planned position marks the position on a leg where the own ship shall be at a certain date and time.";
        type.remarks      = "Distinctions: 'positn', 'refpnt', 'waypnt'. The 'planned position' is used to mark a planned position on a legline according to a sailing schedule. As required by the mariner, the ECDIS voyage planning system should mark the planned position with a dash crossing the leg line, with date and time enclosed in an ellipse. ) (SY(PLNPOS01).";
        type.reference    = "IEC 61174 Annex E Section 17";
        type.attA.add("ORIENT");
        type.attB.add("SCAMIN");
        type.attC.add("inptid");
        type.attC.add("plndat");
        type.geometricPrimitive = "Point;";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57FeatureType();
        type.acronym      = "positn";
        type.code         = 8205;
        type.fullName     = "Position";
        type.description  = "An ECDIS position is based on either dead reckoning, estimated position including the effect of currents, or observation of celestial or terrestrial objects or of an electronic position finding system.";
        type.remarks      = "Distinction: 'plnpos', 'refpnt'";
        type.reference    = "IEC 61174 Annex E Sections 7, 8";
        type.attA.add("pfmeth");
        type.attB.add("SCAMIN");
        type.attC.add("inptid");
        type.attC.add("loctim");
        type.geometricPrimitive = "Point;";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57FeatureType();
        type.acronym      = "poslin";
        type.code         = 8206;
        type.fullName     = "Position Line";
        type.description  = "Position line : A line indicating a series of possible positions of a craft, determined by observation or measurement. (Bowditch, American Practical Navigator, Vol.2, DMA, Pub.No.9, 1981).";
        type.remarks      = "Distinction: 'ebline', 'leglin', 'clrlin', 'wholin'. Whether a position line is transferred or not is indicated by the value of the attribute 'transf'. The ECDIS should provide a time label, and a notation  \"TPL\" on a transferred position line, as required by the mariner.";
        type.reference    = "IEC 61174 Annex E Section 9, 10";
        type.attA.add("transf");
        type.attB.add("SCAMIN");
        type.attC.add("inptid");
        type.attC.add("loctim");
        type.geometricPrimitive = "Line;";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57FeatureType();
        type.acronym      = "refpnt";
        type.code         = 8207;
        type.fullName     = "Cursor reference point";
        type.description  = "A 'cursor reference point' is a geographical position marked by the mariner with the cursor for reference purposes, e.g. measuring distance and bearing.";
        type.remarks      = "Distinction: 'cursor', 'dnghlt', 'plnpos', 'positn', 'waypnt'. The reference point is a tool for doing chartwork.";
        type.reference    = "To meet the requirements of IMO PS 1.6";
        type.geometricPrimitive = "Point;";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57FeatureType();
        type.acronym      = "rngrng";
        type.code         = 8208;
        type.fullName     = "Range ring";
        type.description  = "A 'range ring' is a circle with a defined radius located with its centre at the position of the own ship.";
        type.remarks      = "Distinction: 'vrmark'. Range rings with different radii are used to mark fix distances from a vessel. A circle LS(SOLD,1,NINFO)";
        type.reference    = "To meet the requirements of IMO PS 1.6";
        type.geometricPrimitive = "Line;";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57FeatureType();
        type.acronym      = "tidcur";
        type.code         = 8209;
        type.fullName     = "Tidal stream or current vector";
        type.description  = "The \"tidal stream or current vector\" describes a predicted or actual, observed or measured tidal stream or current.";
        type.remarks      = "S‑57 objects TS_FEB, TS_PRH, TS_PNH, TS_PAD, TS_TIS. The tidal stream or current vector describes the current observed or deduced by the mariner or calculated by a tide prediction system at a certain time at a certain position.“Tidcur” refers to tidal stream or current information other than that contained in the ENC.";
        type.reference    = "IEC 61174 Annex E Section 11";
        type.attA.add("catcur");
        type.attA.add("curstr");
        type.attA.add("ORIENT");
        type.attB.add("SCAMIN");
        type.attC.add("inptid");
        type.attC.add("loctim");
        type.geometricPrimitive = "Point;";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57FeatureType();
        type.acronym      = "vessel";
        type.code         = 8210;
        type.fullName     = "Vessel";
        type.description  = "The 'vessel'  is any ship about which information is available (e.g. from AIS or ARPA information).";
        type.remarks      = "Distinction: 'ownshp'";
        type.reference    = "Required under IMO PS section 6 to ensure that symbols representing another vessel input from AIS, ARPA or other source are consistent with SENC information on the ECDIS display.";
        type.attA.add("cogcrs");
        type.attA.add("sogspd");
        type.attA.add("ctwcrs");
        type.attA.add("stwspd");
        type.attA.add("vesrce");
        type.attA.add("vestat");
        type.attA.add("vecper");
        type.attA.add("vecmrk");
        type.attA.add("vecstb");
        type.attB.add("SCAMIN");
        type.attC.add("RECDAT");
        type.attC.add("loctim");
        type.geometricPrimitive = "Point;";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57FeatureType();
        type.acronym      = "vrmark";
        type.code         = 8211;
        type.fullName     = "Variable range marker";
        type.description  = "The 'variable range marker' is a range ring, the radius of which is continuously adjustable.Alternatively,  it is the range mark on an electronic range and bearing line.";
        type.remarks      = "Distinction: 'rngrng'. The variable range marker may be ship centred or freely movable.";
        type.reference    = "IEC 61174 Annex E Section 4";
        type.geometricPrimitive = "Line;";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57FeatureType();
        type.acronym      = "waypnt";
        type.code         = 8212;
        type.fullName     = "Waypoint";
        type.description  = "A 'waypoint' is a freely defined geographical point which may be independent of a certain leg.";
        type.remarks      = "Distinction: 'plnpos', 'refpnt'.There may be more attributes added to fit the needs of trackkeeping systems and transfer parameter data which may be calculated by the voyage‑planning system. The attributes above should be the only ones with values that have to be defined by the mariner. The attribute select is used to distinguish planned and alternate waypoints.Waypoints have spatial relations to leg lines so that they may define the beginnings or ends of legs.Waypoints may be labeled. The label must be unique. The first character must be a letter, but not \"o\", \"I\" or \"z\". Its colour depends whether it belongs to a planned or an alternate route.";
        type.reference    = "IEC 61174 Annex E Section 15";
        type.attA.add("OBJNAM");
        type.attA.add("rudang");
        type.attA.add("select");
        type.attA.add("trnrad");
        type.attB.add("usrmrk");
        type.attC.add("inptid");
        type.geometricPrimitive = "Point;";
        types.add(type);

        //----------------------------------------------------------------------
        type = new S57FeatureType();
        type.acronym      = "wholin";
        type.code         = 8213;
        type.fullName     = "Wheel-over-line";
        type.description  = "The wheel over line is the line drawn parallel to the next leg so that it intersects with the current leg and defines the point at which the turn should be started in order to accurately attain the next leg.";
        type.remarks      = "Distinction: 'poslin'. The point of intersection of the wheel over line and the current leg will be governed by rudder angle, turning radius, wind speed and direction and tidal stream rate and direction. The wheel-over-line is symbolized by a solid orange line.";
        type.reference    = "IEC 61174 Annex E Section 19";
        type.attA.add("ORIENT");
        type.attB.add("SCAMIN");
        type.attB.add("usrmrk");
        type.attC.add("loctim");
        type.geometricPrimitive = "Line;";
        types.add(type);


        for(S57FeatureType t : types){
            System.out.println(t.toFormattedString());
        }
    }

}
