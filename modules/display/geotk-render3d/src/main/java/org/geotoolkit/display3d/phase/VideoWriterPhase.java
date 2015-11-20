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
package org.geotoolkit.display3d.phase;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.logging.Level;
import org.geotoolkit.display3d.Map3D;
import org.geotoolkit.display3d.utils.TransformRGBtoYUV420;

import org.jcodec.codecs.h264.H264Encoder;
import org.jcodec.codecs.h264.H264Utils;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.containers.mp4.Brand;
import org.jcodec.containers.mp4.MP4Packet;
import org.jcodec.containers.mp4.TrackType;
import org.jcodec.containers.mp4.muxer.FramesMP4MuxerTrack;
import org.jcodec.containers.mp4.muxer.MP4Muxer;
import org.jcodec.scale.AWTUtil;
import org.jcodec.scale.Transform;

/**
 * @author Thomas Rouby (Geomatys)
 */
public class VideoWriterPhase implements Phase {

    private final File videoFile;
    private final SeekableByteChannel ch;
    private final MP4Muxer muxer;
    private final FramesMP4MuxerTrack outTrack;
    private final ByteBuffer _out;
    private final H264Encoder encoder;
    private final Transform transform;

    private final int fps;

    private final List<ByteBuffer> spsList = new ArrayList<>();
    private final List<ByteBuffer> ppsList = new ArrayList<>();

    private Picture toEncode;
    private int frameNo;

    private boolean record = false;

//    private long videoStart = 0l;

    private Map3D map;

    private final int width;
    private final int height;

    public VideoWriterPhase(File fileToWrite, int width, int height, int fps) throws IOException {
        final File videoDirectory = fileToWrite.getParentFile();
        if (!videoDirectory.exists()){
            videoDirectory.mkdirs();
        }
        this.videoFile = fileToWrite;

        this.width = width;
        this.height = height;
        this.fps = fps;

        this.ch = NIOUtils.writableFileChannel(fileToWrite);

        this.transform = new TransformRGBtoYUV420(0,0);

        muxer = new MP4Muxer(ch, Brand.MP4);

        this.outTrack = muxer.addTrackForCompressed(TrackType.VIDEO, fps); // second is FPS
        this.encoder = new H264Encoder();
        this._out = ByteBuffer.allocate(width * height * 6);
    }

    public int getFPS(){
        return fps;
    }

    public int getFrameNo(){
        return frameNo;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    @Override
    public void setMap(Map3D map) {
        this.map = map;
    }

    @Override
    public Map3D getMap() {
        return this.map;
    }

    @Override
    public void update(GL gl) {
        if (record){
//              2.0.2 method not valid in 2.0-rc11
            AWTGLReadBufferUtil glReadBuffer = new AWTGLReadBufferUtil(gl.getGLProfile(), false);
            BufferedImage bufferedImage = glReadBuffer.readPixelsToBufferedImage(gl, true);

//              2.0-rc11 method
//            final BufferedImage bufferedImage = Screenshot.readToBufferedImage(this.getMap().getCamera().getWidth(), this.getMap().getCamera().getHeight(), false);

            try {
                this.encodeImage(convertToType(bufferedImage, BufferedImage.TYPE_3BYTE_BGR));
            } catch (Exception ex) {
                stopRecord();
                if (this.getMap() != null) {
                    this.getMap().getMonitor().exceptionOccured(ex, Level.WARNING);
                } else {
                    System.out.println(ex.getMessage());
                    ex.printStackTrace(System.err);
                }
            }
        }
    }

    public void encodeImage(BufferedImage bi) throws IOException {
        if (toEncode == null) {
            toEncode = Picture.create(getWidth(), getHeight(), ColorSpace.YUV420);
        }

        // Perform conversion
        for (int i = 0; i < 3; i++)
            Arrays.fill(toEncode.getData()[i], 0);
        transform.transform(AWTUtil.fromBufferedImage(bi), toEncode);

        // Encode image into H.264 frame, the result is stored in '_out' buffer and return
        _out.clear();
        ByteBuffer result = encoder.encodeFrame(_out, toEncode);

        // Based on the frame above form correct MP4 packet
        spsList.clear();
        ppsList.clear();
        H264Utils.encodeMOVPacket(result, spsList, ppsList);

        // Add packet to video track
        outTrack.addFrame(new MP4Packet(result, frameNo, fps, 1, frameNo, true, null, frameNo, 0));

        frameNo++;
    }

    public void saveVideo() throws IOException {
        // Push saved SPS/PPS to a special storage in MP4
        outTrack.addSampleEntry(H264Utils.createMOVSampleEntry(spsList, ppsList));

        // Write MP4 header and finalize recording
        muxer.writeHeader();
        NIOUtils.closeQuietly(ch);
    }

    public File getVideoFile() {
        return this.videoFile;
    }

    private BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
        BufferedImage image;
        // if the source image is already the target type, return the source image
        if (sourceImage.getType() == targetType) {
            image = sourceImage;
        }
        // otherwise create a new image of the target type and draw the new image
        else {
            image = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), targetType);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }
        return image;
    }

    public void startRecord(){
        record = true;
    }

    public void stopRecord(){
        record = false;
    }
}
