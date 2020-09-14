package crossj.hacks.ray.main.ch02;

import crossj.IO;
import crossj.hacks.image.Bitmap;
import crossj.hacks.image.Color;
import crossj.hacks.ray.Matrix;

public final class Main {
    public static void main(String[] args) {
        Matrix start = Matrix.newPoint(0, 1, 0);
        Matrix velocity = Matrix.newVector(1, 1.8, 0).normalize().scale(11.25);
        Projectile p = new Projectile(start, velocity);
        Matrix gravity = Matrix.newVector(0, -0.1, 0);
        Matrix wind = Matrix.newVector(-0.01, 0, 0);
        Environment e = new Environment(gravity, wind);
        Bitmap c = Bitmap.withDimensions(900, 550);
        int width = c.getWidth();
        int height = c.getHeight();

        Color white = Color.rgb(1, 1, 1);

        for (int i = 0; i < 1000; i++) {
            int x = (int) p.position.getX();
            int y = height - (int) p.position.getY();
            if (x >= 0 && x < width && y >= 0 && y < height) {
                c.setColor(x, y, white);
            }
            p = tick(e, p);
        }

        IO.writeFileBytes("out/ray-ch02.bmp", c.toBMPBytes());
    }

    public static Projectile tick(Environment env, Projectile proj) {
        Matrix position = proj.position.add(proj.velocity);
        Matrix velocity = proj.velocity.add(env.gravity).add(env.wind);
        return new Projectile(position, velocity);
    }
}
