import bwapi.*;

public class EnemyUnit 
{
	public Unit myUnit;
	private Position position;
	private UnitType type;
	
	public EnemyUnit(Unit vUnit) 
	{
		myUnit = vUnit;
		position = vUnit.getPosition();
		type = vUnit.getType();
	}
	
	public UnitType getType()
	{
		if (myUnit.exists())
		{
			return myUnit.getType();
		}
		return type;
	}
	
	public Position getPosition()
	{
		if (myUnit.exists())
		{
			return myUnit.getPosition();
		}
		return position;
	}
}
