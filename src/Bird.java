import java.awt.*;

public class Bird {
    int birdX;
    int birdY;
    int birdWidth;
    int birdHeight;
    Image img;

    int velocityY=0;
    int gravity = 1;

    public Bird(int birdX, int birdY, int birdWidth, int birdHeight, Image img) {
        this.birdX = birdX;
        this.birdY = birdY;
        this.birdWidth = birdWidth;
        this.birdHeight = birdHeight;
        this.img = img;
    }

    public void update(){
        velocityY += gravity;
        birdY += velocityY;
        birdY = Math.max(birdY,0);
    }

    public void jump(){
        velocityY = -9;
    }

}
