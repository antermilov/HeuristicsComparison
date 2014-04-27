public class DeliverEvent implements Comparable<DeliverEvent> {
	int from, to, time;

	public DeliverEvent(int from, int to, int time) {
		this.from = from;
		this.to = to;
		this.time = time;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public void setTo(int to) {
		this.to = to;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getFrom() {
		return from;
	}

	public int getTime() {
		return time;
	}

	public int getTo() {
		return to;
	}

	public int compareTo(DeliverEvent arg0) {
		return Integer.compare(this.getTime(), arg0.getTime());
	}
}
