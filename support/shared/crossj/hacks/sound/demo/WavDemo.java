package crossj.hacks.sound.demo;

import crossj.base.IO;
import crossj.hacks.sound.Wav;

public final class WavDemo {
    public static void main(String[] args) {
        var amp = 0.5;
        var dur = 2.0;
        IO.writeFileBytes("out/sound/s440.wav", Wav.sine(440, amp, dur).toWAVBytes());
        IO.writeFileBytes("out/sound/s262.wav", Wav.sine(262, amp, dur).toWAVBytes());
        IO.writeFileBytes("out/sound/s880.wav", Wav.sine(880, amp, dur).toWAVBytes());
        IO.writeFileBytes("out/sound/mix440x262.wav",
                Wav.mix(Wav.sine(440, amp, dur), Wav.sine(262, amp, dur)).toWAVBytes());
        IO.writeFileBytes("out/sound/mix2.wav",
                Wav.mix(Wav.sine(440, amp, dur), Wav.cat(Wav.silence(1), Wav.sine(262, amp, dur))).toWAVBytes());
        IO.writeFileBytes("out/sound/mix3.wav",
                Wav.cat(Wav.mix(Wav.sine(440, amp, dur), Wav.cat(Wav.silence(1), Wav.sine(262, amp, dur))),
                        Wav.mix(Wav.sine(262, amp, dur), Wav.sine(524, amp, dur))).toWAVBytes());
    }
}
