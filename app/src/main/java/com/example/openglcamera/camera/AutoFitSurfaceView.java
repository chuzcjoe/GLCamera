package com.example.openglcamera.camera;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

/**
 * A SurfaceView that can be adjusted to a specified aspect ratio and
 * handles proper scaling to fit the aspect ratio while filling the screen.
 */
public class AutoFitSurfaceView extends SurfaceView {
    private static final String TAG = AutoFitSurfaceView.class.getSimpleName();
    private float aspectRatio = 0f;

    public AutoFitSurfaceView(Context context) {
        super(context, null);
    }

    public AutoFitSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public AutoFitSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Sets the aspect ratio for this view. The size of the view will be
     * measured based on the ratio calculated from the parameters.
     *
     * @param width  Camera resolution horizontal size
     * @param height Camera resolution vertical size
     */
    public void setAspectRatio(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Size cannot be negative");
        }
        aspectRatio = (float) width / height;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (aspectRatio == 0f) {
            setMeasuredDimension(width, height);
        } else {
            float actualRatio = (width > height) ? aspectRatio : (1f / aspectRatio);
            int newWidth;
            int newHeight;

            if (width < height * actualRatio) {
                newHeight = height;
                newWidth = Math.round(height * actualRatio);
            } else {
                newWidth = width;
                newHeight = Math.round(width / actualRatio);
            }

            Log.d(TAG, "Measured dimensions set: " + newWidth + " x " + newHeight);
            setMeasuredDimension(newWidth, newHeight);
        }
    }
} 