/*
 * 20-6-23 下午4:40
 * 2020
 * Administrator
 */

package com.dueeeke.videoplayer.render;

import android.content.Context;

public class TextureRenderViewFactory extends RenderViewFactory {

    public static TextureRenderViewFactory create() {
        return new TextureRenderViewFactory();
    }

    @Override
    public IRenderView createRenderView(Context context) {
        return new TextureRenderView(context);
    }
}
