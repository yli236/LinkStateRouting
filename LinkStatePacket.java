package HW5;

import java.util.List;

public class LinkStatePacket {
	int TTL = 10;
	int sequence = 1;
	int originalId;
	int lastid;
	List<Edge> edges;
	
	public LinkStatePacket(int originalId, int lastId, List<Edge> edges) {
		this.originalId = originalId;
		this.lastid = lastId;
		this.edges = edges;
	}
}
