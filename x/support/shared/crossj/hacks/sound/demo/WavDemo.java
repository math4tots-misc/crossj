package crossj.hacks.sound.demo;

import crossj.base.IO;
import crossj.hacks.sound.Wav;

public final class WavDemo {
    public static void main(String[] args) {
        var amp = 0.5;
        var dur = 2;
        IO.writeFileBytes("out/sound/s440.wav", Wav.ofSineWave(440, amp, dur).toWAVBytes());
        IO.writeFileBytes("out/sound/s262.wav", Wav.ofSineWave(262, amp, dur).toWAVBytes());
        IO.writeFileBytes("out/sound/s880.wav", Wav.ofSineWave(880, amp, dur).toWAVBytes());
    }
}
