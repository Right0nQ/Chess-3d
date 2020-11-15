import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class King extends ChessPiece{
	
	private static int baseY;
	
	public static BufferedImage whitePiece;
	public static BufferedImage blackPiece;
	public static boolean setPicYet = false;
	
	public King(int x, int y, boolean white) {
		super(x, y, 6 + (white? 0: 6), 100, white, -1);
	}
	
	public King(int x, int y, boolean white, int index) {
		super(x, y, 6 + (white? 0: 6), 100, white, index);
	}
	
	public void setPic() {
		if (!setPicYet) {
			File f = new File("images/white_king.png");
			try {
				whitePiece = ImageIO.read(f);
			} catch (IOException exc) {
			}
			f = new File("images/black_king.png");
			try {
				blackPiece = ImageIO.read(f);
			} catch (IOException exc) {
			}
			baseY = whitePiece.getHeight() - 7;//assumes both colors same dimensions
			setPicYet = true;
		}
	}
	
	public void draw(Graphics g) {
		int width = (int) ((white? whitePiece: blackPiece).getWidth() / drawZ);
		int height = (int) ((white? whitePiece: blackPiece).getHeight() / drawZ);
		g.drawImage((white? whitePiece: blackPiece), (int) drawX - width / 2, (int) drawY - (int) (baseY * ((double) height / whitePiece.getHeight())), width, height, null);
	}
	
	public ChessPiece copy(int index) {
		ChessPiece hold = new King(x, y, white, index);
		hold.ID = ID;
		totalPieces--;
		hold.moved = moved;
		hold.movedTwo = movedTwo;
		return hold;
	}

	public ArrayList<Integer> getMoves(int kx, int ky, int[][] board, ArrayList<ChessPiece> pieces) {
		ArrayList<Integer> moves = new ArrayList<Integer>();
		for (int row = Math.max(0, y - 1); row <= Math.min(7, y + 1); row++) {
			for (int col = Math.max(0, x - 1); col <= Math.min(7, x + 1); col++) {
				if (row != y || col != x) {
					if (board[row][col] == 0 || (white? board[row][col] >= 7: (board[row][col] > 0 && board[row][col] < 7))) {
						moves.add(col);
						moves.add(row);
					}
				}
			}
		}
		

		filterMoves(x, y, board, pieces, moves, true);
		
		if (!moved && board[y][7] == (white? 2: 8)) {
			
			int j;
			for (j = 0; j < pieces.size(); j++) {
				if (pieces.get(j).x == 7 && pieces.get(j).y == y)
					break;
			}
			
			if (!pieces.get(j).moved) {
				boolean exit = false;
				ArrayList<Integer> castleChecks = new ArrayList<Integer>();
				for (int i = 0; i <= 2; i++) {
					castleChecks.add(x+i);
					castleChecks.add(y);
					
					if (i != 0 && board[y][x+i] != 0)
						exit = true;
					
				}
				
				if (!exit) {
					filterMoves(x, y, board, pieces, castleChecks, true);
					
					if (castleChecks.size() == 6) {
						moves.add(x + 2);
						moves.add(y);
					}
				}
			}		
		}
		
		if (!moved && board[y][0] == (white? 2: 8)) {
			
			int j;
			for (j = 0; j < pieces.size(); j++) {
				if (pieces.get(j).x == 0 && pieces.get(j).y == y)
					break;
			}
			
			//System.out.println(board[y][0]);
			
			if (!pieces.get(j).moved) {
				boolean exit = false;
				ArrayList<Integer> castleChecks = new ArrayList<Integer>();
				for (int i = 0; i <= 3; i++) {
					if (i <= 2) {
						castleChecks.add(x-i);
						castleChecks.add(y);
					}
					
					if (i != 0 && board[y][x-i] != 0) {
						exit = true;
					}
					
				}
				
				if (!exit) {
					filterMoves(x, y, board, pieces, castleChecks, true);
					
					if (castleChecks.size() == 6) {
						moves.add(x - 2);
						moves.add(y);
					}
				}
			}		
		}
		
		return moves;
	}
	
	public boolean checking(int kx, int ky, int[][] board) {
		for (int row = Math.max(0, y - 1); row <= Math.min(7, y + 1); row++) {
			for (int col = Math.max(0, x - 1); col <= Math.min(7, x + 1); col++) {
				if (row != y || col != x) {
					if (row == ky && col == kx) {
						return true;
					}
				}
			}
		}
		return false;
	}
}