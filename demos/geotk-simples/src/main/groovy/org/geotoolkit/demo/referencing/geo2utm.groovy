#!/usr/bin/env groovy
/*
 Use as: geo2utm.groovy [file]

 'file' should be a CSV file with a header row that contains 'latitude' and 'longitude'
 columns. This script converts lat and lon to UTM zone 10 and calculates the distance
 between the point on each consecutive line. The results are appended to the CSV data
 and printed to the terminal. For an example, see the "coordinates.csv" file.

 This file is hereby placed into the Public Domain.
 This means anyone is free to do whatever they wish with this file.

 @author Brian Schlining
 */
@Grapes([
    @GrabConfig(systemClassLoader=true),
    @Grab(group = "org.opengis",      module = "geoapi-pending",    version="2.3-M9"),
    @Grab(group = "org.geotoolkit",   module = "geotk-utility",     version="3.16"),
    @Grab(group = "org.geotoolkit",   module = "geotk-metadata",    version="3.16"),
    @Grab(group = "org.geotoolkit",   module = "geotk-referencing", version="3.16"),
    @Grab(group = "org.geotoolkit",   module = "geotk-epsg",        version="3.16"),
    @Grab(group = "org.apache.derby", module = "derbyclient",       version="10.6.1.0"),
    @Grab(group = "org.apache.derby", module = "derbynet",          version="10.6.1.0"),
    @Grab(group = "ch.qos.logback",   module = 'logback-classic',   version="0.9.27")
])
import java.text.DecimalFormat
import org.opengis.geometry.DirectPosition
import org.opengis.referencing.operation.MathTransform
import org.opengis.referencing.crs.CoordinateReferenceSystem
import org.geotoolkit.referencing.CRS
import org.geotoolkit.referencing.crs.DefaultGeographicCRS
import org.geotoolkit.geometry.DirectPosition2D
import org.geotoolkit.geometry.GeneralDirectPosition

def file = new File(args[0])

// Get the MathTransform once for all before to loop over the lines.
// The 3 first lines below are costly, so better to execute them only once.
CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326")  // WGS84 (lat, lon)
CoordinateReferenceSystem targetCRS = CRS.decode('EPSG:32610') // UTM Zone 10N
MathTransform tr = CRS.findMathTransform(sourceCRS, targetCRS)
DirectPosition sourcePt   = new DirectPosition2D(sourceCRS)
DirectPosition targetPt   = new DirectPosition2D(targetCRS)
DirectPosition previousPt = new DirectPosition2D(targetCRS)

// We will find Latitude and Longitude columns
// in the first iteration of the loop.
def latColumn  = 0
def lonColumn  = 0
def lineNumber = 0
def df = new DecimalFormat('###0.0')

file.eachLine { line ->
    line = line.trim();
    if (line.length() != 0 && line.charAt(0) != '#') {
        def p = line.split(",")
        if (lineNumber == 0) {
            latColumn = p.findIndexOf { it.toLowerCase() == 'latitude' }
            lonColumn = p.findIndexOf { it.toLowerCase() == 'longitude' }
            println("${line}, Easting, Northing, distanceFromPreviousLine[m]")
        }
        else {
            previousPt.setLocation(targetPt)
            sourcePt.setLocation(p[latColumn] as double, p[lonColumn] as double)
            targetPt = tr.transform(sourcePt, targetPt)
            def distance = Double.NaN
            if (lineNumber > 1) {
                distance = targetPt.distance(previousPt)
            }
            println("${line}, ${df.format(targetPt.x)}, ${df.format(targetPt.y)}, ${df.format(distance)}")
        }
        lineNumber++
    }
}
