package crossj.hacks.sound;

import crossj.base.Assert;
import crossj.base.Bytes;
import crossj.base.Func1;
import crossj.base.List;
import crossj.base.M;

/**
 * Quick and easy class to synthesize two-channel LPCM audio sampled at 44,100
 * Hz with 16 bits per sample (i.e. standard format for audio CDs) and write
 * them out as a valid ".wav" file.
 *
 * The samples are mono-sound, but the output wav file is stereo.
 */
public final class Wav {
    private static final int NUMBER_OF_CHANNELS = 2; // stereo
    private static final int SAMPLE_RATE = 44100;
    private static final int BITS_PER_SAMPLE = 16;
    private final List<Integer> sampleData;

    private Wav(List<Integer> sampleData) {
        Assert.withMessage(sampleData.size() % NUMBER_OF_CHANNELS == 0,
                "Wav data must be divisible by " + NUMBER_OF_CHANNELS + " (stereo is always assumed)");
        this.sampleData = sampleData;
    }

    public static Wav ofSamples(int... sampleData) {
        var list = List.<Integer>of();
        for (var sample : sampleData) {
            list.add(sample);
        }
        return new Wav(list);
    }

    public static Wav empty() {
        return new Wav(List.of());
    }

    /**
     * @param duration duration of the returned Wav object.
     * @param f function mapping time (in seconds) to amplitude in range [-1, 1].
     */
    public static Wav ofFunction(double duration, Func1<Double, Double> f) {
        var nsamples = (int) (duration * SAMPLE_RATE);
        var wav = empty();
        for (var i = 0; i < nsamples; i++) {
            var time = (((double) i) / nsamples) * duration;
            var sample = (int) (32767 * M.max(-1, M.min(1, f.apply(time))));
            wav.addMono(sample);
        }
        return wav;
    }

    /**
     * Construct a sine wave given frequency, amplitude and duration.
     * @param freq frequency of the wave in Hz
     * @param amp the amplitude of the wave (clamped to lie between 0 and 1)
     * @param duration number of seconds of data to generate
     * @return
     */
    public static Wav ofSineWave(double freq, double amp, double duration) {
        var adjustedAmp = M.max(0, M.min(1, amp));
        var freqTau = freq * M.TAU;
        return ofFunction(duration, t -> adjustedAmp * M.sin(t * freqTau));
    }

    /**
     * Add the sample value to all channels.
     */
    public void addMono(int sample) {
        sampleData.add(sample);
        sampleData.add(sample);
    }

    public int getSampleRate() {
        return SAMPLE_RATE;
    }

    public int getBitsPerSample() {
        return BITS_PER_SAMPLE;
    }

    public int getNumberOfChannels() {
        return NUMBER_OF_CHANNELS;
    }

    public Bytes toWAVBytes() {
        // References:
        // http://soundfile.sapp.org/doc/WaveFormat/
        var numberOfChannels = NUMBER_OF_CHANNELS;
        var bitsPerSample = BITS_PER_SAMPLE;
        var bytesPerSample = bitsPerSample / 8;
        var sampleRate = SAMPLE_RATE;
        var byteRate = sampleRate * numberOfChannels * bytesPerSample;
        var blockAlign = numberOfChannels * bytesPerSample;
        int chunk1Size = 16; // subchunk size (excluding tag and size)
        int chunk2Size = sampleData.size() * bytesPerSample;
        int chunkSize = 4 + 8 + chunk1Size + 8 + chunk2Size;

        var out = Bytes.withCapacity(chunkSize + 8);

        // -- RIFF header --
        out.addASCII("RIFF");
        out.addI32(chunkSize); // size (excluding ID and Size)
        out.addASCII("WAVE"); // indicate this is a WAVE file

        // -- fmt --
        out.addASCII("fmt ");
        out.addI32(chunk1Size); // subchunk size (excluding tag and size)
        out.addI16(1); // audio format (PCM = 1)
        out.addI16(numberOfChannels); // Mono = 1, Stereo = 2, ...
        out.addI32(sampleRate);
        out.addI32(byteRate);
        out.addI16(blockAlign);
        out.addI16(bitsPerSample);

        // -- data --
        out.addASCII("data");
        out.addI32(chunk2Size);
        for (var sample : sampleData) {
            out.addI16(sample);
        }

        return out;
    }
}
