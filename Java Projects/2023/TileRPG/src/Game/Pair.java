package Game;

import java.util.Objects;

public class Pair <T1, T2> {
	public T1 first;
	public T2 second;
	public Pair (T1 first, T2 second) {
		this.first = first;
		this.second = second;
	}
	
	@Override
	public boolean equals (Object other) {
		if (!(other instanceof Pair))
			return false;
		Pair otherPair = (Pair) other;
		return first.equals(otherPair.first)
				&& second.equals(otherPair.second);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(first, second);
	}
}
