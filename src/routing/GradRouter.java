package routing;

import core.Coord;
import core.DTNHost;
import core.Settings;

public class GradRouter extends ActiveRouter {

	public GradRouter(Settings s) {
		super(s);
		// TODO Auto-generated constructor stub
	}

	public GradRouter(ActiveRouter r) {
		super(r);
		// TODO Auto-generated constructor stub
	}
	
	
	
	private double getProbability(DTNHost from, DTNHost to){
		Coord fromCoord = from.getLocation();
		Coord toCoord = to.getLocation();
		int toRadioRange = getRadioRange(to);
		double alpha = getAlpha(fromCoord, toCoord, to.getPath().getNextWaypoint());
		double theta = getTheta(fromCoord, toCoord, toRadioRange);
		
		double probability = (theta - alpha)/theta;
		
		return probability;
	}

	private double getTheta(Coord fromCoord, Coord toCoord, int toRadioRange) {
		// TODO Auto-generated method stub
		return 0;
	}

	private double getAlpha(Coord fromCoord, Coord toCoord, Coord nextWaypoint) {
		// TODO Auto-generated method stub
		return 0;
	}

	private int getRadioRange(DTNHost to) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public MessageRouter replicate() {
		// TODO Auto-generated method stub
		return null;
	}

}
