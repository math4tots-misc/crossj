package crossj.hacks.ray.main.ch02;

import crossj.hacks.ray.Matrix;

public final class Environment {
    public Matrix gravity;
    public Matrix wind;

    public Environment(Matrix gravity, Matrix wind) {
        this.gravity = gravity;
        this.wind = wind;
    }
}
