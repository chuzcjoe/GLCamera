package com.example.camerafilter.views;

/**
 * Created by joe.chu on 1/21/24
 *
 * @author joe.chu@bytedance.com
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

/**
 * Created by joe.chu on 1/21/24
 *
 * @author joe.chu@bytedance.com
 */

public class AutoFitTextureView extends TextureView {

    private int ratioW = 0;
    private int ratioH = 0;

    public AutoFitTextureView(Context context) {
        super(context);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置宽高比
     * @param width
     * @param height
     */
    public void setAspectRation(int width, int height){
        if (width < 0 || height < 0){
            throw new IllegalArgumentException("width or height can not be negative.");
        }
        ratioW = width;
        ratioH = height;
        //请求重新布局
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (0 == ratioW || 0 == ratioH){
            //未设定宽高比，使用预览窗口默认宽高
            setMeasuredDimension(width, height);
        }else {
            //设定宽高比，调整预览窗口大小（调整后窗口大小不超过默认值）
            if (width < height * ratioW / ratioH){
                setMeasuredDimension(width, width * ratioH / ratioW);
            }else {
                setMeasuredDimension(height * ratioW / ratioH, height);
            }
        }

    }
}

