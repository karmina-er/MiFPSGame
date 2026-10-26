package com.miempresa.mifpsgame.game;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;

public class AudioManager {
    public static AudioNode createAmbientSound(
            AssetManager assets, String path) {
        AudioNode audio = new AudioNode(assets, path,
                AudioData.DataType.Stream);
        audio.setLooping(true);
        audio.setVolume(0.5f);
        audio.setPositional(false); // sonido ambiental = no posicional
        return audio;
    }

    public static AudioNode createSFX(
            AssetManager assets, String path) {
        AudioNode audio = new AudioNode(assets, path,
                AudioData.DataType.Buffer); // en memoria para efectos
        audio.setLooping(false);
        audio.setVolume(1.0f);
        audio.setPositional(true);
        audio.setReverbEnabled(false);
        audio.setMaxDistance(50f);
        audio.setRefDistance(5f);
        return audio;
    }
}