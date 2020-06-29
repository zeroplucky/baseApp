/*
 * 20-6-28 上午10:39
 * 2020
 * Administrator
 */

package com.dueeeke.videoplayer.exo;

import android.content.Context;

import com.dueeeke.videoplayer.player.PlayerFactory;

public class ExoMediaPlayerFactory extends PlayerFactory<ExoMediaPlayer> {

    public static ExoMediaPlayerFactory create() {
        return new ExoMediaPlayerFactory();
    }

    @Override
    public ExoMediaPlayer createPlayer(Context context) {
        return new ExoMediaPlayer(context);
    }
}
