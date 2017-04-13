package com.example.vrushank.multibhashi.Interfaces;

import com.example.vrushank.multibhashi.Fragment.AudioFragment;
import com.example.vrushank.multibhashi.Model.Model;

/**
 * Created by vrushank on 14/4/17.
 */

public interface ItemClickDownload {
    void loadSong(Model nextItem);
    void playSong(AudioFragment fragment);
}
