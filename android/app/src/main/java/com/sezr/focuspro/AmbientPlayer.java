package com.sezr.focuspro;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.util.Random;

public final class AmbientPlayer {
    private static final int SAMPLE_RATE = 22050;
    private volatile boolean playing = false;
    private Thread thread;
    private AudioTrack track;

    public void play(String mode) {
        stop();
        if (mode == null || mode.equals("Sessiz")) return;
        playing = true;
        thread = new Thread(() -> runAudio(mode), "FocusAmbientPlayer");
        thread.start();
    }

    public void stop() {
        playing = false;
        if (track != null) {
            try { track.stop(); } catch (Exception ignored) {}
            try { track.release(); } catch (Exception ignored) {}
            track = null;
        }
        thread = null;
    }

    private void runAudio(String mode) {
        int min = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        int bufferSize = Math.max(min, SAMPLE_RATE / 2);
        track = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);
        short[] buffer = new short[1024];
        Random random = new Random();
        double phaseA = 0;
        double phaseB = 0;
        double phaseC = 0;
        track.play();
        while (playing) {
            for (int i = 0; i < buffer.length; i++) {
                double sample;
                if (mode.equals("Yağmur")) {
                    sample = (random.nextDouble() * 2 - 1) * 0.20 + Math.sin(phaseA) * 0.03;
                    phaseA += 2 * Math.PI * 18 / SAMPLE_RATE;
                } else if (mode.equals("Beyaz Gürültü")) {
                    sample = (random.nextDouble() * 2 - 1) * 0.18;
                } else if (mode.equals("Lo-fi")) {
                    sample = Math.sin(phaseA) * 0.10 + Math.sin(phaseB) * 0.08 + Math.sin(phaseC) * 0.06;
                    phaseA += 2 * Math.PI * 196 / SAMPLE_RATE;
                    phaseB += 2 * Math.PI * 246.94 / SAMPLE_RATE;
                    phaseC += 2 * Math.PI * 329.63 / SAMPLE_RATE;
                } else if (mode.equals("Doğa")) {
                    sample = Math.sin(phaseA) * 0.12 + Math.sin(phaseB) * 0.05 + (random.nextDouble() * 2 - 1) * 0.04;
                    phaseA += 2 * Math.PI * 7 / SAMPLE_RATE;
                    phaseB += 2 * Math.PI * 880 / SAMPLE_RATE;
                } else {
                    sample = Math.sin(phaseA) * 0.09 + (random.nextDouble() * 2 - 1) * 0.03;
                    phaseA += 2 * Math.PI * 110 / SAMPLE_RATE;
                }
                buffer[i] = (short) Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, sample * 16000));
            }
            try { track.write(buffer, 0, buffer.length); } catch (Exception ignored) { break; }
        }
    }
}
