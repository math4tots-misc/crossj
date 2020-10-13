package sanity.games.tetris;

import crossj.base.Assert;
import crossj.base.Test;
import crossj.hacks.games.tetris.Piece;

public final class PieceTest {
    @Test
    public static void str() {
        var piece = Piece.withShape(Piece.SHAPES.last());
        Assert.equals(
            piece.dump(),
                "  x \n" +
                "  x \n" +
                "  x \n" +
                "  x \n");
        Assert.equals(
            piece.rotate(1).dump(),
                "    \n" +
                "    \n" +
                "xxxx\n" +
                "    \n");
        Assert.equals(
            piece.rotate(2).dump(),
                " x  \n" +
                " x  \n" +
                " x  \n" +
                " x  \n");
        Assert.equals(
            piece.rotate(3).dump(),
                "    \n" +
                "xxxx\n" +
                "    \n" +
                "    \n");
        Assert.equals(piece.rotate(4).dump(), piece.dump());
        Assert.equals(piece.rotate(5).dump(), piece.rotate(1).dump());
    }
}
