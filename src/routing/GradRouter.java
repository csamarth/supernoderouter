package routing;

import core.Coord;
import core.DTNHost;
import core.NetworkInterface;
import core.Settings;

public class GradRouter extends ActiveRouter {
	/** identifier for the initial number of copies setting ({@value})*/
	public static final String NROF_COPIES = "nrofCopies";
	/** GradRouter settings' name space ({@value})*/
	public static final String GRAD_NS = "GradRouter";
	/** Key for number of copies property */
	public static final String MSG_COUNT_PROPERTY = GRAD_NS + "." + NROF_COPIES;
	/** Key for last transfer type */
	public static final String TRANSFER_TYPE_PROPERTY = GRAD_NS + ".lastTransferType";
	
	private int nrofCopies;
	
	public GradRouter(Settings s) {
		super(s);
		
		Settings gradSettings = new Settings(GRAD_NS);
		nrofCopies = gradSettings.getInt(NROF_COPIES);
	}

	public GradRouter(GradRouter r) {
		super(r);
		this.nrofCopies = r.nrofCopies;
	}
	
	private MessageTransferScheme getMessageTransferScheme(DTNHost ni, DTNHost nx, DTNHost to) {
		if (nx.getLocation().distance(to.getLocation()) < getRadioRange(to))
			return MessageTransferScheme.COMPLETE_TRANSFER;
		
		double pi = getProbability(ni, to);
		double px = getProbability(nx, to);
		
		if (px > pi) return MessageTransferScheme.BINARY;
		
		double[] anglesx = getAngles(nx, to);
		double alphax = anglesx[0];
		double thetax = anglesx[1];
		
		//what shall we do when px < pi and alphax < thetax?
		
		if (thetax < alphax && alphax < (3*thetax))
			return MessageTransferScheme.NAIVE;
		if (alphax > (3 * thetax))
			return MessageTransferScheme.NO_TRANSFER;
		
		return null;
	}
	
	private double[] getAngles(DTNHost from, DTNHost to) {
		Coord fromCoord = from.getLocation();
		Coord toCoord = to.getLocation();
		double toRadioRange = getRadioRange(to);
		double alpha = getAlpha(fromCoord, toCoord, to.getPath().getNextWaypoint());
		double theta = getTheta(fromCoord, toCoord, toRadioRange);
		return new double[]{alpha, theta};
	}
	
	private double getProbability(DTNHost from, DTNHost to){
		double[] angles = getAngles(from, to);
		double alpha = angles[0];
		double theta = angles[1];
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
	
	private enum MessageTransferScheme {
		COMPLETE_TRANSFER,
		NO_TRANSFER,
		BINARY,
		NAIVE
	}
}
