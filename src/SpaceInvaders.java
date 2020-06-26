import javafx.application.Application;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.*;
import java.io.File;
import javafx.scene.input.KeyCode;
import javafx.scene.Group;
import javafx.application.Platform;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.animation.Transition;
import javafx.scene.control.Alert;
import javafx.scene.canvas.Canvas;

import java.util.Timer;
import java.util.concurrent.TimeUnit;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import java.util.Random;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.animation.AnimationTimer;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.event.EventHandler;


/* need to look into how to implement bullets and bullet intersection
* started to add a bullet array, might make a class of bullets */

public class SpaceInvaders extends Application {
    // Speeds
    private static final double PLAYER_SPEED =3.0;
    private static final double PLAYER_BULLET_SPEED =6.0;
    private static final double ENEMY_SPEED =0.5;
    private static final double ENEMY_VERTICAL_SPEED =3.0;
    private static final double ENEMY1_BULLET_SPEED = 4.0;
    private static final double ENEMY2_BULLET_SPEED = 5.0;
    private static final double ENEMY3_BULLET_SPEED = 6.0;
    private Ship player;
    private boolean alienMoveR = true;
    private Alien[] aliens = new Alien[50];
    private boolean pMoveLeft, pMoveRight;
    Statistics stats = new Statistics();

   public void start(Stage stage){
            // init variables
            Group root = new Group();
            Group root1 = new Group();
            Group root2 = new Group();
            Group playerBullets = new Group();
            Group alienBullets = new Group();
            Group alienGroup = new Group();
            Group textGroup = new Group();
            Scene welcomeScene = new Scene(root, 900, 600, Color.WHITE);
            final Canvas canvas= new Canvas(900,600);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            Scene level1 = new Scene(root1, 900,600, Color.BLACK);
            Scene restartScreen = new Scene(root2, 900,600, Color.WHITE);
            String sound = getClass().getClassLoader().getResource("sounds/fastinvader1.wav").toString();
            AudioClip clip = new AudioClip(sound);
            clip.setCycleCount(AudioClip.INDEFINITE);

            // Draw welcome
            drawWelcome(root);
            drawLevel(root1,alienGroup,playerBullets,alienBullets,textGroup);

       // code snippet from https://www.programcreek.com/java-api-examples/?class=javafx.animation.Transition&method=setCycleCount
       // overide timer to pause inbetween loads
       // fix string so that adjusts to numbers too
       String levelMessage="Starting new level ...";
       Text levelM = new Text(375,250,levelMessage);
       levelM.setFill(Color.WHITE);
       levelM.setFont(Font.font("Ebrima",25));
       levelM.setVisible(false);
       root1.getChildren().add(levelM);
       Transition animator = new Transition() {
           {setCycleDuration(Duration.seconds(3));}
           @Override
           protected void interpolate(double frac) {
               levelM.setVisible(true);
           }
       };
       animator.setCycleCount(1);
       animator.setOnFinished(e -> {
           levelM.setVisible(false);
       });

       // Set up animation timer
           AnimationTimer t = new AnimationTimer (){
               @Override
               public void handle (long now){
                   handle_animation(alienGroup, playerBullets,alienBullets, textGroup,stats.getLevel());

                   if (stats.getLives()<1){
                       drawRestart(root2);
                       stage.setTitle("End of Game");
                       clip.stop();
                       stage.setScene(restartScreen);
                   }
                   if(alienGroup.getChildren().isEmpty()){
                       if (stats.getLevel()==1){
                           animator.play();
                           initAliens(alienGroup);
                           playerBullets.getChildren().removeAll();
                           alienBullets.getChildren().removeAll();
                           stats.setLevel(2);
                           stage.setTitle("Space Invaders: Level 2");
                       }else if (stats.getLevel()==2){
                           animator.play();
                           initAliens(alienGroup);
                           playerBullets.getChildren().removeAll();
                           alienBullets.getChildren().removeAll();
                           stats.setLevel(3);
                           stage.setTitle("Space Invaders: Level 3");
                       }else if (stats.getLevel()==3){
                           drawRestart(root2);
                            stage.setTitle("End of Game");
                            stage.setScene(restartScreen);
                            clip.stop();
                       }
                   }
               }

           };


       // Handle keys on welcome page
       restartScreen.setOnKeyPressed(new EventHandler<KeyEvent>(){
           public void handle(KeyEvent k){
               if(k.getCode() == KeyCode.ENTER){
                   stats=new Statistics();
                   clip.play();
                   player.startPosn();
                   stats.setLevel(1);
                   stage.setTitle("Space Invaders: Level 1");
                   stage.setScene(level1);
               }else if (k.getCode() == KeyCode.Q){
                   Platform.exit();
               }
           }
       });
            // Handle keys on welcome page
            welcomeScene.setOnKeyPressed(new EventHandler<KeyEvent>(){
                    public void handle(KeyEvent k){
                        if(k.getCode() == KeyCode.DIGIT1 || k.getCode() == KeyCode.ENTER){
                            t.start();
                            stats.setLevel(1);
                            clip.play();
                            stage.setTitle("Space Invaders: Level 1");
                            stage.setScene(level1);
                        }else if (k.getCode() == KeyCode.Q){
                            Platform.exit();
                        }else if(k.getCode() == KeyCode.DIGIT2){
                            t.start();
                            stats.setLevel(2);
                            clip.play();
                            stage.setTitle("Space Invaders: Level 2");
                            stage.setScene(level1);
                        }else if(k.getCode() == KeyCode.DIGIT3){
                            t.start();
                            clip.play();
                            stats.setLevel(3);
                            stage.setTitle("Space Invaders: Level 3");
                            stage.setScene(level1);
                        }
                    }
                });

            // Handle keys on level 1 adjust for other levels too
            // handle player movement
            if (stats.getLevel()==0){
                level1.setOnKeyPressed( k -> {
                    if(k.getCode() == KeyCode.A){
                        pMoveLeft=true;
                    }else if (k.getCode() == KeyCode.D){
                        pMoveRight=true;
                    }
                });

                level1.setOnKeyReleased( k->{
                    if(k.getCode() == KeyCode.A){
                        pMoveLeft = false;
                    }else if (k.getCode() == KeyCode.D) {
                        pMoveRight = false;
                    }else if(k.getCode()==KeyCode.SPACE){
                        shootBullet(playerBullets);
                        
                    }
                });
            }


            // Add scene to stage and show it
            stage.setTitle("Space Invaders: Welcome");
            stage.setScene(welcomeScene);
            stage.show();
    }

