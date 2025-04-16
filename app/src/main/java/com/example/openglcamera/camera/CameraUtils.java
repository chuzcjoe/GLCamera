package com.example.openglcamera.camera;

import android.graphics.Point;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.util.Log;
import android.util.Size;
import android.view.Display;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CameraUtils {

    /** Helper class used to pre-compute shortest and longest sides of a [Size] */
    public static class SmartSize {
        public final Size size;
        public final int longSide;
        public final int shortSide;

        public SmartSize(int width, int height) {
            this.size = new Size(width, height);
            this.longSide = Math.max(width, height);
            this.shortSide = Math.min(width, height);
        }

        @Override
        public String toString() {
            return "SmartSize(" + longSide + "x" + shortSide + ")";
        }
    }

    /** Standard High Definition size for pictures and video */
    public static final SmartSize SIZE_1080P = new SmartSize(1920, 1080);

    /** Returns a SmartSize object for the given Display */
    public static SmartSize getDisplaySmartSize(Display display) {
        Point outPoint = new Point();
        display.getRealSize(outPoint);
        return new SmartSize(outPoint.x, outPoint.y);
    }

    /**
     * Returns the largest available PREVIEW size smaller than or equal to screen/1080p size.
     */
    public static <T> Size getPreviewOutputSize(
            Display display,
            CameraCharacteristics characteristics,
            Class<T> targetClass,
            Integer format // nullable
    ) {
        SmartSize screenSize = getDisplaySmartSize(display);
        boolean hdScreen = screenSize.longSide >= SIZE_1080P.longSide || screenSize.shortSide >= SIZE_1080P.shortSide;
        SmartSize maxSize = hdScreen ? SIZE_1080P : screenSize;

        StreamConfigurationMap config = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        if (config == null) {
            throw new IllegalArgumentException("Cannot get StreamConfigurationMap from camera characteristics.");
        }

        Size[] allSizes;
        if (format == null) {
            if (!StreamConfigurationMap.isOutputSupportedFor(targetClass)) {
                throw new AssertionError("Output not supported for class: " + targetClass);
            }
            allSizes = config.getOutputSizes(targetClass);
        } else {
            if (!config.isOutputSupportedFor(format)) {
                throw new AssertionError("Output format not supported: " + format);
            }
            allSizes = config.getOutputSizes(format);
        }

        if (allSizes == null || allSizes.length == 0) {
            throw new IllegalStateException("No output sizes available.");
        }

        List<SmartSize> validSizes = new ArrayList<>();
        for (Size size : allSizes) {
            validSizes.add(new SmartSize(size.getWidth(), size.getHeight()));
        }

        // Sort by area descending
        Collections.sort(validSizes, new Comparator<SmartSize>() {
            @Override
            public int compare(SmartSize o1, SmartSize o2) {
                int area1 = o1.size.getWidth() * o1.size.getHeight();
                int area2 = o2.size.getWidth() * o2.size.getHeight();
                return Integer.compare(area2, area1); // descending
            }
        });

        for (SmartSize candidate : validSizes) {
            if (candidate.longSide <= maxSize.longSide && candidate.shortSide <= maxSize.shortSide) {
                return candidate.size;
            }
        }

        // fallback: return the first available size
        return validSizes.get(0).size;
    }
}


