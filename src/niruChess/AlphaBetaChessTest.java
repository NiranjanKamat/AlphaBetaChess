package niruChess;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

public class AlphaBetaChessTest {

	@Test
	public void possibleKTest() {
		String chessBoard[][] = readState("misc/testData/KnightBugConfig.txt");
		AlphaBetaChess.printBoard(chessBoard);
		assertEquals(chessBoard[6][3], "q");
		System.out.println(AlphaBetaChess.posibleK(57, chessBoard));

	}

	private String[][] readState(String path) {
		String[][] chessBoard = new String[8][8];
		try {
			List<String> lines = Files.readAllLines(Paths.get(path),
					Charset.defaultCharset());
			for (int i = 0; i < lines.size(); i++) {
				if (!lines.get(i).startsWith("#")) {// #indicates a comment line
					String[] row = getRow(lines.get(i));
					chessBoard[i] = row;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return chessBoard;
	}

	private String[] getRow(String row) {
		String withoutEndBrackets = row.substring(1, row.length() - 1);
		String[] individualPieces = withoutEndBrackets.split(",");
		for (int i = 0; i < individualPieces.length; i++) {
			if (individualPieces[i].length() == 2) {
				individualPieces[i] = individualPieces[i].substring(1);
			}
		}
		return individualPieces;
	}
}
