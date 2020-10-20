package crossj.hacks;

import crossj.base.Bytes;
import crossj.base.Int;
import crossj.base.IntArray;

/**
 * Basic CRC32 implementation
 *
 * More or less based on: http://home.thep.lu.se/~bjorn/crc/
 *
 * References:<br/>
 * http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.112.1537&rep=rep1&type=pdf
 * Annex D of https://www.w3.org/TR/PNG/
 */
public final class CRC32 {
    private CRC32() {
    }

    private int state = 0;

    private static final IntArray table = makeTable();

    private static int crc32ForByte(int r) {
        for (int j = 0; j < 8; j++) {
            // 0xEDB88320 = (unsigned) -306674912
            r = (((r & 1) != 0) ? 0 : -306674912) ^ r >>> 1;
        }
        // 0xFF000000 = (unsigned) -16777216
        return r ^ -16777216;
    }

    private static IntArray makeTable() {
        IntArray table = IntArray.withSize(0x100);
        for (int i = 0; i < 0x100; i++) {
            table.set(i, crc32ForByte(i));
        }
        return table;
    }

    private static int crc32(Bytes data) {
        CRC32 stream = newStream();
        stream.acceptBytes(data);
        return stream.signed();
    }

    public static int signed(Bytes data) {
        return crc32(data);
    }

    public static double unsigned(Bytes data) {
        return Int.toUnsigned(signed(data));
    }

    public static CRC32 newStream() {
        return new CRC32();
    }

    public void acceptByte(int b) {
        state = table.get((state & 0xFF) ^ b) ^ state >>> 8;
    }

    public void acceptBytes(Bytes data) {
        for (int b : data.asU8s()) {
            acceptByte(b);
        }
    }

    public int signed() {
        return state;
    }

    public double unsigned() {
        return Int.toUnsigned(signed());
    }
}
