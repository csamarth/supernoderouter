/*
 * Copyright 2017 Shubham Kumaram <krm.shubham@gmail.com>
 * Released under GPLv3.
 */

/**
 * Supernode movement model where the coordinates are restricted to a
 * grid, which in the given protocol encompasses 4 cells of a normal
 * node. The supernode cell is divided into two parts, a core and a
 * suburb. The core is defined around the common vertex of the 4 cells
 * of normal nodes. A supernode should have a higher probability of
 * being in core than in the suburb, but it is configurable via the
 * settings file.
 */

package movement;

import core.Coord;
import core.Settings;

public class SupernodeMovement extends RandomWaypoint {
	/** Total movement range */
	public static final String SNODE_RANGE = "snodeRange";
	/** Center point */
	public static final String SNODE_CENTER  = "snodeCenter";
	/** Core range */
	public static final String SNODE_CORE_RANGE = "snodeCoreRange";
	/** Core probability */
	public static final String SNODE_CORE_PROB = "snodeCoreProb";

	private int s_x_center;
	private int s_y_center;
	private double s_range;
	private double s_c_range;
	private double s_c_prob;

	public SupernodeMovement(Settings s) {
		super(s);

		if (s.contains(SNODE_RANGE)){
			this.s_range = s.getDouble(SNODE_RANGE);
		}
		if (s.contains(SNODE_CENTER)){
			int[] center = s.getCsvInts(SNODE_CENTER,2);
			this.s_x_center = center[0];
			this.s_y_center = center[1];
		}
		if (s.contains(SNODE_CORE_RANGE)) {
			this.s_c_range = s.getDouble(SNODE_CORE_RANGE);
		}
		if (s.contains(SNODE_CORE_PROB)) {
			this.s_c_prob = s.getDouble(SNODE_CORE_PROB, 0.8);
		}
	}

	private SupernodeMovement(SupernodeMovement smv) {
		super(smv);
		this.s_c_prob = smv.s_c_prob;
		this.s_c_range = smv.s_c_range;
		this.s_range = smv.s_range;
		this.s_x_center = smv.s_x_center;
		this.s_y_center = smv.s_y_center;
	}
	
	@Override
	public Coord randomCoord() {
		Coord result;
		double dieRoll = rng.nextDouble();
		if (dieRoll <= s_c_prob) {
			result = getCoreCoord();
		}
		else {
			result = getSuburbCoord();
		}
		return result;
	}

	private Coord getSuburbCoord() {
		double x;
		double y;
		do {
			x = (rng.nextDouble()*2 - 1)*this.s_range;
			y = (rng.nextDouble()*2 - 1)*this.s_range;
		}
		while (Math.pow(x, 10) + Math.pow(y, 10) < Math.pow(this.s_c_range, 10)
				&& Math.pow(x, 10) + Math.pow(y, 10) > Math.pow(this.s_range, 10));
		x += this.s_x_center;
		y += this.s_y_center;
		return new Coord(x,y);
	}

	private Coord getCoreCoord() {
		double x;
		double y;
		do {
			x = (rng.nextDouble()*2 - 1)*this.s_c_range;
			y = (rng.nextDouble()*2 - 1)*this.s_c_range;
		}
		while (Math.pow(x, 10) + Math.pow(y, 10) > Math.pow(this.s_c_range, 10));
		x += this.s_x_center;
		y += this.s_y_center;
		return new Coord(x,y);
	}

	@Override
	public int getMaxX() {
		return (int)Math.ceil(this.s_x_center + this.s_range);
	}

	@Override
	public int getMaxY() {
		return (int)Math.ceil(this.s_y_center + this.s_range);
	}

	@Override
	public SupernodeMovement replicate() {
		return new SupernodeMovement(this);
	}

}
