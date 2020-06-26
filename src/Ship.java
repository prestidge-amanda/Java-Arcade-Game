import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Node;

public class Ship extends ImageView {
    public Ship(){
        Image shipImage = new Image("/images/player.png");
        this.setImage(shipImage);
        this.setFitWidth(70);
        this.setFitHeight(40);
        this.setPreserveRatio(true);
        this.setX(415);
        this.setY(550);
    }

    public void startPosn(){
        this.setX(415);
        this.setY(550);
        this.setTranslateX(0);
        this.setTranslateY(0);
    }

}
