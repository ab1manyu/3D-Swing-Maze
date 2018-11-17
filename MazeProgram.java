import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.Scanner;
import java.awt.GradientPaint;
import java.awt.Polygon;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class MazeProgram extends JPanel implements KeyListener, MouseListener {
    JFrame frame;
    String[][] mazeArray = new String[12][48];
    int x = 1, y = 1;
    int direction = 1;
    String directionS = "right";
    /*
    0 = up
    1 = right
    2 = down
    3 = left
    */
    int startX = 1;
    int startY = 1;
    int endX = 46;
    int endY = 10;
    int counterMoves = 0;
    boolean endScreen = false, mapPickup = false, LSDmode = false, compassMode = false;
    int compX = 24, compY = 1;
    int xx = 0, yy = 0;
    Color color;
	BufferedImage image, bgImage, compU, compR, compD, compL;

    //3D VARIABLES
    ArrayList < Ceiling > ceilingList = new ArrayList < Ceiling > ();
    ArrayList < Ceiling > trapList = new ArrayList < Ceiling > ();
    ArrayList < Ceiling > wallList = new ArrayList < Ceiling > ();

    public MazeProgram() {
        setBoard();
        frame = new JFrame();
        frame.add(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1450, 800);
        frame.setVisible(true);
        frame.addKeyListener(this);
        this.addMouseListener(this);
        MakeSound m = new MakeSound();
        m.playSound("music.wav");
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK); //this will set the background color
        g.drawImage(bgImage, 0, 0, null);

        //3D STUff
        setWalls();

        for (Ceiling c: ceilingList) {
           	g.setColor(Color.GRAY);
            g.fillPolygon(c.getPoly());
        }

        for (Ceiling c: trapList) {
            g.setColor(Color.LIGHT_GRAY);
            g.fillPolygon(c.getPoly());
        }
        for (Ceiling c: wallList) {
            g.setColor(Color.DARK_GRAY);
            g.fillPolygon(c.getPoly());
        }
        wallList.clear();
        if (x == 6 && y == 1){
			mapPickup = true;
		}
		if (x == endX && y == endY) {
			endScreen = true;
		}
		else endScreen = false;
		if(x == compX && y == compY){
			compassMode = true;
		}

		if(endScreen){
			g.setColor(Color.BLACK);
			g.fillRect(0,0,1450,800);
			Font newFont = new Font("Serif", Font.BOLD, 30);
			g.setFont(newFont);
			g.setColor(Color.WHITE);
			g.drawString("You have completed the maze in "+counterMoves+" moves. Congratualations!", 350, 250);
			g.drawString("Click \"R\" to restart the maze, or turn around and continue exploring.", 270, 280);
		}

        //2DMAP
        if(mapPickup){
		g.drawImage(image, 0, 0, null);
        g.setColor(Color.BLACK);
        for (int i = 0; i < mazeArray.length; i++) {
		color = new Color(0,0,0,240);
            for (int j = 0; j < mazeArray[0].length; j++) {
                if (mazeArray[i][j].equals(" ")) {
                    g.setColor(color);
                    g.fillRect(j * 5 +23, i * 5 + 6, 5, 5);
                    //g.setColor(Color.BLACK);
                    //g.drawRect(j * 5 + 0, i * 5 + 50, 5, 5);
                }
            }
        }
        //treasure
        g.setColor(Color.YELLOW);
		g.fillRect(144,11,4,5);


        //painting player
        g.setColor(Color.CYAN);
        g.fillOval(x * 5 + 23, y * 5+ 6, 5, 5);

		}
		if(compassMode){
			if(direction == 0)
				g.drawImage(compU, 1200, 0, null);
			if(direction == 1)
				g.drawImage(compR, 1200, 0, null);
			if(direction == 2)
				g.drawImage(compD, 1200, 0, null);
			if(direction == 3)
				g.drawImage(compL, 1200, 0, null);
		}

        //x & y would be used to located your playable character
        //values would be set below

        //other commands that might come in handy
        //g.setFont("Times New Roman",Font.PLAIN,18);

        /*creating box
        g.setColor(Color.BLACK);
        g.fillRect(50, 30, 481, 20);

        //information box text
        g.setColor(Color.WHITE);
        g.drawString("Position: " + x + " , " + y, 55, 45);
        g.drawString("Number of steps taken: " + counterMoves, 155, 45);
        g.drawString("You are facing " + directionS, 415, 45);
        if (LSDmode) g.drawString("LSD mode ON", 320, 45);

        //completion text
        g.setColor(Color.BLACK);
        if (x == endX && y == endY) {
            g.drawString("YOU FINISHED THE MAZE IN " + counterMoves + " MOVES!", 550, 65);
            if (counterMoves < 60) {
                g.drawString("NICE JOB!", 550, 80);
            } else if (counterMoves >= 60) {
                g.drawString("DID YOU GET LOST? TRY COMPLETING IT IN LESS STEPS.", 550, 80);
            }
            g.drawString("PRESS \"R\" TO BE MAGICALLY", 550, 105);
            g.drawString("TELPORTED BACK TO THE START!", 550, 120);
        }*/

        //you can also use Font.BOLD, Font.ITALIC, Font.BOLD|Font.Italic
        //g.drawOval(x,y,10,10);
        //g.fillRect(x,y,100,100);
        //g.fillOval(x,y,10,10);
    }
    public void setBoard() {
        //choose your maze design

        //pre-fill maze array here
        for (int i = 0; i < mazeArray.length; i++) {
            for (int j = 0; j < mazeArray[0].length; j++) {
                mazeArray[i][j] = "o";
            }
        }

        File name = new File("MazeProgram1.txt");
        int r = 0;
        try {

			image = ImageIO.read(new File("map.png"));
			bgImage = ImageIO.read(new File("bg.png"));
			compU = ImageIO.read(new File("compU.png"));
			compD = ImageIO.read(new File("compD.png"));
			compR = ImageIO.read(new File("compR.png"));
			compL = ImageIO.read(new File("compL.png"));

            BufferedReader input = new BufferedReader(new FileReader(name));
            String text;
            int counter = 0;

            //converts maze text file into a 2D array
            while ((text = input.readLine()) != null) {
                String[] starr = text.split("");

                for (int i = 0; i < mazeArray[counter].length; i++) {
                    mazeArray[counter][i] = starr[i];
                }

                counter++;
            }

            //prints out maze
            for (int i = 0; i < mazeArray.length; i++) {
                for (int j = 0; j < mazeArray[0].length; j++) {
                    System.out.print(mazeArray[i][j]);
                }
                System.out.println();
            }

        } catch (IOException io) {
            System.err.println("File error");
        }
    }

    public void setWalls() {
        //left
        for (int i = 0; i < 4; i++) {
            int r[] = {0,0,125,125}; //x
            int c[] = {100,700,700,100}; //y
            for (int j = 0; j < 4; j++) {
                r[j] += 125 * i;
                if (j == 1 || j == 2) {
                    c[j] -= 75 * i;
                } else {
                    c[j] += 75 * i;
                }
            }
            ceilingList.add(new Ceiling(r, c));
        }

        //right
        for (int i = 0; i < 4; i++) {
            int r[] = {1325,1325,1450,1450};
            int c[] = {100,700,700,100};
            for (int j = 0; j < 4; j++) {
                r[j] -= 125 * i;
                if (j == 0 || j == 3) {
                    c[j] += 75 * i;
                } else {
                    c[j] -= 75 * i;
                }
            }
            ceilingList.add(new Ceiling(r, c));
        }

        //left trapezoids
        //g.setColor(Color.RED);
        trapList = new ArrayList < Ceiling > ();
        for (int i = 0; i < 4; i++) {
            int r[] = {0,0,125,125};
            int c[] = {25,775,700,100}; //x
            for (int j = 0; j < 4; j++) {

                r[j] += 125 * i;
                if (j == 1 || j == 2) {
                    c[j] -= 75 * i;
                } else {
                    c[j] += 75 * i;
                }

                //UP
                if (direction == 0 && y - i >= 0 && x - 1 >= 0 && mazeArray[y - i][x - 1].equals("o"))
                    trapList.add(new Ceiling(r, c));
                //RIGHT
                if (direction == 1 && y - 1 >= 0 && x + i < mazeArray[0].length && mazeArray[y - 1][x + i].equals("o"))
                    trapList.add(new Ceiling(r, c));
                //DOWN
                if (direction == 2 && y + i < mazeArray.length && x + 1 < mazeArray[0].length && mazeArray[y + i][x + 1].equals("o"))
                    trapList.add(new Ceiling(r, c));
                //LEFT
                if (direction == 3 && y + 1 < mazeArray.length && x - i >= 0 && mazeArray[y + 1][x - i].equals("o"))
                    trapList.add(new Ceiling(r, c));
            }

        }
        //right trapezoids
        for (int i = 0; i < 4; i++) {
            int r[] = {1325,1325,1450,1450};
            int c[] = {100,700,775,25};
            for (int j = 0; j < 4; j++) {
                r[j] -= 125 * i;
                if (j == 0 || j == 3)
                    c[j] += 75 * i;
                else
                    c[j] -= 75 * i;
                //UP
                if (direction == 0 && y - i >= 0 && x + 1 < mazeArray[0].length && mazeArray[y - i][x + 1].equals("o")){
                    trapList.add(new Ceiling(r, c));
                    color = new Color(0,0,100+(50*i));
				}
                //RIGHT
                if (direction == 1 && y + 1 < mazeArray.length && x + i < mazeArray[0].length && mazeArray[y + 1][x + i].equals("o")){
                    trapList.add(new Ceiling(r, c));
                    color = new Color(0,0,100+(50*i));
				}
                //DOWN
                if (direction == 2 && y + i < mazeArray.length && x - 1 >= 0 && mazeArray[y + i][x - 1].equals("o")){
                    trapList.add(new Ceiling(r, c));
                    color = new Color(0,0,100+(50*i));
				}
                //LEFT
                if (direction == 3 && y - 1 >= 0 && x - i >= 0 && mazeArray[y - 1][x - i].equals("o")){
                    trapList.add(new Ceiling(r, c));
                    color = new Color(0,0,100+(50*i));
				}
            }

        }

        //center walls
        for (int i = 4; i >= 0; i--) {
            int r[] = {0,0,1450,1450}; //x (add/subtract 125)
            int c[] = {25,775,775,25}; //y (add/subtract 75)
            for (int j = 0; j < 4; j++) {
                if (j == 0) {
                    r[j] += 125 * i;
                    c[j] += 75 * i;
                }
                if (j == 1) {
                    r[j] += 125 * i;
                    c[j] -= 75 * i;
                }
                if (j == 2) {
                    r[j] -= 125 * i;
                    c[j] -= 75 * i;
                }
                if (j == 3) {
                    r[j] -= 125 * i;
                    c[j] += 75 * i;
                }
                //UP
                if (direction == 0 && y - i >= 0 && mazeArray[y - i][x].equals("o"))
                    wallList.add(new Ceiling(r, c));

                //RIGHT
                if (direction == 1 && x + i < mazeArray[0].length && mazeArray[y][x + i].equals("o"))
                    wallList.add(new Ceiling(r, c));
                //DOWN
                if (direction == 2 && y + i < mazeArray.length && mazeArray[y + i][x].equals("o"))
                    wallList.add(new Ceiling(r, c));
                //LEFT
                if (direction == 3 && x - i >= 0 && mazeArray[y][x - i].equals("o"))
                    wallList.add(new Ceiling(r, c));
            }
        }

    }

    public void keyPressed(KeyEvent ke) {
        //System.out.println(ke);
        //turning left
        if (ke.getKeyCode() == 37) {
            if (direction == 0) direction = 3;
            else direction--;
        }
        //turning right
        if (ke.getKeyCode() == 39) {
            if (direction == 3) direction = 0;
            else direction++;
        }
        //changes value of the directionS string
        switch (direction) {
            case 0:
                directionS = "up";
                break;
            case 1:
                directionS = "right";
                break;
            case 2:
                directionS = "down";
                break;
            default:
                directionS = "left";
                break;

        }
        //making character move in the direction it is looking in
        if (ke.getKeyCode() == 38) {
            //up
            if (direction == 0 && mazeArray[y - 1][x].equals(" ")) {
                y--;
                counterMoves++;
            }
            //right
            if (direction == 1 && mazeArray[y][x + 1].equals(" ")) {
                x++;
                counterMoves++;
            }
            //down
            if (direction == 2 && mazeArray[y + 1][x].equals(" ")) {
                y++;
                counterMoves++;
            }
            //left
            if (direction == 3 && mazeArray[y][x - 1].equals(" ")) {
                x--;
                counterMoves++;
            }
        }
        //reset position
        if (ke.getKeyCode() == 82) {
            x = 1;
            y = 1;
        }
        //toggle 'L' for LSD mode (makes screen go ballistic)
        if (ke.getKeyCode() == 76) {
            LSDmode = !LSDmode;
        }
        setWalls();
        repaint();
    }

	public class MakeSound {

	    private final int BUFFER_SIZE = 128000;
	    private File soundFile;
	    private AudioInputStream audioStream;
	    private AudioFormat audioFormat;
	    private SourceDataLine sourceLine;

	    /**
	     * @param filename the name of the file that is going to be played
	     */
	    public void playSound(String filename){

	        String strFilename = filename;

	        try {
	            soundFile = new File(strFilename);
	        } catch (Exception e) {
	            e.printStackTrace();
	            System.exit(1);
	        }

	        try {
	            audioStream = AudioSystem.getAudioInputStream(soundFile);
	        } catch (Exception e){
	            e.printStackTrace();
	            System.exit(1);
	        }

	        audioFormat = audioStream.getFormat();

	        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
	        try {
	            sourceLine = (SourceDataLine) AudioSystem.getLine(info);
	            sourceLine.open(audioFormat);
	        } catch (LineUnavailableException e) {
	            e.printStackTrace();
	            System.exit(1);
	        } catch (Exception e) {
	            e.printStackTrace();
	            System.exit(1);
	        }

	        sourceLine.start();

	        int nBytesRead = 0;
	        byte[] abData = new byte[BUFFER_SIZE];
	        while (nBytesRead != -1) {
	            try {
	                nBytesRead = audioStream.read(abData, 0, abData.length);
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	            if (nBytesRead >= 0) {
	                @SuppressWarnings("unused")
	                int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
	            }
	        }

	        sourceLine.drain();
	        sourceLine.close();
	    }
	}

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
    public void mouseClicked(MouseEvent e) {
        xx = e.getX();
        yy = e.getY();
        repaint();
    }
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public static void main(String args[]) {
        MazeProgram app = new MazeProgram();
    }
}
