import java.awt.*;
import java.awt.event.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener{
    int boardWidth = 360;
    int boardHeight = 640;
    // images
    Image backgroudImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;
    //bird
    int birdX = boardWidth/8;
    int birdY = boardHeight/2;
    int birdWidth = 34;
    int birdHeight = 24;

    //pipe
    ArrayList<Pipe>pipes;
    int pipeWidth = 64;
    int pipeHeight = 512;
    int velocityX=-4; // toc do cua ong
    Random random = new Random();

    // game logic
    Bird bird;
    Timer gameLoop;
    Timer placePinesTimer;
    Font pixelFont;
    boolean gameOver = false;
    double score =0;
    FlappyBird(){
        setPreferredSize(new Dimension(boardWidth,boardHeight));
        //setBackground(Color.blue);
        setFocusable(true);
        addKeyListener(this);

        // load img
        backgroudImg = new ImageIcon(getClass().getResource("/img/flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./img/flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("/img/toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("/img/bottompipe.png")).getImage();
        //load font
        try{
            pixelFont = Font.createFont(Font.TRUETYPE_FONT,
                    getClass().getResourceAsStream("/font/PressStart2P-Regular.ttf"))
                    .deriveFont(24f);
        }catch (Exception e){
            pixelFont = new Font("Arial",Font.PLAIN,24);
        }
        //bird
        bird = new Bird(birdX,birdY,birdWidth,birdHeight,birdImg);
        pipes = new ArrayList<Pipe>();
        //place pipes timer
        placePinesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePine();
            }
        });
        placePinesTimer.start();
        //game timer
        //60 khung hinh 1 giay
        gameLoop = new Timer(1000/60, this);
        gameLoop.start();
    }

    public void placePine(){
        int randomPipeY = (int)(0-pipeHeight/4-Math.random()*(pipeHeight/2));
        int openingSpace = boardHeight/4;

        Pipe topPine = new Pipe(boardWidth,randomPipeY,pipeWidth,pipeHeight,topPipeImg);
        pipes.add(topPine);
        Pipe bottomPipe = new Pipe(boardWidth, randomPipeY + pipeHeight + openingSpace, pipeWidth, pipeHeight, bottomPipeImg);
        pipes.add(bottomPipe);

    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        g.drawImage(backgroudImg, 0, 0, boardWidth, boardHeight, null);

        // draw bird
        g.drawImage(bird.img,bird.birdX,bird.birdY,bird.birdWidth,bird.birdHeight,null);

        // draw pipes
        for (int i = 0;i<pipes.size();i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img,pipe.x,pipe.y,pipe.width,pipe.height,null);
        }

        // score
        g.setColor(Color.white);
        g.setFont(pixelFont);
        if(gameOver){
            g.setColor(Color.BLACK);
            g.drawString("Game Over: "+String.valueOf((int)score),boardWidth/8+2,boardHeight/2+2);
            g.setColor(Color.WHITE);
            g.drawString("Game Over: "+String.valueOf((int)score),boardWidth/8,boardHeight/2);
        }
        else{
            g.setColor(Color.BLACK);
            g.drawString("Score: " + String.valueOf((int) score), 12, 37);
            g.setColor(Color.WHITE);
            g.drawString("Score: "+String.valueOf((int) score),10,35);
        }
    }

    public void move(){
        // bird
        bird.update();

        //pipes
        for (int i =0 ;i<pipes.size();i++){
            Pipe pipe = pipes.get(i);
            pipe.x+=velocityX;
            if(!pipe.passed&&bird.birdX>pipe.x+pipe.width){
                pipe.passed=true;
                score+=0.5;
            }

            if(collision(bird, pipe)){
                gameOver = true;
            }
        }

        if(bird.birdY>boardHeight){
            gameOver=true;
        }
    }
    //ham va cham
    public boolean collision(Bird a, Pipe b){
        return a.birdX<b.x+b.width &&
                a.birdX+a.birdWidth>b.x &&
                a.birdY<b.y+b.height &&
                a.birdY+a.birdHeight>b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameOver){
            placePinesTimer.stop();
            gameLoop.stop();
        }

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_SPACE){
            bird.jump();
            if(gameOver){
                //reset game
                bird.birdY = birdY;
                pipes.clear();
                score = 0;
                gameOver=false;
                gameLoop.start();
                placePinesTimer.start();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
}
