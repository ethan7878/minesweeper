

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

import java.util.*;

public class Main extends Applet implements MouseListener {
	
	int w, h, scale, mineCount, mW, discovered;
	boolean dead, won;
	Rectangle[] buttons;
	
	Square[][] grid;
	
	public void init() {
		
		this.w = 10;
		this.h = 10;
		this.scale = 90;
		this.mineCount = 10;
		this.mW = 300;
		this.discovered = 0;
		this.dead = false;
		this.won = false;
		
		int gridW = this.w * this.scale;
		int gridH = this.h * this.scale;
		
		Rectangle[] buttonArray = {new Rectangle(gridW + 20, 20, 260, 60),
								   new Rectangle(gridW + 20, 100, 260, 60)
								   };
		
		this.buttons = buttonArray;
		
		setSize(gridW + this.mW, gridH);
		setBackground(Color.BLACK);
		
		this.createGrid();
		this.placeMines();
		
		addMouseListener(this);
	}
	
	public void paint(Graphics g) {
		
		g.setFont(new Font("Courier", Font.PLAIN, this.scale / 2));
		
		if (this.dead)
			this.showAll();
		
		this.drawGrid(g);
		this.drawMenu(g);
		
		if (this.dead)
			this.drawState(g, "died");
		
		if (this.won) {
			this.drawState(g, "won");
		}
		
	}

	public void createGrid() {
		this.grid = new Square[w + 2][h + 2];
		
		for (int x=0; x<this.w + 2; x++) {
			for (int y=0; y<this.h + 2; y++) {
				this.grid[x][y] = new Square(x, y);
			}
		}
	}
	
	public void placeMines() {
		
		Random rng = new Random();
		
		for (int i=0; i<this.mineCount; i++) {
			
			int x = rng.nextInt(this.w) + 1;
			int y = rng.nextInt(this.h) + 1;
			
			if (this.grid[x][y].mine == 0) {
				this.grid[x][y].mine = 1;
			} else {
				i--;
			}
			
		}
		
		for (int x=0; x<this.w; x++) {
			for (int y=0; y<this.h; y++) {
				this.grid[x + 1][y + 1].countMines(this.grid);
			}
		}
		
	}
	
	public void discoverSquare(int x, int y) {
		
		if (x <= 0 || y <= 0 || x >= this.w + 1 || y >= this.h + 1)
			return;
		if (this.grid[x][y].shown)
			return;
		
		this.grid[x][y].shown = true;
		this.discovered++;
		
		if (this.grid[x][y].mine == 1)
			this.dead = true;
		else if (this.discovered == this.w * this.h - this.mineCount)
			this.won = true;
		
		if (this.grid[x][y].count == 0) {
			this.discoverSquare(x - 1, y - 1);
			this.discoverSquare(x - 1, y + 1);
			this.discoverSquare(x + 1, y - 1);
			this.discoverSquare(x + 1, y + 1);
			this.discoverSquare(x - 1, y);
			this.discoverSquare(x + 1, y);
			this.discoverSquare(x, y - 1);
			this.discoverSquare(x, y + 1);
		}
		
	}
	
	public void flag(int x, int y) {
		if (x <= 0 || y <= 0 || x >= this.w + 1 || y >= this.h + 1)
			return;
		if (this.grid[x][y].shown)
			return;
		
		this.grid[x][y].flag = !this.grid[x][y].flag;
	}
	
	public void showAll() {
		for (int x=0; x<this.w + 2; x++) {
			for (int y=0; y<this.h + 2; y++) {
				this.grid[x][y].shown = true;
			}
		}
	}
	
	public boolean inRect(Rectangle r, int x, int y) {
		
		return (r.x <= x && x <= r.x + r.width && r.y <= y && y <= r.y + r.height);
		
	}

	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
	public void mouseClicked(MouseEvent e) {
		
		int xS = e.getX();
		int yS = e.getY();
		
		int x = xS / this.scale;
		int y = yS / this.scale;
		
		int button = e.getButton();
		
		if (x <= this.w) {
			if (button == 1)
				discoverSquare(x + 1, y + 1);
			else if (button == 3)
				this.flag(x + 1, y + 1);
		}
		
		else if (this.inRect(this.buttons[0], xS, yS)) {
			this.createGrid();
			this.placeMines();
			this.dead = false;
			this.won  = false;
			this.discovered = 0;
		}
		
		repaint();
		
		e.consume();
		
	}
	
	public void drawGrid(Graphics g) {
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		for (int x=0; x<this.w; x++) {
			for (int y=0; y<this.h; y++) {
				
				if (!this.grid[x + 1][y + 1].shown)
					g.setColor(Color.GRAY);
				else if (this.grid[x + 1][y + 1].mine == 1)
					g.setColor(Color.RED);
				else
					g.setColor(Color.BLUE);
				
				g.fillRect((int) ((x + 0.05) * this.scale), (int) ((y + 0.05) * this.scale), 
						   (int) (this.scale * 0.9), (int) (this.scale * 0.9));
				
				String s = "";
				if (this.grid[x + 1][y + 1].shown) {
					s = (this.grid[x + 1][y + 1].count == 0)? " " : this.grid[x + 1][y + 1].count + "";
					s = (s.equals("-1"))? "*" : s;
				} else if (this.grid[x + 1][y + 1].flag) {
					s = "F";
				}
				
				g.setColor(Color.WHITE);
				g.drawString(s, (int) ((x + 0.33) * this.scale), (int) ((y + 0.7) * this.scale));
			}
		}
		
	}
	
	private void drawMenu(Graphics g) {
		
		g.setColor(Color.GREEN);
		
		for (int i=0; i<this.buttons.length; i++) {
			Rectangle r = this.buttons[i];
			g.fillRect(r.x, r.y, r.width, r.height);
		}
		
		g.setColor(Color.BLACK);
		g.drawString("New Game", this.buttons[0].x + 25, this.buttons[0].y + 45);
		
	}
	
	public void drawState(Graphics g, String state) {
		
		g.setColor(Color.BLACK);
		g.drawString("You " + state + "!", this.buttons[1].x + 15, this.buttons[1].y + 45);
		
	}

}