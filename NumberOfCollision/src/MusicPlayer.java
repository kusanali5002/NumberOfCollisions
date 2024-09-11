import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class MusicPlayer {
    private Clip clip;

    public void playSound(String soundFilePath) {
        playSound(soundFilePath, false);
    }

    public void playSound(String soundFilePath, boolean loop) {
        try {
            File soundFile = new File(soundFilePath);
            if (!soundFile.exists()) {
                System.err.println("Could not find sound file: " + soundFilePath);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            clip = AudioSystem.getClip();
            clip.open(audioStream);

            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                clip.start();
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void stopSound() {
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }

    public boolean isPlaying() {
        return clip != null && clip.isRunning();
    }
}