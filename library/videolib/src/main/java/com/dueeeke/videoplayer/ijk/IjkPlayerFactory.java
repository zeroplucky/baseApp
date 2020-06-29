/*
 * 20-6-23 下午4:47
 * 2020
 * Administrator
 */

package com.dueeeke.videoplayer.ijk;

import android.content.Context;

import com.dueeeke.videoplayer.player.PlayerFactory;

public class IjkPlayerFactory extends PlayerFactory<IjkPlayer> {

    public static IjkPlayerFactory create() {
        return new IjkPlayerFactory();
    }

    @Override
    public IjkPlayer createPlayer(Context context) {
        return new IjkPlayer(context);
    }
}
