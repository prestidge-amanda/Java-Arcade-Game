import javafx.scene.media.AudioClip;

public class Statistics {
    private int lives;
    private int score;
    private int level;
    private static int highScore = 0;

    public Statistics (){
        this.lives=3;
        this.score=0;
        this.level=0;
    }

    public void setDead(){
        this.lives=0;
        String sound = getClass().getClassLoader().getResource("sounds/explosion.wav").toString();
        AudioClip clip = new AudioClip(sound);
        clip.play();
    }
    public void lostLife(){
        lives--;
    }

    public int getLives(){
        return lives;
    }

    public void newKill(){
        score+=1;
    }

    public int getScore(){
        return score;
    }

    public void setHighScore(){
        if (score>highScore){
            highScore=score;
        }
    }

    public int getLevel(){
        return level;
    }

    public void setLevel(int l){
        this.level=l;
    }
    public int getHighScore(){
        return highScore;
    }
}
