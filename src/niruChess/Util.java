package niruChess;

public class Util {

	static String reverseMove(String input) {
		return String.format("%04d",
				7777 - Integer.valueOf(input.substring(0, 4)));
	}
}
