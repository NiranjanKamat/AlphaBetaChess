package niruChess;

public class ChessBoard {
	private static String chessBoard[][] = {
			{ " ", " ", " ", "q", "a", "b", "k", "r" },
			{ " ", " ", " ", "p", "p", "p", "p", "p" },
			{ " ", " ", " ", " ", " ", " ", " ", " " },
			{ " ", " ", " ", " ", " ", " ", " ", " " },
			{ " ", " ", " ", " ", " ", " ", " ", " " },
			{ " ", " ", " ", " ", " ", " ", " ", " " },
			{ "P", "P", "P", "P", "P", "P", "P", "P" },
			{ "R", "K", "B", "Q", "A", "B", "K", "R" } };

	public String get(int row, int column) {
		return chessBoard[row][column];
	}

	public void set(int row, int column, String piece) {
		chessBoard[row][column] = piece;
	}
}
