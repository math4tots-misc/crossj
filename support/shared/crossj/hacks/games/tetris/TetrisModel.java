package crossj.hacks.games.tetris;

import crossj.base.List;
import crossj.base.Rand;

public final class TetrisModel {
    private final double DEFAULT_FALL_RATE = 1.0;
    private Board board = Board.getDefault();
    private Piece piece = null;

    /**
     * The rate at which the live piece falls
     */
    private double fallRate = DEFAULT_FALL_RATE;

    private double timeSinceLastFall = 0.0;

    private TetrisModel() {
    }

    public static TetrisModel getDefault() {
        return new TetrisModel();
    }

    public Board getBoard() {
        return board;
    }

    public double getFallRate() {
        return fallRate;
    }

    /**
     * Updates the model assuming that dt seconds have passed since last tick.
     *
     * @param dt
     */
    public void tick(double dt) {
        if (piece == null) {
            piece = newPiece();
            timeSinceLastFall = 0.0;
        } else {
            timeSinceLastFall += dt;
            if (timeSinceLastFall >= getFallRate()) {
                fall();
                timeSinceLastFall = 0.0;
            }
        }
    }

    private Piece newPiece() {
        var shape = Rand.getDefault().inTuple(Piece.SHAPES);
        return Piece.withShape(shape).moveTo(0, board.getWidth() / 2 - 2);
    }

    private void fall() {
        if (board.canFitPiece(piece.move(1, 0))) {
            piece = piece.move(1, 0);
        } else {
            board.fillWithPiece(piece, 1);
            board.clearFilledRows();
            piece = null;
        }
    }

    public void rotatePiece() {
        var rotatedPiece = piece.rotate(1);
        for (int dcol : List.of(0, -1, 1, -2, 2)) {
            var movedPiece = rotatedPiece.move(0, dcol);
            if (board.canFitPiece(movedPiece)) {
                piece = movedPiece;
                break;
            }
        }
    }

    public void movePieceLeft() {
        if (board.canFitPiece(piece.move(0, -1))) {
            piece = piece.move(0, -1);
        }
    }

    public void movePieceRight() {
        if (board.canFitPiece(piece.move(0, 1))) {
            piece = piece.move(0, 1);
        }
    }

    public void movePieceDown() {
        fall();
    }

    public void hardDrop() {
        while (piece != null) {
            fall();
        }
    }

    /**
     * Returns the currently live piece.
     *
     * May be null
     */
    public Piece getLivePiece() {
        return piece;
    }
}
