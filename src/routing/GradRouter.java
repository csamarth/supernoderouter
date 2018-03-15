package routing;

import core.Connection;
import core.Coord;
import core.DTNHost;
import core.Message;
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
	
	@Override
	public Message messageTransferred(String id, DTNHost from) {
		Message msg = super.messageTransferred(id, from);
		Integer nrofCopies = (Integer) msg.getProperty(MSG_COUNT_PROPERTY);
		MessageTransferScheme messageTransferScheme = (MessageTransferScheme) msg.getProperty(TRANSFER_TYPE_PROPERTY);
		
		/* In case of complete transfer, the nrofCopies does not need to be
		 * changed whereas in case of no transfer, this function will never
		 * be called. Hence we have only two options, binary or naive
		 */
		if (messageTransferScheme == MessageTransferScheme.BINARY) {
			nrofCopies = (int) Math.ceil(nrofCopies/2.0);
		}
		else {
			nrofCopies = 1;
		}
		msg.updateProperty(MSG_COUNT_PROPERTY, nrofCopies);
		return msg;
	}
	
	@Override
	protected void transferDone(Connection con) {
		Integer newNrofCopies;
		MessageTransferScheme msgTransferScheme;
		String msgId = con.getMessage().getId();
		
		//The router's copy of message
		Message m = getMessage(msgId);
		if (m == null) return;
		
		newNrofCopies = (Integer) m.getProperty(MSG_COUNT_PROPERTY);
		msgTransferScheme = (MessageTransferScheme) m.getProperty(TRANSFER_TYPE_PROPERTY);
		
		if (msgTransferScheme == MessageTransferScheme.BINARY) {
			newNrofCopies /= 2;
		}
		else if (msgTransferScheme == MessageTransferScheme.COMPLETE_TRANSFER) {
			newNrofCopies = 0;
			deleteMessage(msgId, false);
		}
		else if (msgTransferScheme == MessageTransferScheme.NAIVE) {
			newNrofCopies--;
		}
		if (newNrofCopies != 0)
			m.updateProperty(MSG_COUNT_PROPERTY, newNrofCopies);
	}
	
	@Override
	public boolean createNewMessage(Message msg) {
		makeRoomForNewMessage(msg.getSize());
		
		msg.setTtl(this.msgTtl);
		msg.addProperty(MSG_COUNT_PROPERTY, new Integer(nrofCopies));
		msg.addProperty(TRANSFER_TYPE_PROPERTY, MessageTransferScheme.NO_TRANSFER);
		addToMessages(msg, true);
		return true;
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
		
		if (alphax < (3*thetax))
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