    void handle_animation(Group alienGroup, Group playerBullets, Group alienBullets,Group textGroup, int level){

       double enemyBulletSpeed=ENEMY1_BULLET_SPEED;
       // set enemy bullet speed based on level
        if (level==2){
            enemyBulletSpeed=ENEMY2_BULLET_SPEED;
        }else if (level==3){
            enemyBulletSpeed=ENEMY3_BULLET_SPEED;
        }

        // set alien moving speed based on number left
        double enemyVSpeed=ENEMY_VERTICAL_SPEED;
        double enemySpeed=ENEMY_SPEED;
        if (alienGroup.getChildren().size()>30 && alienGroup.getChildren().size()<=40){
            enemyVSpeed=ENEMY_VERTICAL_SPEED+(0.25*level);
            enemySpeed=ENEMY_SPEED+(0.25*level);
        }else if (alienGroup.getChildren().size()>20 && alienGroup.getChildren().size()<=30){
            enemyVSpeed=ENEMY_VERTICAL_SPEED+(0.5*level);
            enemySpeed=ENEMY_SPEED+(0.5*level);
        }else if(alienGroup.getChildren().size()>10 && alienGroup.getChildren().size()<=20){
            enemyVSpeed=ENEMY_VERTICAL_SPEED+(0.75*level);
            enemySpeed=ENEMY_SPEED+(0.75*level);
        }else if(alienGroup.getChildren().size()<=10){
            enemyVSpeed=ENEMY_VERTICAL_SPEED+(1*level);
            enemySpeed=ENEMY_SPEED+(1*level);
        }

       // handle player movement
       if (pMoveLeft==true){
           movePlayerLeft();
        }else if(pMoveRight==true){
           movePlayerRight();
       }


       // handle aliens moving
        if (alienMoveR){
            moveAliensRight(alienGroup,alienBullets,enemySpeed,enemyVSpeed,level);
        }else{
            moveAliensLeft(alienGroup,alienBullets,enemySpeed,enemyVSpeed,level);
        }

        // handle moving player bullets
        for(int i=0;i<playerBullets.getChildren().size();i++){
            if (playerBullets.getChildren().get(i).getBoundsInParent().getMinY()-PLAYER_BULLET_SPEED>0){
                playerBullets.getChildren().get(i).setTranslateY(playerBullets.getChildren().get(i).getTranslateY()-PLAYER_BULLET_SPEED);
            }else{
                playerBullets.getChildren().remove(i);
            }
        }

        // handle moving alien bullets
        for(int i=0;i<alienBullets.getChildren().size();i++){
            if (alienBullets.getChildren().get(i).getBoundsInParent().getMinY()+enemyBulletSpeed<600){
                alienBullets.getChildren().get(i).setTranslateY(alienBullets.getChildren().get(i).getTranslateY()+enemyBulletSpeed);
            }else{
                alienBullets.getChildren().remove(i);
            }
        }

        // check collision of player bullet with aliens
        int k=0;
        boolean hit=false;
        while (k<playerBullets.getChildren().size()){
            hit=false;
            for (int j=0;j<alienGroup.getChildren().size();j++){
                if(alienGroup.getChildren().get(j).localToScene(alienGroup.getChildren().get(j).getBoundsInLocal()).intersects(playerBullets.getChildren().get(k).localToScene(playerBullets.getChildren().get(k).getBoundsInLocal()))){
                    alienGroup.getChildren().remove(j);
                    playerBullets.getChildren().remove(k);
                    hit = true;
                    stats.newKill();
                    break;
                }
            }
            if (hit==false){
                k++;
            }
        }

        // check collision of alien bullet with player
        int n=0;
        boolean hitP=false;
        while (n<alienBullets.getChildren().size()){
            hitP=false;
            if(player.getBoundsInParent().intersects(alienBullets.getChildren().get(n).localToScene(alienBullets.getChildren().get(n).getBoundsInLocal()))){
                    alienBullets.getChildren().remove(n);
                    hitP = true;
                    kill_player();
            }
            if (hitP==false){
                n++;
            }
        }

        // Draw score
        // fix string so that adjusts to numbers too
        String leftS="Score: "+stats.getScore();
        String rightS="Ships: "+alienGroup.getChildren().size()+"  Lives: "+stats.getLives()+"   Level: "+stats.getLevel();
        Text leftSide = new Text(10,20,leftS);
        Text rightSide = new Text(625, 20, rightS );
        leftSide.setFill(Color.WHITE);
        leftSide.setFont(Font.font("Ebrima",18));
        rightSide.setFill(Color.WHITE);
        rightSide.setFont(Font.font("Ebrima",18));

        textGroup.getChildren().remove(0);
        textGroup.getChildren().remove(0);
        textGroup.getChildren().add(leftSide);
        textGroup.getChildren().add(rightSide);
    }

