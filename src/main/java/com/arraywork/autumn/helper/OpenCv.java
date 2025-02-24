package com.arraywork.autumn.helper;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.springframework.boot.system.ApplicationHome;

import com.arraywork.autumn.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Open Computer Vision Utils
 * (Depends on org.opencv)
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/07/10
 */
@Slf4j
public class OpenCv {

    // Default image output quality is 75%.
    private static final int DEFAULT_QUALITY = 75;

    /**
     * 根据路径加载OpenCv库（相对路径则从项目根目录查找）
     * Load OpenCV library based on the path
     * (for relative paths, it will look from the project root directory).
     * Windows -> /path/to/opencv_java4100.dll
     * Linux -> /path/to/libopencv_java4100.so
     *
     * @param libPath OpenCv library path
     */
    public static void loadLibrary(String libPath) {
        if (!Path.of(libPath).isAbsolute()) {
            String appHome = new ApplicationHome().getDir().toString();
            libPath = Path.of(appHome, libPath).toString();
        }
        System.load(libPath);
    }

    /**
     * Get the width and height of the image in pixels.
     *
     * @param input
     * @return { width, height }
     */
    public static int[] getShape(String input) {
        Mat mat = Imgcodecs.imread(input);
        int[] shape = { mat.cols(), mat.rows() };
        mat.release();
        return shape;
    }

    /**
     * Capture video and take a thumbnail with the same aspect ratio.
     * (Chinese paths are not supported on Windows.)
     *
     * @param input    Input file path
     * @param output   Output file path
     * @param longSide Long side value
     */
    public static void captureVideo(String input, String output, int longSide) {
        checkPath(input, output);
        VideoCapture capture = null;
        Mat mat = null;
        try {
            capture = new VideoCapture(input);
            Assert.isTrue(capture.isOpened(), "Cannot open the video: " + input);

            // 为避免前几帧的空白或空黑画面、同时避免超过总帧数，此处自动截取整个视频的中间帧
            // 1. 在windows上，如果使用CAP_PROP_POS_FRAMES或CAP_PROP_POS_MSEC设置精确的中间帧会非常慢
            // CAP_PROP_POS_AVI_RATIO采用相对的、粗略的位置（取值0.0-1.0），速度非常快
            // capture.set(Videoio.CAP_PROP_POS_AVI_RATIO, 0.5);
            // 2. 但在Linux上，CAP_PROP_POS_AVI_RATIO参数不起作用，因此改用CAP_PROP_POS_FRAMES
            // 速度也非常快。可能不同操作系统的opencv库寻帧的方式不同。
            int frames = (int) capture.get(Videoio.CAP_PROP_FRAME_COUNT);
            capture.set(Videoio.CAP_PROP_POS_FRAMES, frames / 2);

            mat = new Mat();
            Assert.isTrue(capture.read(mat), "Cannot read the video: " + input);

            Size size = calcSize(mat.width(), mat.height(), longSide);
            Imgproc.resize(mat, mat, size, 0, 0, Imgproc.INTER_AREA);
            boolean success = Imgcodecs.imwrite(output, mat);
            Assert.isTrue(success, "Cannot write image to output: " + output);
        } catch (Exception e) {
            log.error("Capture video failed: {}", input, e);
            throw new RuntimeException("Capture video failed: " + input);
        } finally {
            if (capture != null) capture.release();
            if (mat != null) mat.release();
        }
    }

    public static void resizeImage(String input, String output, int longSide) {
        resizeImage(input, output, longSide, DEFAULT_QUALITY);
    }

    public static void resizeImage(String input, String output, int longSide, int quality) {
        if (isWindows()) resizeImageUnicode(input, output, longSide, quality);
        else resizeImageNative(input, output, longSide, quality);
    }

    /**
     * Resize the image to the same aspect ratio (reading file with OpenCv)
     *
     * @param input    Input file path
     * @param output   Output file path
     * @param longSide Long side value
     * @param quality  Output image quality（0-100）
     */
    private static void resizeImageNative(String input, String output, int longSide, int quality) {
        checkPath(input, output);
        Mat src = null, dist = null;
        try {
            src = Imgcodecs.imread(input);
            dist = new Mat();
            Size size = calcSize(src.width(), src.height(), longSide);
            Imgproc.resize(src, dist, size, 0, 0, Imgproc.INTER_AREA);
            MatOfInt params = new MatOfInt(Imgcodecs.IMWRITE_JPEG_QUALITY, quality);

            boolean success = Imgcodecs.imwrite(output, dist, params);
            Assert.isTrue(success, "Cannot write image to output: " + output);
        } catch (Exception e) {
            log.error("Resize image failed: {}", input, e);
            throw new RuntimeException("Resize image failed: " + input);
        } finally {
            if (src != null) src.release();
            if (dist != null) dist.release();
        }
    }

    /**
     * Resize the image to the same aspect ratio (reading file with Java)
     * if the input or output path contains Unicode characters, such as Chinese paths
     * not being supported on Windows. It's slightly slower than using native OpenCV
     *
     * @param input    Input file path
     * @param output   Output file path
     * @param longSide Long side value
     * @param quality  Output image quality（0-100）
     */
    private static void resizeImageUnicode(String input, String output, int longSide, int quality) {
        checkPath(input, output);
        Mat src = null, dist = null;
        try {
            byte[] bytes = Files.readAllBytes(Path.of(input));
            src = Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.IMREAD_UNCHANGED);

            dist = new Mat();
            Size size = calcSize(src.width(), src.height(), longSide);
            Imgproc.resize(src, dist, size, 0, 0, Imgproc.INTER_AREA);
            MatOfInt params = new MatOfInt(Imgcodecs.IMWRITE_JPEG_QUALITY, quality);

            MatOfByte buff = new MatOfByte();
            Imgcodecs.imencode(".jpg", dist, buff, params);
            Files.write(Path.of(output), buff.toArray());
        } catch (Exception e) {
            log.error("Resize image failed: {}", input, e);
            throw new RuntimeException("Resize image failed: " + input);
        } finally {
            if (src != null) src.release();
            if (dist != null) dist.release();
        }
    }

    /** Verify if the input file exists and automatically create the output directory. */
    private static void checkPath(String input, String output) {
        File file = new File(input);
        Assert.isTrue(file.exists(), "Input file not found: " + file);
        File dir = new File(output).getParentFile();
        if (dir != null && !dir.exists()) dir.mkdirs();
    }

    /** Calculate the dimensions after scaling. */
    private static Size calcSize(int width, int height, int longSide) {
        if (longSide > 0) {
            double ratio = (double) width / height;

            if (width > height) {
                width = longSide;
                height = (int) (longSide / ratio);
            } else {
                height = longSide;
                width = (int) (longSide * ratio);
            }
        }
        return new Size(width, height);
    }

    /** Determine if the operating system is Windows. */
    private static boolean isWindows() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("windows");
    }

}