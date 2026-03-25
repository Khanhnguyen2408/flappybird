import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
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
    int selectedOption = 0; // lua chon o menu
    int selectedOption1 =0; // sau khi thua thi chon gi
    int selectedOption2 = 0; // lua chon o high score
    int countdown =4;// dem count down
    boolean isCountingDown = false;
    int highScore = 0;
    boolean newHighScore = false;

    // ket noi giua 2 class
    BirdSelectionScreen birdSelectionScreen;
    int selectBirdIndex=0;

    //so lương man
    enum GameState{
        MENU,
        PLAYING,
        SELECT_BIRD,
        HIGH_SCORE
    }
    GameState gameState = GameState.MENU;

    FlappyBird(){
        //tao contructor ket noi den man hinh bird
        birdSelectionScreen = new BirdSelectionScreen(this);

        setPreferredSize(new Dimension(boardWidth,boardHeight));
        //setBackground(Color.blue);
        setFocusable(true);
        addKeyListener(this);

        // load img
        backgroudImg = new ImageIcon(getClass().getResource("/img/flappybirdbg.png")).getImage();
        //birdImg = new ImageIcon(getClass().getResource("./img/flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("/img/toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("/img/bottompipe.png")).getImage();
        //load font
        try{
            pixelFont = Font.createFont(Font.TRUETYPE_FONT,
                    getClass().getResourceAsStream("/font/PressStart2P-Regular.ttf"))
                    .deriveFont(18f);
        }catch (Exception e){
            pixelFont = new Font("Arial",Font.PLAIN,18);
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
        //load highscore
        loadHighScore();
        //game timer
        //60 khung hinh 1 giay
        gameLoop = new Timer(1000/60, this);
        gameLoop.start();
    }
        // ham luu file
        public void saveHighScore(){
            try{
                FileWriter writer = new FileWriter("highscore.txt");
                writer.write(String.valueOf(highScore));
                writer.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        //ham load file
        public void loadHighScore(){
            try{
                File file = new File ("highscore.txt");
                if(file.exists()){
                    Scanner sc = new Scanner(file);
                    if(sc.hasNextLine()){
                        highScore = sc.nextInt();
                    }
                    sc.close();
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
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
        if(gameState==GameState.MENU){
            drawMenu(g);
        } else if (gameState==GameState.PLAYING) {
            drawPlaying(g);
        } else if (gameState==GameState.SELECT_BIRD) {
           birdSelectionScreen.drawSelectBird(g);
        } else if (gameState==GameState.HIGH_SCORE) {
            drawHighScore(g);
        }
    }
    //Ve draw menu
    void drawMenu(Graphics g){
        g.setFont(pixelFont);
        g.setColor(Color.white);
        g.drawString("FLAPPY BIRD", 70, 160);
        String[] options = {
                "Start Game",
                "Select Bird",
                "High Score"
        };
        int startX = 80;
        int startY = 250;
        int boxWidth = 250;
        int boxHeight = 40;
        for (int i=0; i< options.length;i++){
            int y = startY + i*60;
            //neu dang duoc chon
            if(i==selectedOption){
                g.setColor(Color.yellow);
                g.fillRect(startX-10,y-30,boxWidth,boxHeight);
                g.setColor(Color.black);
            }else{
                g.setColor(Color.white);
            }
            g.drawString(options[i],startX,y);
        }
    }
    // ve man
    public void drawPlaying(Graphics g){
        //bat dau choi
        g.drawImage(backgroudImg, 0, 0, boardWidth, boardHeight, null);
        // draw bird
        g.drawImage(bird.img,bird.birdX,bird.birdY,bird.birdWidth,bird.birdHeight,null);

        //ve dem thoi gian
        if(isCountingDown){
            g.setColor(Color.black);
            g.setFont(pixelFont.deriveFont(40F));
            String text = String.valueOf(countdown);
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int x = (boardWidth - textWidth) / 2;
            int y = boardHeight / 2;

            // viền đen
            g.drawString(text, x + 3, y + 3);
            // chữ trắng
            g.setColor(Color.WHITE);
            g.drawString(text, x, y);
            return;

        }

        // draw pipes
        for (int i = 0;i<pipes.size();i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img,pipe.x,pipe.y,pipe.width,pipe.height,null);
        }

        // score
        g.setColor(Color.white);
        g.setFont(pixelFont);
//        //hien neu co ki luc moi
//        if(newHighScore){
//
//        }
        if(gameOver){
            //kiem tra xem co lap ki luc moi khong
            if(highScore<score){
                highScore = (int)score;
                newHighScore = true;
                saveHighScore();
            }
            //hien neu co ki luc moi
            if (newHighScore) {
                g.setFont(pixelFont.deriveFont(20f));
                g.setColor(Color.YELLOW);

                String text = "NEW HIGH SCORE!";
                FontMetrics fm = g.getFontMetrics();
                int textWidth = fm.stringWidth(text);

                int x = (boardWidth - textWidth) / 2;

                g.drawString(text, x, 80);
            }
            //hien thi sau khi thua game
            g.setColor(Color.BLACK);
            g.drawString("Game Over: "+String.valueOf((int)score),boardWidth/8+2,boardHeight/2+2);
            g.setColor(Color.WHITE);
            g.drawString("Game Over: "+String.valueOf((int)score),boardWidth/8,boardHeight/2);
            int yOption =  boardHeight/2+40;
            if(selectedOption1==0){
                g.setColor(Color.black);
            }else{
                g.setColor(Color.yellow);
            }
            g.drawString("Again",boardWidth/2+5, yOption);
            if(selectedOption1==1){
                g.setColor(Color.black);
            }else{
                g.setColor(Color.yellow);
            }
            g.drawString("Menu", boardWidth/4-5, yOption);
        }
        else{
            g.setColor(Color.BLACK);
            g.drawString("Score: " + String.valueOf((int) score), 12, 37);
            g.setColor(Color.WHITE);
            g.drawString("Score: "+String.valueOf((int) score),10,35);
        }
    }

    //ve highscore
    public void drawHighScore(Graphics g){
        g.drawImage(backgroudImg, 0, 0, boardWidth, boardHeight,null);
        g.setFont(pixelFont.deriveFont(25F));
        g.setColor(Color.white);
        g.drawString("Flappy Bird",50,200);
        g.setFont(pixelFont.deriveFont(20F));
        g.drawString("HIGH SCORE",80, 220);
        g.drawString(String.valueOf(highScore),140,250);
        //nut bam
        String[] option = {"PLAY", "MENU"};
        int startX=100;
        int startY=350;
        int boxWidth=160;
        int boxHeight=40;
        for(int i=0;i<option.length;i++){
            int y =startY + i*70;
            if(i==selectedOption2){
                g.setColor(Color.yellow);
                g.fillRect(startX-10, y-30,boxWidth,boxHeight);
                g.setColor(Color.black);
            }else {
                g.setColor(Color.white);
            }
            g.drawString(option[i],startX,y);
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
        //move();

        if(gameOver){
            placePinesTimer.stop();
            gameLoop.stop();
        }
        if (gameState==GameState.PLAYING){
            if(isCountingDown){
                handleCountDown();
            }else{
                move();
            }
        }
        repaint();
    }
    //ham reset game
    public void resetGame(){
        bird.birdY = birdY;
        pipes.clear();
        score = 0;
        gameOver=false;
        gameLoop.start();
        placePinesTimer.start();
        countdown =3;
        isCountingDown = true;
        newHighScore = false;
    }
    //ham count down
    long lastTime = System.currentTimeMillis();
    public void handleCountDown(){
        long currentTime = System.currentTimeMillis();
        if(currentTime-lastTime>=1000){
            countdown--;
            lastTime=currentTime;
        }
        if(countdown<=0){
            isCountingDown=false;
            pipes.clear();
            placePinesTimer.start();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(gameState==GameState.PLAYING&&!isCountingDown){
            if(e.getKeyCode()==KeyEvent.VK_SPACE){
                bird.jump();
            }
        }
        else if(gameState==GameState.MENU){
            if(e.getKeyCode()==KeyEvent.VK_UP){
                selectedOption--;
                if(selectedOption<0) selectedOption=2;
                repaint();
            }
            if(e.getKeyCode()==KeyEvent.VK_DOWN){
                selectedOption++;
                if(selectedOption>2) selectedOption=0;
                repaint();
            }
            if(e.getKeyCode()==KeyEvent.VK_ENTER){
                if(selectedOption==0){
                    resetGame();
                    gameState=GameState.PLAYING;
                    repaint();
                } else if (selectedOption==1) {
                    gameState=GameState.SELECT_BIRD;
                    repaint();
                } else if (selectedOption==2) {
                    gameState=GameState.HIGH_SCORE;
                    repaint();
                }
            }
            System.out.println(selectedOption);
        }
        // xu ly o man highscore
        else if(gameState==GameState.HIGH_SCORE){
            if(e.getKeyCode()==KeyEvent.VK_UP){
                selectedOption2 = 0;
                repaint();
            } else if (e.getKeyCode()==KeyEvent.VK_DOWN) {
                selectedOption2 = 1;
                repaint();
            } else if (e.getKeyCode()==KeyEvent.VK_ENTER) {
                if(selectedOption2==0){
                    resetGame();
                    gameState=GameState.PLAYING;

                    handleCountDown();
                    repaint();
                }
                if(selectedOption2==1){
                    gameState=GameState.MENU;
                    repaint();
                }
            }
        }
        //xu ly o man chon chim
        else if (gameState==GameState.SELECT_BIRD) {
            birdSelectionScreen.handleKey(e);
            repaint();
        }
        if(gameOver){
            if(e.getKeyCode()==KeyEvent.VK_LEFT){
                selectedOption1 = 0;
                repaint();
            } else if (e.getKeyCode()==KeyEvent.VK_RIGHT) {
                selectedOption1 = 1;
                repaint();
            } else if (e.getKeyCode()==KeyEvent.VK_ENTER) {
                if(selectedOption1==1) resetGame();
                if(selectedOption1==0){
                    gameState=GameState.MENU;
                    gameOver=false;
                    repaint();
                }
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
}