    private void drawRestart(Group root){
       String big = "GAME OVER: You Lose!";
       if (stats.getLives()>0){
           big = "CONGRATULATIONS: You Win!";
           stats.setHighScore();
       }

        String med = "Highscore: " + stats.getHighScore()+"\nYour Score: "+ stats.getScore();
        // Add text for instructions
        Text bigText = new Text(320,200,big);
        Text mediumText = new Text(370,250,med);
        Text mediumText2 = new Text (350,300,"Press Enter to Restart.\nPress Q to Quit.");

        bigText.setFont(Font.font("Ebrima",20));
        bigText.setTextAlignment(TextAlignment.CENTER);
        mediumText.setFont(Font.font("Ebrima",18));
        mediumText.setTextAlignment(TextAlignment.CENTER);
        mediumText2.setFont(Font.font("Ebrima",18));
        mediumText2.setTextAlignment(TextAlignment.CENTER);

        if(root.getChildren().isEmpty()){
            root.getChildren().add(bigText);
            root.getChildren().add(mediumText);
            root.getChildren().add(mediumText2);
        }
    }

    private void drawWelcome(Group root){
        // Add space invader title image
        Image titleImage = new Image("/images/logo.png");
        ImageView titleImageView = new ImageView(titleImage);
        titleImageView.setFitWidth(400);
        titleImageView.setFitHeight(200);
        titleImageView.setPreserveRatio(true);
        titleImageView.setX(250);
        titleImageView.setY(20);

        // Add text for instructions
        Text bigText = new Text(400,300,"Instructions");
        Text mediumText = new Text(260,350,"Use A/D to move left/right and SPACE to fire.\nPress ENTER to start the game, or Q to Quit.\nPress 1 or 2 or 3 to start game at a specific level.");
        Text credits = new Text(360, 550,"Amanda Prestidge 20612233");

        bigText.setFont(Font.font("Ebrima",20));
        bigText.setTextAlignment(TextAlignment.CENTER);
        mediumText.setFont(Font.font("Ebrima",18));
        mediumText.setTextAlignment(TextAlignment.CENTER);
        credits.setFont(Font.font("Ebrima",16));
        credits.setTextAlignment(TextAlignment.CENTER);

        // Add things to root
        root.getChildren().add(titleImageView);
        root.getChildren().add(bigText);
        root.getChildren().add(mediumText);
        root.getChildren().add(credits);
    }

