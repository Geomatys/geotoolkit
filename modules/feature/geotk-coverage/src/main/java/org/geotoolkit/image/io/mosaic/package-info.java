/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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

/**
 * Support for reading and creating mosaics of geographically referenced images.
 * This package uses a subclass of the standard Java {@link javax.imageio.ImageReader}
 * which tracks the extent and resolution of the component images.
 * <p>
 * The {@link org.geotoolkit.image.io.mosaic.MosaicImageReader} class can generate visual
 * representations of an underlying mosaic for different extents and resolutions based
 * on the parameter values in the {@link javax.imageio.ImageReadParam} instance passed
 * to the {@code read(...)} method. Each image in a mosaic will be a stand alone image in
 * main storage (<it>e.g.</it> disk) and will be represented to the {@code MosaicImageReader}
 * as a {@link org.geotoolkit.image.io.mosaic.Tile} instance which carries the spatial metadata
 * for the image, including its resolution and extent. In a typical mosaic the tiles will
 * be laid out on a regular grid; in a typical pyramid the tiles will form several mosaic
 * layers at different resolutions. The {@link org.geotoolkit.image.io.mosaic.TileManager}
 * tracks the {@code Tile} instances for the {@code MosaicImageReader}. For the user, the
 * {@code MosaicImageReader} can be used as if it were a standard
 * {@link javax.imageio.ImageReader} backed by a single, huge image.
 * <p>
 * This package can work directly with an existing image set or mosaic by creating
 * a {@code Tile} instance to define the geographic metadata for each image. The
 * package also provides the {@link org.geotoolkit.image.io.mosaic.MosaicBuilder} which
 * can generate a new mosaic from an existing one simply by specifying the properties
 * of the desired mosaic. For example, a user can start with a small set of large images,
 * define the geographic extent of each image, and then use the {@code MosaicBuilder} to
 * build a mosaic composed of many, small images with which to work more efficiently.
 * <p>
 * All of the images referenced by a single {@code MosaicImageReader} must exist on the same
 * 'grid' which is to say they must share the same axes and have pixel sizes which are
 * integer multiple of the smallest pixel size. The images do not necessarily need to cover a
 * contiguous area nor do the mosaics need to be geo-referenced when they are originally created.
 *
 *
 * {@section To define a mosaic from existing set of images}
 *
 * Users must first specify the layout of their source images. This step is required
 * whether or not the users wish to use an existing mosaic "as is" or want to generate
 * a new mosaic. Once defined, the mosaic can be used directly to generate images by
 * creating a {@code MosaicImageReader}. Alternatively, the mosaic can be used by
 * {@code MosaicBuilder} to generate a new mosaic.
 *
 * <ol>
 *   <li><p>Create a collection of {@link org.geotoolkit.image.io.mosaic.Tile} objects where
 *       each {@code Tile} instance describes an existing image. {@code Tile} instances
 *       contain only metadata about the tiles, not the actual pixel data. Two approaches
 *       exist to define the location of a given {@code Tile} instance relative to other
 *       tiles in the mosaic: the relative location can be described in pixel units by
 *       passing {@code Point} or {@code Rectangle} arguments to the constructor or the
 *       information can be calculated from the actual geographic coordinates of the image.
 *       In the latter case, an <cite>affine transform</cite>, which defines the conversion
 *       from grid (pixel) coordinates to geographic coordinates, may be passed explicitly to
 *       a constructor. Alternatively, this transform can be inferred automatically by the
 *       {@linkplain org.geotoolkit.image.io.mosaic.Tile#Tile(javax.imageio.spi.ImageReaderSpi,
 *       java.io.File, int) a constructor} which uses a 'world file' (TFW) which accompanies
 *       the image to calculate the transform.</p></li>
 *
 *   <li><p>Create a {@link org.geotoolkit.image.io.mosaic.TileManager} from the collection of tiles.
 *       The tile manager is {@linkplain org.geotoolkit.image.io.mosaic.TileManagerFactory#create created
 *       using a factory}. The factory will infer the mosaic layout (location of tiles relative to each
 *       other, subsampling relative to the tiles having the smallest pixels, <i>etc.</i>) from
 *       the affine transform defined for each tile. If the tiles appear to be distributed on a regular
 *       grid, then the created {@code TileManager} instance will store that information in a compact
 *       way that does not require the retention of every {@code Tile} object.</p></li>
 * </ol>
 *
 * The following example creates a {@code TileManager} for an existing set of images. In this
 * example the images are TIFF files where each image file is accompanied by a file of the same
 * name but with the ".tfw" extension (its <cite>World File</cite>). The {@code Tile} constructor
 * used here automatically looks for the TFW file and builds the required {@code AffineTransform}.
 *
 * {@preformat java
 *     File[] sourceImages = directory.listFiles(new DefaultFileFilter("*.tiff"));
 *     List<Tile> tiles = new ArrayList<Tile>(sourceImages.length);
 *     for (File file : sourceImages) {
 *         tiles.add(new Tile(null, sourceImages, 0));
 *     }
 *     TileManager[] originalMosaic = TileManagerFactory.DEFAULT.create(tiles);
 * }
 *
 * {@note
 *   <ul>
 *     <li>The resulting <code>TileManager</code> array should have length 1 if all the images could
 *         be defined in the same grid. Otherwise be prepared to handle multiple mosaics.</li>
 *     <li>In current implementation, make sure that you get an instance of <code>GridTileManager</code>.
 *         The other instance of <code>TileManager</code>, <code>TreeTileManager</code>, is more
 *         generic but still experimental.</li>
 *     <li>You can serialize the <code>TileManager</code> instance in order to reload it quickly in
 *         a subsequent run of your application.</li>
 *   </ul>
 * }
 *
 *
 * {@section To generate a new mosaic from an existing one}
 *
 * Users may want to generate a new mosaic from an existing set of images, typically either
 * to have small regular tiles or to create a mosaic with several layers of images having
 * different resolutions. (The latter is often called a "pyramid" in the geographic community.)
 * <p>
 * The {@link org.geotoolkit.image.io.mosaic.MosaicBuilder} class makes building a new mosaic easy:
 *
 * {@preformat java
 *     MosaicBuilder builder = new MosaicBuilder();
 *     builder.setTileDirectory(new File("output")); // The output directory.
 *     builder.setTileSize(new Dimension(256, 256)); // The size of the output tiles.
 *     builder.setSubsamplings(1, 2, 3, 4);          // Defines the layers.
 *     TileManager newMosaic = builder.createTileManager(originalMosaic, TileWritingPolicy.WRITE_NEWS_NONEMPTY);
 * }
 *
 * Because the cost of building a mosaic can be substantial, it is often worth while to save
 * the {@code TileManager} data structure in some way. The mosaic will be used every time an
 * image needs to be read from the mosaic (see next section) while the steps performed up to this
 * point need to be executed only once. An easy way to save the mosaic information is simply to
 * {@linkplain java.io.ObjectOutput#writeObject serialize} the {@code TileManager} instance.
 * <p>
 * <blockquote><table border="1" cellpadding="6" bgcolor="paleturquoise"><tr><td>
 * <b>Tip:</b> for an easy way to generate a mosaic using a graphical user interface, see the
 * <a href="http://www.geotoolkit.org/modules/display/geotk-wizards-swing/MosaicWizard.html">Image
 * Mosaic Wizard</a>.
 * </td></tr></table></blockquote>
 *
 *
 * {@section To read images from the mosaic}
 *
 * To generate images, users need to perform the following steps:
 *
 * <ol>
 *   <li><p>Create a new instance of {@link org.geotoolkit.image.io.mosaic.MosaicImageReader}.
 *       Instantiation can be done directly using the public constructors, or an instance can be
 *       {@linkplain javax.imageio.ImageIO#getImageReadersByFormatName provided by the Java Image
 *       I/O framework} when the users request for the {@code "mosaic"} format.</p></li>
 *
 *   <li><p>{@linkplain org.geotoolkit.image.io.mosaic.MosaicImageReader#setInput Set the input} of
 *       the {@code MosaicImageReader} to use the {@code TileManager} instance created in the
 *       previous section.</p></li>
 *
 *   <li><p>Obtain a new instance of {@link org.geotoolkit.image.io.mosaic.MosaicImageReadParam}.
 *       Instantiation can be done directly using the public constructor, or an instance can be
 *       {@link org.geotoolkit.image.io.mosaic.MosaicImageReader#getDefaultReadParam provided by
 *       the reader}.</p></li>
 *
 *   <li><p>Configure the parameters to reference the data of interest. Methods of special interest
 *       are {@link org.geotoolkit.image.io.mosaic.MosaicImageReadParam#setSubsamplingChangeAllowed
 *       setSubsamplingChangeAllowed} (<strong>non-standard but strongly recommended</strong>),
 *       {@link org.geotoolkit.image.io.mosaic.MosaicImageReadParam#setSourceSubsampling setSourceSubsampling}
 *       and {@link org.geotoolkit.image.io.mosaic.MosaicImageReadParam#setSourceRegion setSourceRegion}.</p></li>
 *
 *   <li><p>Invoke {@link org.geotoolkit.image.io.mosaic.MosaicImageReader#read MosaicImageReader.read}
 *       with the parameters created in the previous step.</p></li>
 * </ol>
 *
 * Users who wish to generate more images from the same mosaic can repeat only steps&nbsp;4
 * and&nbsp;5. The steps&nbsp;1 to&nbsp;3 (and <cite>a fortiori</cite> the previous section
 * which created the {@code TileManager}) need to be done only once.
 * <p>
 * The example below generates a single image from the mosaic created in the previous section.
 * Note that the source region is given in pixel coordinates.
 *
 * {@preformat java
 *     MosaicImageReader reader = new MosaicImageReader();
 *     reader.setInput(newMosaic);
 *     MosaicImageReadParam param = reader.getDefaultReadParam();
 *     param.setSubsamplingChangeAllowed(true); // Strongly recommended.
 *     param.setSourceRegion(new Rectangle(2000, 2000, 800, 600));
 *     param.setSourceSubsampling(2, 2, 0, 0);
 *     RenderedImage image = reader.read(0, param);
 *
 *     // Because of setSubsamplingChangeAllowed(true), the subsampling actually used
 *     // may be different than the one we asked for. To get the subsampling which was
 *     // actually used we can do:
 *     int sx = param.getSourceXSubsampling();
 *     int sy = param.getSourceYSubsampling();
 *     // Adjust gridToCRS here using (sx,sy).
 * }
 *
 *
 * {@section To convert a source region from "real world" to pixel coordinates}
 *
 * The {@code ImageReader} API is defined only in terms of pixel coordinates. If the region to
 * read is defined in "real world" coordinates, then it must be converted to pixel coordinates
 * before being given to {@code MosaicImageReadParam}. This conversion can be done by obtaining
 * an {@link java.awt.geom.AffineTransform} from the
 * {@link org.geotoolkit.image.io.mosaic.TileManager#getGridGeometry()} method and using it as
 * shown here:
 *
 * {@preformat java
 *     Rectangle2D realWorldRegion = ...;
 *     Rectangle sourceRegion = new Rectangle();
 *     AffineTransform tr = newMosaic.getImageGeometry().getGridToCRS();
 *     tr = tr.createInverse();
 *     XAffineTransform.transform(tr, realWorldRegion, sourceRegion);
 * }
 *
 * The above can only be used if the {@code TileManager} has been created in one of the
 * following ways:
 * <p>
 * <ul>
 *   <li>From a set of {@code Tile} objects where each tile was given an affine transform
 *       at construction time, either directly or indirectly through a TFW file.</li>
 *   <li>From a {@code MosaicBuilder} in which the
 *       {@linkplain org.geotoolkit.image.io.mosaic.MosaicBuilder#setGridToCRS grid to CRS
 *       transform has been specified}.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 2.5
 * @module
 */
package org.geotoolkit.image.io.mosaic;
