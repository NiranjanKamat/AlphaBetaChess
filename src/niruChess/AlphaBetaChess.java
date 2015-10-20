package niruChess;

import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class AlphaBetaChess {
	static ChessBoard chessBoard = new ChessBoard();
	static int humanAsWhite = -1;// 1=human as white, 0=human as black
	static int globalDepth = 2;
	static boolean kingCMoved = false, kingLMoved = false;
	static boolean rook56Moved = false, rook7Moved = false;
	static boolean rook63Moved = false, rook0Moved = false;

	static int kingPositionC() {
		int kingPositionC = 0;
		while (!"A"
				.equals(chessBoard.get(kingPositionC / 8, kingPositionC % 8))) {
			kingPositionC++;
		}
		return kingPositionC;
	}

	public static void main(String[] args) {
		System.out.println(UserInterface.pawnPromotion());
		/*
		 * PIECE=WHITE/black pawn=P/p kinght (horse)=K/k bishop=B/b rook
		 * (castle)=R/r Queen=Q/q King=A/a
		 * 
		 * (1234b represents row1,column2 moves to row3, column4 which captured
		 * b (a space represents no capture))
		 */
		humanAsWhite = 1;
		JFrame f = new JFrame("Chess Tutorial");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		UserInterface ui = new UserInterface();
		f.add(ui);
		f.setSize(600, 500);
		f.setVisible(true);

		Object[] option = { "Computer", "Human" };
		humanAsWhite = JOptionPane.showOptionDialog(null,
				"Who should play as white?", "", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
		if (humanAsWhite == 0) {
			UserInterface.humanAsWhite = false;
			long startTime = System.currentTimeMillis();
			String computerMove = alphaBeta(globalDepth, 1000000, -1000000, "",
					0);
			makeMove(computerMove);
			long endTime = System.currentTimeMillis();
			System.out.println("That took " + (endTime - startTime)
					+ " milliseconds");
			flipBoard();
			f.repaint();

		}
		// // makeMove("7655 ");
		// // undoMove("7655 ");
		// for (int i = 0; i < 8; i++) {
		// System.out.println(Arrays.toString(chessBoard[i]));
		// }
	}

	public static void printBoard(String[][] chessBoard) {
		for (int i = 0; i < 8; i++) {
			System.out.println(i + " " + Arrays.toString(chessBoard[i]));
		}
		System.out.println("   0, 1, 2, 3, 4, 5, 6, 7");
	}

	public static String alphaBeta(int depth, int beta, int alpha, String move,
			int player) {
		// return in the form of 1234b##########
		String list = posibleMoves();
		if (depth == 0 || list.length() == 0) {
			return move
					+ (Rating.rating(list.length(), depth) * (1 - player * 2));
		}
		list = sortMoves(list);
		player = 1 - player;// either 1 or 0: 0 is computer 1 is human
		for (int i = 0; i < list.length(); i += 5) {
			makeMove(list.substring(i, i + 5));
			flipBoard();
			String returnString = alphaBeta(depth - 1, beta, alpha,
					list.substring(i, i + 5), player);
			int value = Integer.valueOf(returnString.substring(5));
			flipBoard();
			undoMove(list.substring(i, i + 5));
			if (player == 0) {
				if (value <= beta) {
					beta = value;
					if (depth == globalDepth) {
						move = returnString.substring(0, 5);
					}
				}
			} else {
				if (value > alpha) {
					alpha = value;
					if (depth == globalDepth) {
						move = returnString.substring(0, 5);
					}
				}
			}
			if (alpha >= beta) {
				if (player == 0) {
					return move + beta;
				} else {
					return move + alpha;
				}
			}
		}
		if (player == 0) {
			return move + beta;
		} else {
			return move + alpha;
		}
	}

	public static void flipBoard() {
		String temp;
		for (int i = 0; i < 32; i++) {
			int r = i / 8, c = i % 8;
			temp = switchCase(chessBoard.get(r, c));
			chessBoard.set(r, c, switchCase(chessBoard.get(7 - r, 7 - c)));
			chessBoard.set(7 - r, 7 - c, temp);
		}

		boolean movedTemp = kingCMoved;
		kingCMoved = kingLMoved;
		kingLMoved = movedTemp;

		boolean rook56Temp = rook56Moved;
		rook56Moved = rook7Moved;
		rook7Moved = rook56Temp;

		boolean rook63Temp = rook63Moved;
		rook63Moved = rook0Moved;
		rook0Moved = rook63Temp;
	}

	public static String switchCase(String input) {
		if (Character.isUpperCase(input.charAt(0))) {
			return input.toLowerCase();
		} else {
			return input.toUpperCase();
		}
	}

	public static void makeMove(String move) {
		if (move.charAt(4) == 'Z') {
			castleRight();
		} else if (move.charAt(4) == 'Y') {
			castleLeft();
		} else if (move.charAt(4) == 'P') {
			// if pawn promotion
			chessBoard.set(1, Character.getNumericValue(move.charAt(0)), " ");
			chessBoard.set(0, Character.getNumericValue(move.charAt(1)),
					String.valueOf(move.charAt(3)));
		} else {
			chessBoard.set(Character.getNumericValue(move.charAt(2)), Character
					.getNumericValue(move.charAt(3)), chessBoard.get(
					Character.getNumericValue(move.charAt(0)),
					Character.getNumericValue(move.charAt(1))));
			chessBoard.set(Character.getNumericValue(move.charAt(0)),
					Character.getNumericValue(move.charAt(1)), " ");
		}
	}

	public static void undoMove(String move) {
		if (move.charAt(4) == 'Z') {
			uncastleRight();
		} else if (move.charAt(4) == 'Y') {
			uncastleLeft();
		} else if (move.charAt(4) == 'P') {
			// if pawn promotion
			chessBoard.set(1, Character.getNumericValue(move.charAt(0)), "P");
			chessBoard.set(0, Character.getNumericValue(move.charAt(1)),
					String.valueOf(move.charAt(2)));
		} else {
			chessBoard.set(Character.getNumericValue(move.charAt(0)), Character
					.getNumericValue(move.charAt(1)), chessBoard.get(
					Character.getNumericValue(move.charAt(2)),
					Character.getNumericValue(move.charAt(3))));
			chessBoard.set(Character.getNumericValue(move.charAt(2)),
					Character.getNumericValue(move.charAt(3)),
					String.valueOf(move.charAt(4)));
		}
	}

	public static String posibleMoves() {
		String list = "";
		for (int i = 0; i < 64; i++) {
			switch (chessBoard.get(i / 8, i % 8)) {
				case "P":
					list += posibleP(i);
					break;
				case "R":
					list += posibleR(i);
					break;
				case "K":
					list += possibleKnight(i);
					break;
				case "B":
					list += posibleB(i);
					break;
				case "Q":
					list += posibleQ(i);
					break;
				case "A":
					list += possibleKing(i);
					break;
			}
		}
		return list;// x1,y1,x2,y2,captured piece
	}

	// TODO enpassant
	public static String posibleP(int i) {
		String list = "", oldPiece;
		int r = i / 8, c = i % 8;
		for (int j = -1; j <= 1; j += 2) {
			try {// capture
				if (Character.isLowerCase(chessBoard.get(r - 1, c + j)
						.charAt(0)) && i >= 16) {
					oldPiece = chessBoard.get(r - 1, c + j);
					chessBoard.set(r, c, " ");
					chessBoard.set(r - 1, c + j, "P");
					if (kingSafe()) {
						list = list + r + c + (r - 1) + (c + j) + oldPiece;
					}
					chessBoard.set(r, c, "P");
					chessBoard.set(r - 1, c + j, oldPiece);
				}
			} catch (Exception e) {
			}
			try {// promotion && capture
				if (Character.isLowerCase(chessBoard.get(r - 1, c + j)
						.charAt(0)) && i < 16) {
					String[] temp = { "Q", "R", "B", "K" };
					for (int k = 0; k < 4; k++) {
						oldPiece = chessBoard.get(r - 1, c + j);
						chessBoard.set(r, c, " ");
						chessBoard.set(r - 1, c + j, temp[k]);
						if (kingSafe()) {
							// column1,column2,captured-piece,new-piece,P
							list = list + c + (c + j) + oldPiece + temp[k]
									+ "P";
						}
						chessBoard.set(r, c, "P");
						chessBoard.set(r - 1, c + j, oldPiece);
					}
				}
			} catch (Exception e) {
			}
		}
		try {// move one up
			if (" ".equals(chessBoard.get(r - 1, c)) && i >= 16) {
				oldPiece = chessBoard.get(r - 1, c);
				chessBoard.set(r, c, " ");
				chessBoard.set(r - 1, c, "P");
				if (kingSafe()) {
					list = list + r + c + (r - 1) + c + oldPiece;
				}
				chessBoard.set(r, c, "P");
				chessBoard.set(r - 1, c, oldPiece);
			}
		} catch (Exception e) {
		}
		try {// promotion && no capture
			if (" ".equals(chessBoard.get(r - 1, c)) && i < 16) {
				String[] temp = { "Q", "R", "B", "K" };
				for (int k = 0; k < 4; k++) {
					oldPiece = chessBoard.get(r - 1, c);
					chessBoard.set(r, c, " ");
					chessBoard.set(r - 1, c, temp[k]);
					if (kingSafe()) {
						// column1,column2,captured-piece,new-piece,P
						list = list + c + c + oldPiece + temp[k] + "P";
					}
					chessBoard.set(r, c, "P");
					chessBoard.set(r - 1, c, oldPiece);
				}
			}
		} catch (Exception e) {
		}
		try {// move two up
			if (" ".equals(chessBoard.get(r - 1, c))
					&& " ".equals(chessBoard.get(r - 2, c)) && i >= 48
					&& i < 56) {
				chessBoard.set(r, c, " ");
				chessBoard.set(r - 2, c, "P");
				if (kingSafe()) {
					list = list + r + c + (r - 2) + c + " ";
				}
				chessBoard.set(r, c, "P");
				chessBoard.set(r - 2, c, " ");
			}
		} catch (Exception e) {
		}
		return list;
	}

	public static String posibleR(int i) {
		String list = "", oldPiece;
		int r = i / 8, c = i % 8;
		for (int j = -1; j <= 1; j += 2) {
			int temp = 1;
			try {
				while (" ".equals(chessBoard.get(r, c + temp * j))) {
					oldPiece = chessBoard.get(r, c + temp * j);
					chessBoard.set(r, c, " ");
					chessBoard.set(r, c + temp * j, "R");
					if (kingSafe()) {
						list = list + r + c + r + (c + temp * j) + oldPiece;
					}
					chessBoard.set(r, c, "R");
					chessBoard.set(r, c + temp * j, oldPiece);
					temp++;
				}
				if (Character.isLowerCase(chessBoard.get(r, c + temp * j)
						.charAt(0))) {
					oldPiece = chessBoard.get(r, c + temp * j);
					chessBoard.set(r, c, " ");
					chessBoard.set(r, c + temp * j, "R");
					if (kingSafe()) {
						list = list + r + c + r + (c + temp * j) + oldPiece;
					}
					chessBoard.set(r, c, "R");
					chessBoard.set(r, c + temp * j, oldPiece);
				}
			} catch (Exception e) {
			}
			temp = 1;
			try {
				while (" ".equals(chessBoard.get(r + temp * j, c))) {
					oldPiece = chessBoard.get(r + temp * j, c);
					chessBoard.set(r, c, " ");
					chessBoard.set(r + temp * j, c, "R");
					if (kingSafe()) {
						list = list + r + c + (r + temp * j) + c + oldPiece;
					}
					chessBoard.set(r, c, "R");
					chessBoard.set(r + temp * j, c, oldPiece);
					temp++;
				}
				if (Character.isLowerCase(chessBoard.get(r + temp * j, c)
						.charAt(0))) {
					oldPiece = chessBoard.get(r + temp * j, c);
					chessBoard.set(r, c, " ");
					chessBoard.set(r + temp * j, c, "R");
					if (kingSafe()) {
						list = list + r + c + (r + temp * j) + c + oldPiece;
					}
					chessBoard.set(r, c, "R");
					chessBoard.set(r + temp * j, c, oldPiece);
				}
			} catch (Exception e) {
			}
		}
		return list;
	}

	public static String possibleKnight(int i) {
		String list = "", oldPiece;
		int r = i / 8, c = i % 8;
		for (int j = -1; j <= 1; j += 2) {
			for (int k = -1; k <= 1; k += 2) {
				try {
					if (Character.isLowerCase(chessBoard.get(r + j, c + k * 2)
							.charAt(0))
							|| " ".equals(chessBoard.get(r + j, c + k * 2))) {
						oldPiece = chessBoard.get(r + j, c + k * 2);
						chessBoard.set(r, c, " ");
						chessBoard.set(r + j, c + k * 2, "K");
						if (kingSafe()) {
							list = list + r + c + (r + j) + (c + k * 2)
									+ oldPiece;
						}
						chessBoard.set(r, c, "K");
						chessBoard.set(r + j, c + k * 2, oldPiece);
					}
				} catch (Exception e) {
				}
				try {
					if (Character.isLowerCase(chessBoard.get(r + j * 2, c + k)
							.charAt(0))
							|| " ".equals(chessBoard.get(r + j * 2, c + k))) {
						oldPiece = chessBoard.get(r + j * 2, c + k);
						chessBoard.set(r, c, " ");
						chessBoard.set(r + j * 2, c + k, "K");
						if (kingSafe()) {
							list = list + r + c + (r + j * 2) + (c + k)
									+ oldPiece;
						}
						chessBoard.set(r, c, "K");
						chessBoard.set(r + j * 2, c + k, oldPiece);
					}
				} catch (Exception e) {
				}
			}
		}
		return list;
	}

	public static String posibleB(int i) {
		String list = "", oldPiece;
		int r = i / 8, c = i % 8;
		for (int j = -1; j <= 1; j += 2) {
			for (int k = -1; k <= 1; k += 2) {
				int temp = 1;
				try {
					while (" ".equals(chessBoard
							.get(r + temp * j, c + temp * k))) {
						oldPiece = chessBoard.get(r + temp * j, c + temp * k);
						chessBoard.set(r, c, " ");
						chessBoard.set(r + temp * j, c + temp * k, "B");
						if (kingSafe()) {
							list = list + r + c + (r + temp * j)
									+ (c + temp * k) + oldPiece;
						}
						chessBoard.set(r, c, "B");
						chessBoard.set(r + temp * j, c + temp * k, oldPiece);
						temp++;
					}
					if (Character.isLowerCase(chessBoard.get(r + temp * j,
							c + temp * k).charAt(0))) {
						oldPiece = chessBoard.get(r + temp * j, c + temp * k);
						chessBoard.set(r, c, " ");
						chessBoard.set(r + temp * j, c + temp * k, "B");
						if (kingSafe()) {
							list = list + r + c + (r + temp * j)
									+ (c + temp * k) + oldPiece;
						}
						chessBoard.set(r, c, "B");
						chessBoard.set(r + temp * j, c + temp * k, oldPiece);
					}
				} catch (Exception e) {
				}
			}
		}
		return list;
	}

	public static String posibleQ(int i) {
		String list = "", oldPiece;
		int r = i / 8, c = i % 8;
		for (int j = -1; j <= 1; j++) {
			for (int k = -1; k <= 1; k++) {
				if (j != 0 || k != 0) {
					int temp = 1;
					try {
						while (" ".equals(chessBoard.get(r + temp * j, c + temp
								* k))) {
							oldPiece = chessBoard.get(r + temp * j, c + temp
									* k);
							chessBoard.set(r, c, " ");
							chessBoard.set(r + temp * j, c + temp * k, "Q");
							if (kingSafe()) {
								list = list + r + c + (r + temp * j)
										+ (c + temp * k) + oldPiece;
							}
							chessBoard.set(r, c, "Q");
							chessBoard
									.set(r + temp * j, c + temp * k, oldPiece);
							temp++;
						}
						if (Character.isLowerCase(chessBoard.get(r + temp * j,
								c + temp * k).charAt(0))) {
							oldPiece = chessBoard.get(r + temp * j, c + temp
									* k);
							chessBoard.set(r, c, " ");
							chessBoard.set(r + temp * j, c + temp * k, "Q");
							if (kingSafe()) {
								list = list + r + c + (r + temp * j)
										+ (c + temp * k) + oldPiece;
							}
							chessBoard.set(r, c, "Q");
							chessBoard
									.set(r + temp * j, c + temp * k, oldPiece);
						}
					} catch (Exception e) {
					}
				}
			}
		}
		return list;
	}

	public static String possibleKing(int i) {
		String list = "", oldPiece;
		int r = i / 8, c = i % 8;
		for (int j = 0; j < 9; j++) {
			if (j != 4) {
				try {
					if (Character.isLowerCase(chessBoard.get(r - 1 + j / 3,
							c - 1 + j % 3).charAt(0))
							|| " ".equals(chessBoard.get(r - 1 + j / 3, c - 1
									+ j % 3))) {
						oldPiece = chessBoard.get(r - 1 + j / 3, c - 1 + j % 3);
						chessBoard.set(r, c, " ");
						chessBoard.set(r - 1 + j / 3, c - 1 + j % 3, "A");

						if (kingSafe()) {
							list = list + r + c + (r - 1 + j / 3)
									+ (c - 1 + j % 3) + oldPiece;
						}
						chessBoard.set(r, c, "A");
						chessBoard.set(r - 1 + j / 3, c - 1 + j % 3, oldPiece);

					}
				} catch (Exception e) {
				}
			}
		}

		// Y is castling 7472. Z is castling 7476
		// TODO without the explicit check for "A", opponent kings spawn out of
		// nowhere. Must be bug in move rating
		if (!kingCMoved) {
			if (chessBoard.get(7, 4).equals("A") && !rook56Moved
					&& (chessBoard.get(7, 0).equals("R"))
					&& (chessBoard.get(7, 1).equals(" "))
					&& (chessBoard.get(7, 2).equals(" "))
					&& (chessBoard.get(7, 3).equals(" "))) {
				castleLeft();
				if (kingSafe()) {
					list = list + "7472Y";
				}
				uncastleLeft();
			}
			if (chessBoard.get(7, 4).equals("A") && !rook63Moved
					&& (chessBoard.get(7, 7).equals("R"))
					&& (chessBoard.get(7, 6).equals(" "))
					&& (chessBoard.get(7, 5).equals(" "))) {
				castleRight();
				if (kingSafe()) {
					list = list + "7476Z";
				}
				uncastleRight();
			}
		}
		return list;
	}

	static void castleLeft() {
		chessBoard.set(7, 2, "A");
		chessBoard.set(7, 3, "R");
		chessBoard.set(7, 0, " ");
		chessBoard.set(7, 4, " ");
		kingCMoved = true;
		rook56Moved = true;
	}

	static void uncastleLeft() {

		chessBoard.set(7, 2, " ");
		chessBoard.set(7, 3, " ");
		chessBoard.set(7, 0, "R");
		chessBoard.set(7, 4, "A");
		kingCMoved = false;
		rook56Moved = false;
	}

	static void castleRight() {
		chessBoard.set(7, 6, "A");
		chessBoard.set(7, 5, "R");
		chessBoard.set(7, 4, " ");
		chessBoard.set(7, 7, " ");
		kingCMoved = true;
		rook63Moved = true;
	}

	static void uncastleRight() {
		chessBoard.set(7, 5, " ");
		chessBoard.set(7, 6, " ");
		chessBoard.set(7, 7, "R");
		chessBoard.set(7, 4, "A");
		kingCMoved = false;
		rook63Moved = false;
	}

	public static String sortMoves(String list) {
		int[] score = new int[list.length() / 5];
		for (int i = 0; i < list.length(); i += 5) {
			makeMove(list.substring(i, i + 5));
			score[i / 5] = Rating.rating(-1, 0);
			undoMove(list.substring(i, i + 5));
		}
		String newListA = "", newListB = list;
		for (int i = 0; i < Math.min(6, list.length() / 5); i++) {// first few
			// moves only
			int max = -1000000, maxLocation = 0;
			for (int j = 0; j < list.length() / 5; j++) {
				if (score[j] > max) {
					max = score[j];
					maxLocation = j;
				}
			}
			score[maxLocation] = -1000000;
			newListA += list.substring(maxLocation * 5, maxLocation * 5 + 5);
			newListB = newListB.replace(
					list.substring(maxLocation * 5, maxLocation * 5 + 5), "");
		}
		return newListA + newListB;
	}

	public static boolean kingSafe() {
		return squareSafe(kingPositionC());
	}

	public static boolean squareSafe(int position) {
		// bishop/queen
		for (int i = -1; i <= 1; i += 2) {
			for (int j = -1; j <= 1; j += 2) {
				int temp = 1;
				try {
					while (" ".equals(chessBoard.get(position / 8 + temp * i,
							position % 8 + temp * j))) {
						temp++;
					}
					if ("b".equals(chessBoard.get(position / 8 + temp * i,
							position % 8 + temp * j))
							|| "q".equals(chessBoard.get(position / 8 + temp
									* i, position % 8 + temp * j))) {
						return false;
					}
				} catch (Exception e) {
				}
			}
		}
		// rook/queen
		for (int i = -1; i <= 1; i += 2) {
			int temp = 1;
			try {
				while (" ".equals(chessBoard.get(position / 8, position % 8
						+ temp * i))) {
					temp++;
				}
				if ("r".equals(chessBoard.get(position / 8, position % 8 + temp
						* i))
						|| "q".equals(chessBoard.get(position / 8, position % 8
								+ temp * i))) {
					return false;
				}
			} catch (Exception e) {
			}
			temp = 1;
			try {
				while (" ".equals(chessBoard.get(position / 8 + temp * i,
						position % 8))) {
					temp++;
				}
				if ("r".equals(chessBoard.get(position / 8 + temp * i,
						position % 8))
						|| "q".equals(chessBoard.get(position / 8 + temp * i,
								position % 8))) {
					return false;
				}
			} catch (Exception e) {
			}
			temp = 1;
		}
		// knight
		for (int i = -1; i <= 1; i += 2) {
			for (int j = -1; j <= 1; j += 2) {
				try {
					if ("k".equals(chessBoard.get(position / 8 + i, position
							% 8 + j * 2))) {
						return false;
					}
				} catch (Exception e) {
				}
				try {

					if ("k".equals(chessBoard.get(position / 8 + i * 2,
							position % 8 + j))) {
						return false;
					}
				} catch (Exception e) {
				}
			}
		}
		// pawn
		if (position >= 16) {
			try {
				if ("p".equals(chessBoard.get(position / 8 - 1,
						position % 8 - 1))) {
					return false;
				}
			} catch (Exception e) {
			}
			try {
				if ("p".equals(chessBoard.get(position / 8 - 1,
						position % 8 + 1))) {
					return false;
				}
			} catch (Exception e) {
			}
			// king
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					if (i != 0 || j != 0) {
						try {
							if ("a".equals(chessBoard.get(position / 8 + i,
									position % 8 + j))) {
								return false;
							}
						} catch (Exception e) {
						}
					}
				}
			}
		}
		return true;
	}
}