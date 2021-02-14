package de.jraetz.bluetooth.connection;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;
import javax.sound.sampled.SourceDataLine;

public class AudioListener implements Runnable {

  private ByteArrayInputStream inputStream;
  private SourceDataLine line;
  private byte[] buf;
  private boolean listen;

  public AudioListener() throws LineUnavailableException {
    // find a DataLine that can be read
    Info[] mixerInfo = AudioSystem.getMixerInfo();
    for (int i = 0; i < mixerInfo.length; i++) {
      Mixer mixer = AudioSystem.getMixer(mixerInfo[i]);
      Line.Info[] source = mixer.getSourceLineInfo();
      if (source.length > 0) {
        line = (SourceDataLine) mixer.getLine(source[0]);
        break;
      }
    }
    if (line == null) {
      throw new UnsupportedOperationException("No audio mixer found");
    }
  }

  @Override
  public void run() {
    try {
      AudioFormat af = new AudioFormat(11000, 8, 1, true, false);
      line.open(af);
      line.start();
      inputStream = getAudioStream();
      listen = true;
      startListening();
      line.stop();
      line.close();
      inputStream.close();
    } catch (LineUnavailableException | IOException e) {
      e.printStackTrace();
    }

  }

  private ByteArrayInputStream getAudioStream() throws LineUnavailableException {
    AudioFormat af = new AudioFormat(11000, 8, 1, true, false);
    line.open(af);
    line.start();
    buf = new byte[(int) af.getSampleRate() * af.getFrameSize()];
    ByteArrayInputStream bais = new ByteArrayInputStream(buf);
    return bais;
  }

  private void startListening() {
    int i = 0, len = 1024;
    while (listen) {
      inputStream.read(buf, i*len, len*(i+1));
      i++;
    }
  }

  public ByteArrayInputStream getInputStream() {
    return inputStream;
  }

  public void stopListening() {
    this.listen = false;
  }
}
