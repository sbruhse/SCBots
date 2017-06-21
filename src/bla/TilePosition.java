package bla;

public class TilePosition extends bwapi.TilePosition {

    public TilePosition(int x, int y) {
        super(x, y);
    }
    public TilePosition(bwapi.TilePosition bwapiTilePos) {
        super(bwapiTilePos.getX(), bwapiTilePos.getY());
    }

    public TilePosition add( TilePosition aPosition ){
        return new TilePosition(getX() + aPosition.getX(), getY() + aPosition.getY());
    }

    public TilePosition add( bwapi.TilePosition aPosition ){
        return new TilePosition(getX() + aPosition.getX(), getY() + aPosition.getY());
    }

    @Override
    public boolean equals(Object o) {
        if( !(o instanceof TilePosition) ){
            return false;
        }
        return ((TilePosition)o).getX() == getX() && ((TilePosition)o).getY() == getY();
    }

    @Override
    public int hashCode() {
        return getX()*10000+getY();
    }

};