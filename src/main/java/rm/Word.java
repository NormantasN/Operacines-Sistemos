package rm;

import java.util.Arrays;

public class Word {
  private char[] word;

  public Word(String data){
    if(data == null || data.length() != 4){
      throw new IllegalArgumentException("Zodis turi buti 4 baitu ilgio");
    }
    word = data.toCharArray();
  }

  public Word(char[] data){
    if(data == null || data.length != 4){
      throw new IllegalArgumentException("Zodis turi buti 4 baitu ilgio");
    }
    word = data.clone();
  }

  @Override
  public String toString() {
    return Arrays.toString(word);
  }
}
