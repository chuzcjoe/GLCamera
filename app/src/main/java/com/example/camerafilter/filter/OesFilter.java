package com.example.camerafilter.filter;


import android.opengl.GLES11Ext;

import static android.opengl.GLES30.*;

import com.example.camerafilter.R;
import com.example.camerafilter.utils.GLUtil;

/**
 * Created by joe.chu on 1/21/24
 *
 * @author joe.chu@bytedance.com
 */
public class OesFilter extends BaseFilter {
    public OesFilter() {
        super();
    }

    @Override
    public int initProgram() {
        return GLUtil.createAndLinkProgram(R.raw.texture_vertex_shader, R.raw.texture_oes_fragtment_shader);
    }

    @Override
    public void bindTexture() {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, getTextureId()[0]);
        glUniform1i(hTexture, 0);
    }
}
