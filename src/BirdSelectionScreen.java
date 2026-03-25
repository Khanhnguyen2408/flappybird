import java.awt.*;
import java.awt.event.KeyEvent;
import javax.swing.*;
public class BirdSelectionScreen {
    FlappyBird game;
    Image background;
    Image[] birds;
    int selectIndex =0;
    public BirdSelectionScreen(FlappyBird game){
        this.game = game;
        background = new ImageIcon(getClass().getResource("/img/flappybirdbg.png")).getImage();
        birds = new Image[]{
                new ImageIcon(getClass().getResource("/img/flappybird.png")).getImage(),
                new ImageIcon(getClass().getResource("/img/flappybirdblue.png")).getImage(),
                new ImageIcon(getClass().getResource("/img/flappybirdred.png")).getImage(),
        };
        selectIndex = game.selectBirdIndex;  // load chim cu neu khong chon
    }
    public void drawSelectBird(Graphics g){
        g.drawImage(background,0,0, game.boardWidth, game.boardHeight,null);
        g.setColor(Color.white);
        g.setFont(game.pixelFont.deriveFont(20f));
        g.drawString("Select Bird",80,200);
        //ve chim
        Image currentBird = birds[selectIndex];
        g.drawImage(currentBird,game.boardWidth/2-20,game.boardHeight/2-20,60,60,null);
        g.drawString("< >",game.boardWidth/2-20, game.boardHeight/2+60);
    }
    public void handleKey(KeyEvent e){
        if(e.getKeyCode()==KeyEvent.VK_LEFT){
            selectIndex--;
            if(selectIndex<0) selectIndex=birds.length-1;
        }
        else if(e.getKeyCode()==KeyEvent.VK_RIGHT){
            selectIndex++;
            if(selectIndex>=birds.length) selectIndex=0;
        } else if (e.getKeyCode()==KeyEvent.VK_ENTER) {
            game.selectBirdIndex=selectIndex;
            //;uu lai chim
            game.bird.img=birds[selectIndex];
            //vao game
            game.resetGame();
            game.gameState= FlappyBird.GameState.PLAYING;
        }
    }


}
