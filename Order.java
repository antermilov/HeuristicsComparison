public class Order implements Comparable<Order> {
	int truckId, time, to;

	public Order(int truckId, int time, int to) {
		this.truckId = truckId;
		this.time = time;
		this.to = to;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public void setTruckId(int truckId) {
		this.truckId = truckId;
	}

	public void setTo(int to) {
		this.to = to;
	}

	public int getTime() {
		return time;
	}

	public int getTruckId() {
		return truckId;
	}

	public int getTo() {
		return to;
	}

	public int compareTo(Order arg0) {
		return Integer.compare(this.getTime(), arg0.getTime());
	}
}
