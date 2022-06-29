package com.genymobile.scrcpy;

import android.graphics.Rect;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.Arrays;

public final class ScreenInfo {
    private static String TAG = "scrcpy";
    /**
     * Device (physical) size, possibly cropped
     */
    private final Rect contentRect; // device size, possibly cropped

    /**
     * Video size, possibly smaller than the device size, already taking the device rotation and crop into account.
     * <p>
     * However, it does not include the locked video orientation.
     */
    private final Size unlockedVideoSize;

    /**
     * Device rotation, related to the natural device orientation (0, 1, 2 or 3)
     */
    private final int deviceRotation;

    /**
     * The locked video orientation (-1: disabled, 0: normal, 1: 90° CCW, 2: 180°, 3: 90° CW)
     */
    private final int lockedVideoOrientation;

    public ScreenInfo(Rect contentRect, Size unlockedVideoSize, int deviceRotation, int lockedVideoOrientation) {
        this.contentRect = contentRect;
        this.unlockedVideoSize = unlockedVideoSize;
        this.deviceRotation = deviceRotation;
        this.lockedVideoOrientation = lockedVideoOrientation;
    }

    public Rect getContentRect() {
        return contentRect;
    }

    /**
     * Return the video size as if locked video orientation was not set.
     *
     * @return the unlocked video size
     */
    public Size getUnlockedVideoSize() {
        return unlockedVideoSize;
    }

    /**
     * Return the actual video size if locked video orientation is set.
     *
     * @return the actual video size
     */
    public Size getVideoSize() {
        if (getVideoRotation() % 2 == 0) {
            return unlockedVideoSize;
        }

        return unlockedVideoSize.rotate();
    }

    public int getDeviceRotation() {
        return deviceRotation;
    }

    public ScreenInfo withDeviceRotation(int newDeviceRotation) {
        if (newDeviceRotation == deviceRotation) {
            return this;
        }
        // true if changed between portrait and landscape
        boolean orientationChanged = (deviceRotation + newDeviceRotation) % 2 != 0;
        Rect newContentRect;
        Size newUnlockedVideoSize;
        if (orientationChanged) {
            newContentRect = flipRect(contentRect);
            newUnlockedVideoSize = unlockedVideoSize.rotate();
        } else {
            newContentRect = contentRect;
            newUnlockedVideoSize = unlockedVideoSize;
        }
        return new ScreenInfo(newContentRect, newUnlockedVideoSize, newDeviceRotation, lockedVideoOrientation);
    }

    public static ScreenInfo computeScreenInfo(DisplayInfo displayInfo, VideoSettings videoSettings) {
        int lockedVideoOrientation = videoSettings.getLockedVideoOrientation();
        int rotation = displayInfo.getRotation();

        if (lockedVideoOrientation == Device.LOCK_VIDEO_ORIENTATION_INITIAL) {
            // The user requested to lock the video orientation to the current orientation
            lockedVideoOrientation = rotation;
        }

        Size deviceSize = displayInfo.getSize();
        Rect contentRect = new Rect(0, 0, deviceSize.getWidth(), deviceSize.getHeight());

        Size videoSize = new Size(deviceSize.getWidth(), deviceSize.getHeight());
        return new ScreenInfo(contentRect, videoSize, rotation, lockedVideoOrientation);
    }


    private static Rect flipRect(Rect crop) {
        return new Rect(crop.top, crop.left, crop.bottom, crop.right);
    }

    /**
     * Return the rotation to apply to the device rotation to get the requested locked video orientation
     *
     * @return the rotation offset
     */
    public int getVideoRotation() {
        if (lockedVideoOrientation == -1) {
            // no offset
            return 0;
        }
        return (deviceRotation + 4 - lockedVideoOrientation) % 4;
    }

    /**
     * Return the rotation to apply to the requested locked video orientation to get the device rotation
     *
     * @return the (reverse) rotation offset
     */
    public int getReverseVideoRotation() {
        if (lockedVideoOrientation == -1) {
            // no offset
            return 0;
        }
        return (lockedVideoOrientation + 4 - deviceRotation) % 4;
    }

    public byte[] toByteArray() {
        ByteBuffer temp = ByteBuffer.allocate(6 * 4 + 1);
        temp.putInt(contentRect.left);
        temp.putInt(contentRect.top);
        temp.putInt(contentRect.right);
        temp.putInt(contentRect.bottom);
        temp.putInt(unlockedVideoSize.getWidth());
        temp.putInt(unlockedVideoSize.getHeight());
        temp.put((byte) getVideoRotation());
        return temp.array();
    }
}
