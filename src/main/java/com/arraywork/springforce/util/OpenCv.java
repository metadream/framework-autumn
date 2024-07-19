package com.arraywork.springforce.util;

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

import lombok.extern.slf4j.Slf4j;

/**
 * Open Computer Vision Utils
 *
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/07/10
 */
@Slf4j
public class OpenCv {

    // 输出图像默认质量75%
    private static final int DEFAULT_QUALITY = 75;

    /**
     * 根据路径加载OpenCv库（相对路径则从项目根目录查找）
     * Windows -> /path/to/opencv_java4100.dll
     * Linux -> /path/to/libopencv_java4100.so
     *
     * @param libPath OpenCv库路径
     */
    public static void loadLibrary(String libPath) {
        if (!Path.of(libPath).isAbsolute()) {
            String appHome = new ApplicationHome().getDir().toString();
            libPath = Path.of(appHome, libPath).toString();
        }
        System.load(libPath);
    }

    /**
     * 捕获视频并截取相同纵横比的缩略图
     * Windows下不支持中文路径
     *
     * @param input    输入文件路径
     * @param output   输出文件路径
     * @param longSide 长边值
     */
    public static void captureVideo(String input, String output, int longSide) {
        checkPath(input, output);
        VideoCapture capture = null;
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

            Mat mat = new Mat();
            Assert.isTrue(capture.read(mat), "Cannot read the video: " + input);

            Size size = calcSize(mat.width(), mat.height(), longSide);
            Imgproc.resize(mat, mat, size, 0, 0, Imgproc.INTER_AREA);
            boolean success = Imgcodecs.imwrite(output, mat);
            Assert.isTrue(success, "Cannot write image to output: " + output);
        } catch (Exception e) {
            log.error("Capture video failed: {}", input, e);
            throw new RuntimeException("Capture video failed: " + input);
        } finally {
            if (capture != null) {
                capture.release();
            }
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
     * 按相同纵横比缩放图片
     *
     * @param input    输入文件路径
     * @param output   输出文件路径
     * @param longSide 长边值
     * @param quality  质量（0-100）
     */
    private static void resizeImageNative(String input, String output, int longSide, int quality) {
        checkPath(input, output);
        try {
            Mat src = Imgcodecs.imread(input);
            Mat dist = new Mat();
            Size size = calcSize(src.width(), src.height(), longSide);
            Imgproc.resize(src, dist, size, 0, 0, Imgproc.INTER_AREA);
            MatOfInt params = new MatOfInt(Imgcodecs.IMWRITE_JPEG_QUALITY, quality);

            boolean success = Imgcodecs.imwrite(output, dist, params);
            Assert.isTrue(success, "Cannot write image to output: " + output);
        } catch (Exception e) {
            log.error("Resize image failed: {}", input, e);
            throw new RuntimeException("Resize image failed: " + input);
        }
    }

    /**
     * 缩放图片（输入或输出路径中含有Unicode字符，例如在Windows中不支持中文路径）
     * 用Java读取文件的速度比原生opencv略慢
     *
     * @param input    输入文件路径
     * @param output   输出文件路径
     * @param longSide 长边值
     * @param quality  输出质量
     */
    private static void resizeImageUnicode(String input, String output, int longSide, int quality) {
        checkPath(input, output);
        try {
            byte[] bytes = Files.readAllBytes(Path.of(input));
            Mat src = Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.IMREAD_UNCHANGED);

            Mat dist = new Mat();
            Size size = calcSize(src.width(), src.height(), longSide);
            Imgproc.resize(src, dist, size, 0, 0, Imgproc.INTER_AREA);
            MatOfInt params = new MatOfInt(Imgcodecs.IMWRITE_JPEG_QUALITY, quality);

            MatOfByte buff = new MatOfByte();
            Imgcodecs.imencode(".jpg", dist, buff, params);
            Files.write(Path.of(output), buff.toArray());
        } catch (Exception e) {
            log.error("Resize image failed: {}", input, e);
            throw new RuntimeException("Resize image failed: " + input);
        }
    }

    // 校验输入文件是否存在，自动创建输出目录
    private static void checkPath(String input, String output) {
        File file = new File(input);
        Assert.isTrue(file.exists(), "Input file not found: " + file);
        File dir = new File(output).getParentFile();
        if (dir != null && !dir.exists()) dir.mkdirs();
    }

    // 计算缩放后的尺寸
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

    // 判断是否Windows操作系统
    private static boolean isWindows() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("windows");
    }

}