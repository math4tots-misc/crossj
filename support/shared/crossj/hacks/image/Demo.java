package crossj.hacks.image;

import crossj.base.IO;
import crossj.base.M;

public final class Demo {
    public static void main(String[] args) {
        int width = 300, height = 300, radius = 150;
        Bitmap bmp = Bitmap.withDimensions(width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                bmp.setColor(x, y, Color.of(0.2, 0.2, 0.2, 1));
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double dist = M.pow(M.pow(y - height / 2, 2) + M.pow(x - width / 2, 2), 0.5);
                double normDist = dist / radius;
                double lumen = 1 - normDist;
                if (lumen >= 0 && lumen <= 1) {
                    bmp.setColor(x, y, Color.of(0.3, lumen, lumen, 1));
                }
            }
        }
        IO.writeFileBytes("out/demo.bmp", bmp.toBMPBytes());
    }
}
