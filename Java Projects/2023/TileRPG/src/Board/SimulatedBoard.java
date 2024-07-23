package Board;

import java.util.Collection;
import java.util.LinkedList;

import Mobs.Mob;

public class SimulatedBoard extends Board {

    private Board _originalBoard;

    SimulatedBoard (Board originalBoard) {
        super(originalBoard.numRows(), originalBoard.numCols(0));
        _originalBoard = originalBoard;
    }

    public void simulateTurn() {
        copyOriginalOntoSimulated();
        endTurn();
    }

    private void copyOriginalOntoSimulated () {
        for (int r = 0; r < numRows(); ++r)
            for (int c = 0; c < numCols(r); ++c)
                copyTileOntoSimulated(_originalBoard.getTile(r, c), getTile(r, c));
    }
    private void copyTileOntoSimulated (Tile original, Tile simulated) {
        original.projectOnTo(simulated);
    }

    @Override
    public void endTurn () {
        Collection<Mob> mobs = getMobs();
        super.endTurn();
        applyPreviewsOf(mobs);
    }

    private Collection<Mob> getMobs () {
        LinkedList<Mob> mobs = new LinkedList<Mob>();
        for (Tile tile : this)
            if (tile.hasMob())
                mobs.add(tile.getMob());
        return mobs;
    }

    private void applyPreviewsOf (Collection<Mob> mobs) {
        for (Mob mob : mobs)
            mob.applyPreview();
    }
}
