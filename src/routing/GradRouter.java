package routing;

import core.Coord;
import core.DTNHost;
import core.NetworkInterface;
import core.Settings;

public class GradRouter extends ActiveRouter {
	
	public GradRouter(Settings s) {
		super(s);
	}

	public GradRouter(ActiveRouter r) {
		super(r);
	}
	
	private double getProbability(DTNHost from, DTNHost to){
		Coord fromCoord = from.getLocation();
		Coord toCoord = to.getLocation();
		double toRadioRange = getRadioRange(to);
		double alpha = getAlpha(fromCoord, toCoord, to.getPath().getNextWaypoint());
		double theta = getTheta(fromCoord, toCoord, toRadioRange);
		
		double probability = (theta - alpha)/theta;
		
		return probability;
	}

	private double getTheta(Coord fromCoord, Coord toCoord, double toRadioRange) {
		double distance = fromCoord.distance(toCoord) ;
		double theta = Math.atan(toRadioRange/distance);
		return theta;
	}

	private double getAlpha(Coord fromCoord, Coord toCoord, Coord nextWaypoint) {
		double slopePath = getSlope(fromCoord, nextWaypoint);
		double slopeDest = getSlope(fromCoord, toCoord);
		double alpha = Math.abs(Math.atan(slopePath) - Math.atan(slopeDest));
		return alpha;
	}

	private double getSlope(Coord from, Coord to) {
		double slope = (to.getY() - from.getY())/(to.getX() - from.getX());
		return slope;
	}

	private double getRadioRange(DTNHost to) {
		return (double)getHost().getComBus().getProperty(NetworkInterface.RANGE_ID);
	}

	@Override
	public MessageRouter replicate() {
		GradRouter r = new GradRouter(this);	
		return r;
	}
}
