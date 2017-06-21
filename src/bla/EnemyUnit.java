package bla;

import bwapi.*;

public class EnemyUnit implements Comparable<EnemyUnit>
{
	public Unit unit;
	private Position position;
	private UnitType type;
	private int weight = -1;
	
	public EnemyUnit(Unit vUnit) 
	{
		unit = vUnit;
		position = vUnit.getPosition();
		type = vUnit.getType();
	}
	
	public UnitType getType()
	{
		if (unit.exists())
		{
			return unit.getType();
		}
		return type;
	}
	
	public Position getPosition()
	{
		if (unit.exists())
		{
			return unit.getPosition();
		}
		return position;
	}
	
	public void setWeight(int weight)
	{
		this.weight = weight;
	}
	
	public int getWeight()
	{
		return this.weight;
	}

	@Override
	public int compareTo(EnemyUnit o) {
		Integer myWeight = this.getWeight();
		return myWeight.compareTo((Integer)o.getWeight());
	}



}

