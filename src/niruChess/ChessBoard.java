package niruChess;

public class ChessBoard {
	public static String[][] chessBoard = null;

	ChessBoard() {
		chessBoard = defaultChessBoard;
	}

	ChessBoard(String[][] chessBoard) {
		ChessBoard.chessBoard = chessBoard;
	}

	public static ChessBoard onlyKingComputerBoard() {
		return new ChessBoard(ChessBoard.onlyKingComputerBoard);
	}

	public static ChessBoard onlyKingHumanBoard() {
		return new ChessBoard(ChessBoard.onlyKingHumanBoard);
	}

	static String[][] defaultChessBoard = {
			{ "r", "k", "b", "q", "a", "b", "k", "r" },
			{ "p", "p", "p", "p", "p", "p", "p", "p" },
			{ " ", " ", " ", " ", " ", " ", " ", " " },
			{ " ", " ", " ", " ", " ", " ", " ", " " },
			{ " ", " ", " ", " ", " ", " ", " ", " " },
			{ " ", " ", " ", " ", " ", " ", " ", " " },
			{ "P", "P", "P", "P", "P", "P", "P", "P" },
			{ "R", "K", "B", "Q", "A", "B", "K", "R" } };

	static String[][] onlyKingComputerBoard = new String[][] {
			{ " ", " ", " ", " ", "a", " ", " ", " " },
			{ " ", " ", " ", " ", " ", " ", " ", " " },
			{ " ", " ", " ", " ", " ", " ", " ", " " },
			{ " ", " ", " ", " ", " ", " ", " ", " " },
			{ " ", " ", " ", " ", " ", " ", " ", " " },
			{ " ", " ", " ", " ", " ", " ", " ", " " },
			{ "P", "P", "P", "P", "P", "P", "P", "P" },
			{ "R", "K", "B", "Q", "A", "B", "K", "R" } };

	static String[][] onlyKingHumanBoard = new String[][] {
			{ "r", "k", "b", "q", "a", "b", "k", "r" },
			{ "p", "p", "p", "p", "p", "p", "p", "p" },
			{ " ", " ", " ", " ", " ", " ", " ", " " },
			{ " ", " ", " ", " ", " ", " ", " ", " " },
			{ " ", " ", " ", " ", " ", " ", " ", " " },
			{ " ", " ", " ", " ", " ", " ", " ", " " },
			{ " ", " ", " ", " ", " ", " ", " ", " " },
			{ " ", " ", " ", " ", "A", " ", " ", " " } };

	public String get(int row, int column) {
		return chessBoard[row][column];
	}

	public String get(char row, char column) {
		return get(Character.getNumericValue(row),
				Character.getNumericValue(column));
	}

	public void set(int row, int column, String piece) {
		chessBoard[row][column] = piece;
	}

	public void set(int row, char column, String piece) {
		set(row, Character.getNumericValue(column), piece);
	}

	public void set(char row, int column, String piece) {
		set(Character.getNumericValue(row), column, piece);
	}

	public void set(char row, char column, String piece) {
		set(Character.getNumericValue(row), Character.getNumericValue(column),
				piece);
	}
}