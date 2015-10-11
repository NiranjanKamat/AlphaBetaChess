package niruChess;

import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class AlphaBetaChess {
	static String chessBoard[][] = {
			{ "r", "k", "b", "q", "a", "b", "k", "r" },
			{ "p", "p", "p", "p", "p", "p", "p", "p" },
			{ " ", " ", " ", " ", " ", " ", " ", " " },
			{ " ", " ", " ", " ", " ", " ", " ", " " },
			{ " ", " ", " ", " ", " ", " ", " ", " " },
			{ " ", " ", " ", " ", " ", " ", " ", " " },
			{ "P", "P", "P", "P", "P", "P", "P", "P" },
			{ "R", "K", "B", "Q", "A", "B", "K", "R" } };
	static int kingPositionC, kingPositionL;
	static int humanAsWhite = -1;// 1=human as white, 0=human as black
	static int globalDepth = 2;
	static boolean kingCMoved = false, kingLMoved = false;
	static boolean rook56Moved = false, rook7Moved = false;
	static boolean rook63Moved = false, rook0Moved = false;

	public static void main(String[] args) {
		while (!"A".equals(chessBoard[kingPositionC / 8][kingPositionC % 8])) {
			kingPositionC++;
		}// get King's location
		while (!"a".equals(chessBoard[kingPositionL / 8][kingPositionL % 8])) {
			kingPositionL++;
		}
		// get king's location
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
		// System.out.println(sortMoves(posibleMoves()));
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
			temp = switchCase(chessBoard[r][c]);
			chessBoard[r][c] = switchCase(chessBoard[7 - r][7 - c]);
			chessBoard[7 - r][7 - c] = temp;
		}

		int positionTemp = kingPositionC;
		kingPositionC = 63 - kingPositionL;
		kingPositionL = 63 - positionTemp;

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
			chessBoard[1][Character.getNumericValue(move.charAt(0))] = " ";
			chessBoard[0][Character.getNumericValue(move.charAt(1))] = String
					.valueOf(move.charAt(3));
		} else {
			chessBoard[Character.getNumericValue(move.charAt(2))][Character
					.getNumericValue(move.charAt(3))] = chessBoard[Character
					.getNumericValue(move.charAt(0))][Character
					.getNumericValue(move.charAt(1))];
			chessBoard[Character.getNumericValue(move.charAt(0))][Character
					.getNumericValue(move.charAt(1))] = " ";
			if ("A".equals(chessBoard[Character.getNumericValue(move.charAt(2))][Character
					.getNumericValue(move.charAt(3))])) {
				kingPositionC = 8 * Character.getNumericValue(move.charAt(2))
						+ Character.getNumericValue(move.charAt(3));
			}
		}
	}

	public static void undoMove(String move) {
		if (move.charAt(4) == 'Z') {
			uncastleRight();
		} else if (move.charAt(4) == 'Y') {
			uncastleLeft();
		} else if (move.charAt(4) == 'P') {
			// if pawn promotion
			chessBoard[1][Character.getNumericValue(move.charAt(0))] = "P";
			chessBoard[0][Character.getNumericValue(move.charAt(1))] = String
					.valueOf(move.charAt(2));
		} else {
			chessBoard[Character.getNumericValue(move.charAt(0))][Character
					.getNumericValue(move.charAt(1))] = chessBoard[Character
					.getNumericValue(move.charAt(2))][Character
					.getNumericValue(move.charAt(3))];
			chessBoard[Character.getNumericValue(move.charAt(2))][Character
					.getNumericValue(move.charAt(3))] = String.valueOf(move
					.charAt(4));
			if ("A".equals(chessBoard[Character.getNumericValue(move.charAt(0))][Character
					.getNumericValue(move.charAt(1))])) {
				kingPositionC = 8 * Character.getNumericValue(move.charAt(0))
						+ Character.getNumericValue(move.charAt(1));
			}
		}
	}

	public static String posibleMoves() {
		String list = "";
		for (int i = 0; i < 64; i++) {
			switch (chessBoard[i / 8][i % 8]) {
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

	public static String posibleP(int i) {
		String list = "", oldPiece;
		int r = i / 8, c = i % 8;
		for (int j = -1; j <= 1; j += 2) {
			try {// capture
				if (Character.isLowerCase(chessBoard[r - 1][c + j].charAt(0))
						&& i >= 16) {
					oldPiece = chessBoard[r - 1][c + j];
					chessBoard[r][c] = " ";
					chessBoard[r - 1][c + j] = "P";
					if (kingSafe()) {
						list = list + r + c + (r - 1) + (c + j) + oldPiece;
					}
					chessBoard[r][c] = "P";
					chessBoard[r - 1][c + j] = oldPiece;
				}
			} catch (Exception e) {
			}
			try {// promotion && capture
				if (Character.isLowerCase(chessBoard[r - 1][c + j].charAt(0))
						&& i < 16) {
					String[] temp = { "Q", "R", "B", "K" };
					for (int k = 0; k < 4; k++) {
						oldPiece = chessBoard[r - 1][c + j];
						chessBoard[r][c] = " ";
						chessBoard[r - 1][c + j] = temp[k];
						if (kingSafe()) {
							// column1,column2,captured-piece,new-piece,P
							list = list + c + (c + j) + oldPiece + temp[k]
									+ "P";
						}
						chessBoard[r][c] = "P";
						chessBoard[r - 1][c + j] = oldPiece;
					}
				}
			} catch (Exception e) {
			}
		}
		try {// move one up
			if (" ".equals(chessBoard[r - 1][c]) && i >= 16) {
				oldPiece = chessBoard[r - 1][c];
				chessBoard[r][c] = " ";
				chessBoard[r - 1][c] = "P";
				if (kingSafe()) {
					list = list + r + c + (r - 1) + c + oldPiece;
				}
				chessBoard[r][c] = "P";
				chessBoard[r - 1][c] = oldPiece;
			}
		} catch (Exception e) {
		}
		try {// promotion && no capture
			if (" ".equals(chessBoard[r - 1][c]) && i < 16) {
				String[] temp = { "Q", "R", "B", "K" };
				for (int k = 0; k < 4; k++) {
					oldPiece = chessBoard[r - 1][c];
					chessBoard[r][c] = " ";
					chessBoard[r - 1][c] = temp[k];
					if (kingSafe()) {
						// column1,column2,captured-piece,new-piece,P
						list = list + c + c + oldPiece + temp[k] + "P";
					}
					chessBoard[r][c] = "P";
					chessBoard[r - 1][c] = oldPiece;
				}
			}
		} catch (Exception e) {
		}
		try {// move two up
			if (" ".equals(chessBoard[r - 1][c])
					&& " ".equals(chessBoard[r - 2][c]) && i >= 48) {
				oldPiece = chessBoard[r - 2][c];
				chessBoard[r][c] = " ";
				chessBoard[r - 2][c] = "P";
				if (kingSafe()) {
					list = list + r + c + (r - 2) + c + oldPiece;
				}
				chessBoard[r][c] = "P";
				chessBoard[r - 2][c] = oldPiece;
			}
		} catch (Exception e) {
		}
		return list;
	}

	public static String posibleR(int i) {
		String list = "", oldPiece;
		int r = i / 8, c = i % 8;
		int temp = 1;
		for (int j = -1; j <= 1; j += 2) {
			try {
				while (" ".equals(chessBoard[r][c + temp * j])) {
					oldPiece = chessBoard[r][c + temp * j];
					chessBoard[r][c] = " ";
					chessBoard[r][c + temp * j] = "R";
					if (kingSafe()) {
						list = list + r + c + r + (c + temp * j) + oldPiece;
					}
					chessBoard[r][c] = "R";
					chessBoard[r][c + temp * j] = oldPiece;
					temp++;
				}
				if (Character
						.isLowerCase(chessBoard[r][c + temp * j].charAt(0))) {
					oldPiece = chessBoard[r][c + temp * j];
					chessBoard[r][c] = " ";
					chessBoard[r][c + temp * j] = "R";
					if (kingSafe()) {
						list = list + r + c + r + (c + temp * j) + oldPiece;
					}
					chessBoard[r][c] = "R";
					chessBoard[r][c + temp * j] = oldPiece;
				}
			} catch (Exception e) {
			}
			temp = 1;
			try {
				while (" ".equals(chessBoard[r + temp * j][c])) {
					oldPiece = chessBoard[r + temp * j][c];
					chessBoard[r][c] = " ";
					chessBoard[r + temp * j][c] = "R";
					if (kingSafe()) {
						list = list + r + c + (r + temp * j) + c + oldPiece;
					}
					chessBoard[r][c] = "R";
					chessBoard[r + temp * j][c] = oldPiece;
					temp++;
				}
				if (Character
						.isLowerCase(chessBoard[r + temp * j][c].charAt(0))) {
					oldPiece = chessBoard[r + temp * j][c];
					chessBoard[r][c] = " ";
					chessBoard[r + temp * j][c] = "R";
					if (kingSafe()) {
						list = list + r + c + (r + temp * j) + c + oldPiece;
					}
					chessBoard[r][c] = "R";
					chessBoard[r + temp * j][c] = oldPiece;
				}
			} catch (Exception e) {
			}
			temp = 1;
		}
		return list;
	}

	public static String possibleKnight(int i) {
		String list = "", oldPiece;
		int r = i / 8, c = i % 8;
		for (int j = -1; j <= 1; j += 2) {
			for (int k = -1; k <= 1; k += 2) {
				try {
					if (Character.isLowerCase(chessBoard[r + j][c + k * 2]
							.charAt(0))
							|| " ".equals(chessBoard[r + j][c + k * 2])) {
						oldPiece = chessBoard[r + j][c + k * 2];
						chessBoard[r][c] = " ";
						chessBoard[r + j][c + k * 2] = "K";
						if (kingSafe()) {
							list = list + r + c + (r + j) + (c + k * 2)
									+ oldPiece;
						}
						chessBoard[r][c] = "K";
						chessBoard[r + j][c + k * 2] = oldPiece;
					}
				} catch (Exception e) {
				}
				try {
					if (Character.isLowerCase(chessBoard[r + j * 2][c + k]
							.charAt(0))
							|| " ".equals(chessBoard[r + j * 2][c + k])) {
						oldPiece = chessBoard[r + j * 2][c + k];
						chessBoard[r][c] = " ";
						chessBoard[r + j * 2][c + k] = "K";
						if (kingSafe()) {
							list = list + r + c + (r + j * 2) + (c + k)
									+ oldPiece;
						}
						chessBoard[r][c] = "K";
						chessBoard[r + j * 2][c + k] = oldPiece;
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
		int temp = 1;
		for (int j = -1; j <= 1; j += 2) {
			for (int k = -1; k <= 1; k += 2) {
				try {
					while (" ".equals(chessBoard[r + temp * j][c + temp * k])) {
						oldPiece = chessBoard[r + temp * j][c + temp * k];
						chessBoard[r][c] = " ";
						chessBoard[r + temp * j][c + temp * k] = "B";
						if (kingSafe()) {
							list = list + r + c + (r + temp * j)
									+ (c + temp * k) + oldPiece;
						}
						chessBoard[r][c] = "B";
						chessBoard[r + temp * j][c + temp * k] = oldPiece;
						temp++;
					}
					if (Character.isLowerCase(chessBoard[r + temp * j][c + temp
							* k].charAt(0))) {
						oldPiece = chessBoard[r + temp * j][c + temp * k];
						chessBoard[r][c] = " ";
						chessBoard[r + temp * j][c + temp * k] = "B";
						if (kingSafe()) {
							list = list + r + c + (r + temp * j)
									+ (c + temp * k) + oldPiece;
						}
						chessBoard[r][c] = "B";
						chessBoard[r + temp * j][c + temp * k] = oldPiece;
					}
				} catch (Exception e) {
				}
				temp = 1;
			}
		}
		return list;
	}

	public static String posibleQ(int i) {
		String list = "", oldPiece;
		int r = i / 8, c = i % 8;
		int temp = 1;
		for (int j = -1; j <= 1; j++) {
			for (int k = -1; k <= 1; k++) {
				if (j != 0 || k != 0) {
					try {
						while (" "
								.equals(chessBoard[r + temp * j][c + temp * k])) {
							oldPiece = chessBoard[r + temp * j][c + temp * k];
							chessBoard[r][c] = " ";
							chessBoard[r + temp * j][c + temp * k] = "Q";
							if (kingSafe()) {
								list = list + r + c + (r + temp * j)
										+ (c + temp * k) + oldPiece;
							}
							chessBoard[r][c] = "Q";
							chessBoard[r + temp * j][c + temp * k] = oldPiece;
							temp++;
						}
						if (Character.isLowerCase(chessBoard[r + temp * j][c
								+ temp * k].charAt(0))) {
							oldPiece = chessBoard[r + temp * j][c + temp * k];
							chessBoard[r][c] = " ";
							chessBoard[r + temp * j][c + temp * k] = "Q";
							if (kingSafe()) {
								list = list + r + c + (r + temp * j)
										+ (c + temp * k) + oldPiece;
							}
							chessBoard[r][c] = "Q";
							chessBoard[r + temp * j][c + temp * k] = oldPiece;
						}
					} catch (Exception e) {
					}
					temp = 1;
				}
			}
		}
		return list;
	}

	public static String possibleKing(int i) {
		// System.out.println("kingCMoved " + kingCMoved);
		String list = "", oldPiece;
		int r = i / 8, c = i % 8;
		for (int j = 0; j < 9; j++) {
			if (j != 4) {
				try {
					if (Character.isLowerCase(chessBoard[r - 1 + j / 3][c - 1
							+ j % 3].charAt(0))
							|| " ".equals(chessBoard[r - 1 + j / 3][c - 1 + j
									% 3])) {
						oldPiece = chessBoard[r - 1 + j / 3][c - 1 + j % 3];
						chessBoard[r][c] = " ";
						chessBoard[r - 1 + j / 3][c - 1 + j % 3] = "A";
						int kingTemp = kingPositionC;
						kingPositionC = i + (j / 3) * 8 + j % 3 - 9;
						if (kingSafe()) {
							list = list + r + c + (r - 1 + j / 3)
									+ (c - 1 + j % 3) + oldPiece;
						}
						chessBoard[r][c] = "A";
						chessBoard[r - 1 + j / 3][c - 1 + j % 3] = oldPiece;
						kingPositionC = kingTemp;
					}
				} catch (Exception e) {
				}
			}
		}

		// Y is castling 7472. Z is castling 7476
		// TODO Checking and undoing
		// if (!kingCMoved) {
		// if (!rook56Moved && (chessBoard[7][0].equals("R"))
		// && (chessBoard[7][1].equals(" "))
		// && (chessBoard[7][2].equals(" "))
		// && (chessBoard[7][3].equals(" "))) {
		// castleLeft();
		// if (kingSafe()) {
		// list = list + "7472Y";
		// }
		// uncastleLeft();
		// }
		// if (!rook63Moved && (chessBoard[7][7].equals("R"))
		// && (chessBoard[7][6].equals(" "))
		// && (chessBoard[7][5].equals(" "))) {
		// castleRight();
		// if (kingSafe()) {
		// list = list + "7476Z";
		// }
		// uncastleRight();
		// }
		// }
		// need to add casting later
		return list;
	}

	static void castleLeft() {
		chessBoard[7][2] = "A";
		chessBoard[7][3] = "R";
		chessBoard[7][0] = " ";
		chessBoard[7][4] = " ";
		kingPositionC -= 2;
		kingCMoved = true;
		rook56Moved = true;
	}

	static void uncastleLeft() {
		chessBoard[7][2] = " ";
		chessBoard[7][3] = " ";
		chessBoard[7][0] = "R";
		chessBoard[7][4] = "A";
		kingPositionC += 2;
		kingCMoved = false;
		rook56Moved = false;
	}

	static void castleRight() {
		chessBoard[7][6] = "A";
		chessBoard[7][5] = "R";
		chessBoard[7][4] = " ";
		chessBoard[7][7] = " ";
		kingPositionC += 2;
		kingCMoved = true;
		rook63Moved = true;
	}

	static void uncastleRight() {
		chessBoard[7][5] = " ";
		chessBoard[7][6] = " ";
		chessBoard[7][7] = "R";
		chessBoard[7][4] = "A";
		kingPositionC -= 2;
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
		// bishop/queen
		int temp = 1;
		for (int i = -1; i <= 1; i += 2) {
			for (int j = -1; j <= 1; j += 2) {
				try {
					while (" "
							.equals(chessBoard[kingPositionC / 8 + temp * i][kingPositionC
									% 8 + temp * j])) {
						temp++;
					}
					if ("b".equals(chessBoard[kingPositionC / 8 + temp * i][kingPositionC
							% 8 + temp * j])
							|| "q".equals(chessBoard[kingPositionC / 8 + temp
									* i][kingPositionC % 8 + temp * j])) {
						return false;
					}
				} catch (Exception e) {
				}
				temp = 1;
			}
		}
		// rook/queen
		for (int i = -1; i <= 1; i += 2) {
			try {
				while (" ".equals(chessBoard[kingPositionC / 8][kingPositionC
						% 8 + temp * i])) {
					temp++;
				}
				if ("r".equals(chessBoard[kingPositionC / 8][kingPositionC % 8
						+ temp * i])
						|| "q".equals(chessBoard[kingPositionC / 8][kingPositionC
								% 8 + temp * i])) {
					return false;
				}
			} catch (Exception e) {
			}
			temp = 1;
			try {
				while (" "
						.equals(chessBoard[kingPositionC / 8 + temp * i][kingPositionC % 8])) {
					temp++;
				}
				if ("r".equals(chessBoard[kingPositionC / 8 + temp * i][kingPositionC % 8])
						|| "q".equals(chessBoard[kingPositionC / 8 + temp * i][kingPositionC % 8])) {
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
					if ("k".equals(chessBoard[kingPositionC / 8 + i][kingPositionC
							% 8 + j * 2])) {
						return false;
					}
				} catch (Exception e) {
				}
				try {
					if ("k".equals(chessBoard[kingPositionC / 8 + i * 2][kingPositionC
							% 8 + j])) {
						return false;
					}
				} catch (Exception e) {
				}
			}
		}
		// pawn
		if (kingPositionC >= 16) {
			try {
				if ("p".equals(chessBoard[kingPositionC / 8 - 1][kingPositionC % 8 - 1])) {
					return false;
				}
			} catch (Exception e) {
			}
			try {
				if ("p".equals(chessBoard[kingPositionC / 8 - 1][kingPositionC % 8 + 1])) {
					return false;
				}
			} catch (Exception e) {
			}
			// king
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					if (i != 0 || j != 0) {
						try {
							if ("a".equals(chessBoard[kingPositionC / 8 + i][kingPositionC
									% 8 + j])) {
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