package crossj.hacks.ray.main.ch02;

import crossj.hacks.ray.Matrix;

public final class Projectile {
    public Matrix position;
    public Matrix velocity;

    public Projectile(Matrix position, Matrix velocity) {
        this.position = position;
        this.velocity = velocity;
    }
}
