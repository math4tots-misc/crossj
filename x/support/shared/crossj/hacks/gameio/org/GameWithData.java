package crossj.hacks.gameio.org;

import crossj.base.List;
import crossj.hacks.gameio.Game;
import crossj.hacks.gameio.GameIO;

/**
 * A Game with GameData using Scenes.
 */
public final class GameWithData<M extends GameModel, D extends GameData<M>> implements Game {
    private final D data;
    private final List<Scene<M, D>> sceneStack;
    private GameIO io;

    private GameWithData(D data, Scene<M, D> startScene) {
        this.data = data;
        this.sceneStack = List.of(startScene);
    }

    public static <M extends GameModel, D extends GameData<M>> GameWithData<M, D> of(D data, Scene<M, D> startScene) {
        return new GameWithData<M, D>(data, startScene);
    }

    public GameIO getIO() {
        return io;
    }

    public D getData() {
        return data;
    }

    private List<Scene<M, D>> getSceneStack() {
        return sceneStack;
    }

    @Override
    public void init(GameIO io) {
        data.setIO(io);
        this.io = io;
    }

    public M getModel() {
        return getData().getModel();
    }

    public Scene<M, D> getCurrentScene() {
        return getSceneStack().last();
    }

    public void popScene() {
        getSceneStack().pop().end(this);
    }

    public void pushScene(Scene<M, D> scene) {
        getSceneStack().add(scene);
        scene.start(this);
    }

    public void setScene(Scene<M, D> scene) {
        popScene();
        pushScene(scene);
    }

    @Override
    public void pause() {
        getCurrentScene().pause(this);
    }

    @Override
    public void resume() {
        getCurrentScene().pause(this);
    }

    @Override
    public void update(double dt) {
        getCurrentScene().update(this, dt);
    }

    @Override
    public void render() {
        getCurrentScene().render(this);
    }

    @Override
    public void resize(int width, int height) {
        getCurrentScene().resize(this, width, height);
    }

    @Override
    public void dispose() {
        var data = getData();
        var stack = getSceneStack();
        while (stack.size() > 0) {
            popScene();
        }
        data.dispose();
    }
}
