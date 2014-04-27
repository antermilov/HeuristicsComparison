import java.util.Arrays;

public class Path {
	int to[], time[];
	int length = 0;

	public Path(int to[], int time[]) throws Exception {
		if ((to.length != time.length))
			throw new IllegalArgumentException(
					"Wrong amount of instructions: (|to| == |time|) not satisfied");

		this.length = to.length;

		for (int i = 0; i < time.length - 1; i++)
			if (time[i] < time[i - 1])
				throw new IllegalArgumentException("Unordered time of events");

		this.to = Arrays.copyOf(to, to.length);
		this.time = Arrays.copyOf(time, to.length);
	}

	public int getSegmentDestination(int index) {
		return to[index];
	}

	public int getSegmentStartTime(int index) {
		return time[index];
	}

	public int getLength() {
		return length;
	}
}
