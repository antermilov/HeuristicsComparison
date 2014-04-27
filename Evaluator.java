import java.security.SecureRandom;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import java.util.Random;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;

public class Evaluator {
	int graph[][];
	DeliverEvent events[];

	int vertexCount;
	int truckCount;
	int eventCount;

	Random rnd;

	static int defaultTruckCount = 5;
	static int defaultEventCount = 1000;
	static int defaultVertexCount = 50;

	static int maxDistance = 1000;
	static int maxTime = 10000;

	int getDistance(int from, int to) {
		return graph[from][to];
	}

	int getVertexCount() {
		return vertexCount;
	}

	int getTruckCount() {
		return truckCount;
	}

	int getEventCount() {
		return eventCount;
	}

	DeliverEvent getDelivery(int index) {
		return events[index];
	}

	public Evaluator() {
		new Evaluator(new SecureRandom().nextLong());
	}

	public Evaluator(long seed) {
		new Evaluator(seed, defaultEventCount, defaultVertexCount,
				defaultTruckCount);
	}

	public Evaluator(long seed, int eventCount) {
		new Evaluator(seed, eventCount, defaultVertexCount, defaultTruckCount);
	}

	public Evaluator(long seed, int eventCount, int vertexCount) {
		new Evaluator(seed, eventCount, vertexCount, defaultTruckCount);
	}

	public Evaluator(long seed, int eventCount, int vertexCount, int truckCount) {
		// MAIN CONSTRUCTOR
		this.rnd = new Random(seed);
		this.eventCount = eventCount;
		this.truckCount = truckCount;
		this.vertexCount = vertexCount;
		graph = new int[vertexCount][vertexCount];
		for (int i = 0; i < vertexCount; i++) {
			graph[i][i] = 0;
			for (int j = 0; j < vertexCount; j++)
				if (i != j) {
					graph[i][j] = rnd.nextInt(maxDistance);
				}
		}

		for (int i = 0; i < vertexCount; i++)
			for (int j = 0; j < vertexCount; j++)
				for (int k = 0; k < vertexCount; k++)
					if (graph[i][j] + graph[j][k] < graph[i][k])
						graph[i][k] = graph[i][j] + graph[j][k];

		DeliverEvent events[] = new DeliverEvent[eventCount];
		for (int i = 0; i < eventCount; i++) {
			int from = rnd.nextInt(vertexCount);
			int to = rnd.nextInt(vertexCount - 1);
			if (from == to)
				to = vertexCount - 1;
			events[i] = new DeliverEvent(from, to, rnd.nextInt(maxTime));
		}
		Arrays.sort(events);
	}

	public int evaluate(Path[] instructions) throws Exception {
		if (instructions.length != truckCount)
			throw new IllegalArgumentException("Strange amount of Paths");
		ArrayDeque<DeliverEvent>[] waiters = new ArrayDeque[vertexCount];
		for (int i = 0; i < vertexCount; i++) {
			waiters[i] = new ArrayDeque<DeliverEvent>();
		}

		int orderCount = 0;
		for (Path e : instructions) {
			orderCount += e.getLength();
		}

		Order[] moves = new Order[orderCount];

		int curMove = 0;
		for (int truck = 0; truck < instructions.length; truck++) {
			for (int i = 0; i < instructions[truck].getLength(); i++) {
				moves[curMove] = new Order(truck,
						instructions[truck].getSegmentStartTime(i),
						instructions[truck].getSegmentDestination(i));
				curMove++;
			}
		}

		Arrays.sort(moves);

		int longestWait = 0;
		int processedWaiters = 0;
		int curEvent = 0;
		curMove = 0;
		int curTime = 0;

		int position[] = new int[truckCount];
		int arrival[] = new int[truckCount];

		while ((curEvent != eventCount) || (curMove != orderCount)) {
			int deliverTime = curEvent == eventCount ? Integer.MAX_VALUE
					: events[curEvent].getTime();
			int movementTime = curMove == orderCount ? Integer.MAX_VALUE
					: moves[curMove].getTime();
			if (deliverTime <= movementTime) {
				// process single delivery
				// add that delivery to waiting queue
				waiters[events[curEvent].getFrom()].add(events[curEvent]);
				curEvent++;
			} else {
				int moveTo = moves[curMove].getTo();
				int moveTruckId = moves[curMove].getTruckId();
				int moveTime = moves[curMove].getTime();

				curTime = moveTime;

				if (arrival[moveTruckId] > curTime) {
					throw new RuntimeException(
							"Path turn back on halfway. Please check your instructions.");
				} else {
					ArrayDeque<DeliverEvent> remove = new ArrayDeque<DeliverEvent>();

					for (DeliverEvent e : waiters[position[moveTruckId]]) {
						if (e.getTo() == moveTo) {
							processedWaiters++;
							longestWait = Math.max(longestWait, curTime
									- e.time);
						}
					}

					// for(DeliverEvent e:remove)

					arrival[moveTruckId] = curTime
							+ graph[position[moveTruckId]][moveTo];
					position[moveTruckId] = moveTo;

				}
				curMove++;
			}

		}

		final int MISSED_DELIVERY_PENALTY = 1000000;

		return MISSED_DELIVERY_PENALTY * (eventCount - processedWaiters)
				+ longestWait;

	}

	public void setDistance(int x, int y, int d) throws Exception {
		if ((x < 0) || (y < 0) || (x > vertexCount) || (y > vertexCount))
			throw new IllegalArgumentException(
					"Something wrong with vertice index.");
		if (d < 0)
			throw new IllegalArgumentException("Distance cannot be negative.");
		graph[x][y] = d;
		graph[y][x] = d;
	}

}