    private void drawLevel(Group group, Group alienGroup,Group playerBullets,Group alienBullets, Group textGroup){
        Image pBullet = new Image("/images/player_bullet.png");
        // Draw score
        String leftS="Score: " + stats.getScore();
        String rightS="Ships: " + alienGroup.getChildren().size()+ "  Lives: "+stats.getLives()+"   Level: "+stats.getLevel();
        Text leftSide = new Text(10,20,leftS);
        Text rightSide = new Text(625, 20, rightS );
        leftSide.setFill(Color.WHITE);
        leftSide.setFont(Font.font("Ebrima",18));
        rightSide.setFill(Color.WHITE);
        rightSide.setFont(Font.font("Ebrima",18));

        textGroup.getChildren().add(leftSide);
        textGroup.getChildren().add(rightSide);

        // Set player on screen
        player = new Ship();
        pMoveLeft=false;
        pMoveRight=false;

        // init aliens
        initAliens(alienGroup);

        group.getChildren().add(player);
        group.getChildren().add(alienGroup);
        group.getChildren().add(playerBullets);
        group.getChildren().add(alienBullets);
        group.getChildren().add(textGroup);
    }

    private void moveAliensLeft(Group group,Group alienBullets,double enemySpeed, double enemyVSpeed, int level){

        if((group.getLayoutBounds().getMinX()+group.getTranslateX() - enemySpeed) > 0){
            group.setTranslateX(group.getTranslateX() - enemySpeed);
        }else{
            if (group.getChildren().size()>=1) {
                ImageView bullet;
                for (int i = 0; i < level; i++) {
                    bullet = fireAlien(group);
                    alienBullets.getChildren().add(bullet);
                }
            }
            alienMoveR=true;
            moveAliensDown(group,enemyVSpeed);
        }
    }

