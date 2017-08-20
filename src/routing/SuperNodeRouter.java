package routing;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import routing.util.RoutingInfo;
import core.Connection;
import core.DTNHost;
import core.Message;
import core.ModuleCommunicationBus;
import core.NetworkInterface;
import core.Settings;

public class SuperNodeRouter extends ActiveRouter {
	
	private Set<String> ackedMessageIds;

	public SuperNodeRouter(Settings s) {
		super(s);
	}
	
	protected SuperNodeRouter(SuperNodeRouter r) {
		super(r);
		this.ackedMessageIds = new HashSet<String>();
	}
	
	@Override
	public void init(DTNHost host, java.util.List<core.MessageListener> mListeners) {
		super.init(host, mListeners);
		if (host.toString().charAt(0)=='s') {
			ModuleCommunicationBus comBus = getHost().getComBus();
			comBus.updateProperty(NetworkInterface.RANGE_ID, 200.0);
		}
	}

	int[] getGridCoords(DTNHost host) {
		String gridraw =  host.toString().substring(4, 6);
		int coords[] = new int[2];
		coords[0] = gridraw.charAt(0)-'0';
		coords[1] = gridraw.charAt(1)-'0';
		return coords;
	}

	private void tryMessages() {
		if (getHost().toString().charAt(0)=='s') {
			sendMessagesFromSuperNode();
		}
		else {
			sendMessagesFromNormalNode();
		}
		
	}
	private void sendMessagesFromNormalNode() {
		boolean connectedToSuperNode = false;
		
		Collection<Message> messages = getMessageCollection();
		List<Connection> connections = getConnections();
		
		int superNodeCoords[] = new int[2];
		Connection superNodeConn = null;
		for (Connection con : connections) {
			DTNHost other = con.getOtherNode(getHost());
			//System.out.println(other.toString());
			if (other.toString().charAt(0)=='s') {
				connectedToSuperNode = true;
				superNodeCoords[0] = other.toString().charAt(1) - '0';
				superNodeCoords[1] = other.toString().charAt(2) - '0';
				superNodeConn = con;
			}
		}
		
		if (connectedToSuperNode) {
			for (Message m : messages) {
				//Check messages for supernode
				if (m.getTo().toString().substring(0,6).equals(getHost().toString().substring(0,6)))
					continue;
				int msgDestCoords[] = getGridCoords(m.getTo());
				int currCoords[] = getGridCoords(getHost());
				int a = currCoords[0] - msgDestCoords[0];
				int b = currCoords[1] - msgDestCoords[1];
				int x = currCoords[0], y = currCoords[1];
				if(a>0){
					//go up
					x = currCoords[0] - 1;
					if(b>0){
						//go left
						y = currCoords[1] - 1;
					}
				}
				if (x == superNodeCoords[0] &&
						y == superNodeCoords[1]) {
					
					//transfer to supernode
						startTransfer(m, superNodeConn);
				}
			}
		} 
			for (Connection con : connections) {
				DTNHost other = con.getOtherNode(getHost());
				SuperNodeRouter otherRouter = (SuperNodeRouter) other.getRouter();
				
				if (otherRouter.isTransferring()) continue;
				
				if (getGridCoords(other)[0] != getGridCoords(getHost())[0] ||
						getGridCoords(other)[1] != getGridCoords(getHost())[1]) {
						continue;
				}
				
				for (Message message : messages) {
					if (otherRouter.hasMessage(message.getId()) ||
							message.getHops().contains(other)) {
						continue;
					}
					startTransfer(message, con);
				}
		}
		
	}
	private void sendMessagesFromSuperNode() {
		Collection<Message> messages = getMessageCollection();
		List<Connection> connections = getConnections();
		
		for (Message message : messages) {
			int msgDestCoords[] = getGridCoords(message.getTo());
			int superNodeCoords[] = {getHost().toString().charAt(1)-'0',
					getHost().toString().charAt(2)-'0'};
			int x = superNodeCoords[0],
				y = superNodeCoords[1];
			if(msgDestCoords[0] > superNodeCoords[0]){
				x = x+1;
			}
			if(msgDestCoords[1] > superNodeCoords[1]){
				y = y+1;
			}	
			for (Connection con : connections) {
				DTNHost other = con.getOtherNode(getHost());
				if (other.toString().charAt(0)=='s') {
					startTransfer(message, con);
					continue;
				}
				int otherGrid[] = getGridCoords(other);
				if (otherGrid[0] == x && otherGrid[1]== y) {
					startTransfer(message, con);
				}
			}
		}
	}
	
	@Override
	public Message messageTransferred(String id, DTNHost from) {
		Message m = super.messageTransferred(id, from);
		
		if (isDeliveredMessage(m)) {
			this.ackedMessageIds.add(m.getId());
		}
		return m;
	}
	@Override
	protected void transferDone(Connection con) {
		if (getHost().toString().charAt(0) == 's')
			return;
		Message message = con.getMessage();
		DTNHost recipient = con.getOtherNode(getHost());
		
		if ((recipient.toString().charAt(0)=='s' && !getHost().toString().substring(0,6)
				.equals(message.getTo().toString().substring(0,6)) ) ||
				message.getTo() == recipient) {
			this.ackedMessageIds.add(message.getId());
			this.deleteMessage(message.getId(), false);
		}
	}
	
	@Override
	public void changedConnection(Connection con) {
		super.changedConnection(con);
		
		if (con.isUp() && 
				con.isInitiator(getHost()) && 
				con.getOtherNode(getHost()).toString().charAt(0) != 's' &&
				con.getOtherNode(getHost()).toString().substring(0, 6)
				.equals(getHost().toString().substring(0, 6))) {
			SuperNodeRouter otherRouter = 
					(SuperNodeRouter) con.getOtherNode(getHost()).getRouter();
			
			this.ackedMessageIds.addAll(otherRouter.ackedMessageIds);
			otherRouter.ackedMessageIds.addAll(this.ackedMessageIds);
			deleteAckedMessages();
			otherRouter.deleteAckedMessages();
		}
	}
	private void deleteAckedMessages() {
		for (String id : this.ackedMessageIds) {
			if (this.hasMessage(id) && !isSending(id)) {
				this.deleteMessage(id, false);
			}
		}
	}
	@Override
	public void update() {
		super.update();
		if (!canStartTransfer() ||isTransferring()) {
			return; // nothing to transfer or is currently transferring 
		}
		
		// try messages that could be delivered to final recipient
		if (exchangeDeliverableMessages() != null) {
			return;
		}
		
		tryMessages();	
	}
	
	@Override
	public RoutingInfo getRoutingInfo() {
		RoutingInfo top = super.getRoutingInfo();
		RoutingInfo ri = new RoutingInfo(ackedMessageIds.size() + "ACKs");
		
		for (String mid : ackedMessageIds) {
			ri.addMoreInfo(new RoutingInfo(mid));
		}
		top.addMoreInfo(ri);
		return top;
	}
	@Override
	public MessageRouter replicate() {
		SuperNodeRouter r = new SuperNodeRouter(this);
		return r;
	}
}