import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Alien extends ImageView {
    private int typeAlien;
    public Alien() {
        Image shipImage = new Image("/images/enemy1.png");
        Image bulletImage = new Image("/images/enemy1.png");
        this.setImage(shipImage);
        this.setFitWidth(40);
        this.setFitHeight(30);
        this.setPreserveRatio(true);
    }

    public void changeImage(int typeAlien){
        if (typeAlien == 2){
            Image s = new Image("/images/enemy2.png");
            this.setImage(s);
        }else if (typeAlien == 3){
            Image s = new Image("/images/enemy3.png");
            this.setImage(s);
        }
    }

    public int getType(){
        return typeAlien;
    }

}