    private ImageView fireAlien(Group group){
        String sound = getClass().getClassLoader().getResource("sounds/shoot.wav").toString();
        AudioClip clip = new AudioClip(sound);
        clip.play();
        int alienToShoot=alienToFire(group.getChildren().size());
        Node a1= group.getChildren().get(alienToShoot);
        double xB = a1.localToScene(a1.getBoundsInLocal()).getMinX()+20;
        double yB = a1.localToScene(a1.getBoundsInLocal()).getMinY()+10;
        Random r = new Random();
        int bType = r.nextInt(4);
        Image bulletImage = new Image("/images/bullet1.png");;
        if (bType==2){
            bulletImage= new Image("/images/bullet2.png");
        }else if (bType==3){
            bulletImage= new Image("/images/bullet3.png");
        }
        ImageView bullet = new ImageView(bulletImage);
        bullet.setFitHeight(16);
        bullet.setFitWidth(8);
        bullet.setX(xB);
        bullet.setY(yB);
        return bullet;
    }

    private int alienToFire(int s){
       Random r =new Random();
       return r.nextInt(s);
    }

    private void moveAliensRight(Group group,Group alienBullets,double enemySpeed, double enemyVSpeed, int level){
        if(group.getLayoutBounds().getMaxX()+enemySpeed+group.getTranslateX()<900){
            group.setTranslateX(group.getTranslateX()+enemySpeed);
        }else{
            if (group.getChildren().size()>=1) {
                ImageView bullet;
                for (int i = 0; i < level; i++) {
                    bullet = fireAlien(group);
                    alienBullets.getChildren().add(bullet);
                }
            }
            alienMoveR=false;
            moveAliensDown(group,enemyVSpeed);
        }
    }

    private void moveAliensDown(Group group,double enemyVSpeed){
        if(group.getLayoutBounds().getMaxY() + enemyVSpeed+ group.getTranslateY() < 600){
            group.setTranslateY(group.getTranslateY()+enemyVSpeed);
        }else {
            stats.setDead();
        }
    }

    private void kill_player(){
        stats.lostLife();
        String sound = getClass().getClassLoader().getResource("sounds/explosion.wav").toString();
        AudioClip clip = new AudioClip(sound);
        clip.play();
       Transition animator = new Transition() {
           {setCycleDuration(Duration.seconds(1));}
            @Override
            protected void interpolate(double frac) {
                player.setVisible(false);
            }
        };
        animator.setCycleCount(1);
        animator.setOnFinished(e -> {
            player.setVisible(true);
        });
        animator.play();
    }

    private void movePlayerLeft(){
       if((player.xProperty().get() + player.getTranslateX() - PLAYER_SPEED) > 0 ){
            player.setTranslateX(player.getTranslateX()-PLAYER_SPEED);
       }
    }

    private void movePlayerRight(){
        if((player.xProperty().get() + player.getTranslateX() + PLAYER_SPEED) < 830 ){
            player.setTranslateX(player.getTranslateX()+PLAYER_SPEED);
        }
    }

    private void initAliens(Group group){
       int x;
       int y;
       for (int j=0;j<5;j++) {
           y = 30 +j*30;
           for (int i = 0; i < 10; i++) {
               x = (i * 40);
               aliens[i+i*j]=new Alien();
               aliens[i+i*j].setX(x);
               aliens[i+i*j].setY(y);
               if (j>=2 && j<4){
                   aliens[i+i*j].changeImage(2);
               }else if (j>=4){
                   aliens[i+i*j].changeImage(3);
               }
               group.getChildren().add(aliens[i+i*j]);
           }
       }
    }

    private void shootBullet(Group group){
        Image bulletImage = new Image("/images/player_bullet.png");;
        ImageView bullet = new ImageView(bulletImage);
        bullet.setFitHeight(10);
        bullet.setFitWidth(2);
        bullet.setPreserveRatio(true);
        bullet.setX(player.getX()+player.getTranslateX()+30);
        bullet.setY(player.getY());

        String sound = getClass().getClassLoader().getResource("sounds/shoot.wav").toString();
        AudioClip clip = new AudioClip(sound);
        clip.play();
        group.getChildren().add(bullet);
    }
    // need an animation thing

    // need an even thing too
}
