import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Pawn extends ChessPiece{
	
	private static int baseY;
	
	public static BufferedImage whitePiece;
	public static BufferedImage blackPiece;
	public static boolean setPicYet = false;
	
	public Pawn(int x, int y, boolean white) {
		super(x, y, 1 + (white? 0: 6), 1, white, -1);
	}
	
	public Pawn(int x, int y, boolean white, int index) {
		super(x, y, 1 + (white? 0: 6), 1, white, index);
	}
	
	public void setPic() {
		if (!setPicYet) {
			File f = new File("images/white_pawn.png");
			try {
				whitePiece = ImageIO.read(f);
			} catch (IOException exc) {
			}
			f = new File("images/black_pawn.png");
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
		ChessPiece hold = new Pawn(x, y, white, index);
		hold.ID = ID;
		totalPieces--;
		hold.moved = moved;
		hold.movedTwo = movedTwo;
		return hold;
	}
	
	public ArrayList<Integer> getMoves(int kx, int ky, int[][] board, ArrayList<ChessPiece> pieces) {
		ArrayList<Integer> moves = new ArrayList<Integer>();
		int movement = white? 1: -1;
		if (y < 7 && board[y+movement][x] == 0) {
			moves.add(x);
			moves.add(y+movement);
		}
		if (!moved && (white? y < 6: y > 1) && board[y + movement * 2][x] == 0 && board[y + movement][x] == 0) {
			//System.out.println(moved + " : " + x + " : " + y);
			moves.add(x);
			moves.add(y+movement * 2);
		}
		if (x < 7 && y < 7 && (white? board[y+movement][x+1] >= 7: (board[y+movement][x+1] > 0 && board[y+movement][x+1] < 7))) {
			moves.add(x+1);
			moves.add(y+movement);
		}
		if (x < 7 && y < 7 && (white? board[y][x+1] == 7: board[y][x+1] == 1)) {//french move
			int j;
			for (j = 0; j < pieces.size(); j++) {
				if (pieces.get(j).x == x+1 && pieces.get(j).y == y)
					break;
			}
			if (pieces.get(j).movedTwo) {
				moves.add(x+1);
				moves.add(y+movement);
			}
		}
		if (x > 0 && y < 7 && board[y+movement][x-1] > 0 && (white? board[y+movement][x-1] >= 7: (board[y+movement][x-1] > 0 && board[y+movement][x-1] < 7))) {
			moves.add(x-1);
			moves.add(y+movement);
		}
		if (x > 0 && y < 7 && (white? board[y][x-1] == 7: board[y][x-1] == 1)) {//french move
			int j;
			for (j = 0; j < pieces.size(); j++) {
				if (pieces.get(j).x == x-1 && pieces.get(j).y == y)
					break;
			}
			if (pieces.get(j).movedTwo) {
				moves.add(x-1);
				moves.add(y+movement);
			}
		}
		filterMoves(kx, ky, board, pieces, moves, false);
		
		return moves;
	}
	
	public boolean checking(int kx, int ky, int[][] board) {
		int movement = white? 1: -1;
		if (x < 7 && y < 7 && y + movement == ky && x + 1 == kx)
			return true;
		if (x > 0 && y < 7 && y + movement == ky && x - 1 == kx)
			return true;
		
		return false;
	}

}
