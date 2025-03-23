package rm;

import java.util.Arrays;

public class UserMemory {
  private static final int BLOCK_SIZE = 16;
  private static final int TOTAL_BLOCKS = 17; // 16 blokų VM + 1 puslapių lentelė
  public Word[][] memory;

  public UserMemory() {
    memory = new Word[TOTAL_BLOCKS][BLOCK_SIZE];
    for (int i = 0; i < TOTAL_BLOCKS; i++) {
      for (int j = 0; j < BLOCK_SIZE; j++) {
        memory[i][j] = new Word("0000"); // Inicializuojame nulinėmis reikšmėmis
      }
    }
  }
  public UserMemory(int TOTAL_BLOCKS) {
    memory = new Word[TOTAL_BLOCKS][BLOCK_SIZE];
    for (int i = 0; i < TOTAL_BLOCKS; i++) {
      for (int j = 0; j < BLOCK_SIZE; j++) {
        memory[i][j] = new Word("0000"); // Inicializuojame nulinėmis reikšmėmis
      }
    }
  }

  // Gauti visą bloką pagal bloko indeksą
  public Word[] getBlock(int blockIndex) {
    if (blockIndex < 0 || blockIndex >= TOTAL_BLOCKS) {
      throw new IllegalArgumentException("Neteisingas bloko indeksas: " + blockIndex);
    }
    return memory[blockIndex];
  }

  // Gauti vieną žodį pagal bloką ir jo vietą bloke
  public Word getWord(int blockIndex, int wordIndex) {
    if (blockIndex < 0 || blockIndex >= TOTAL_BLOCKS || wordIndex < 0 || wordIndex >= BLOCK_SIZE) {
      throw new IllegalArgumentException("Neteisingas adresas: bloko " + blockIndex + ", žodžio " + wordIndex);
    }
    return memory[blockIndex][wordIndex];
  }

  // Rašyti žodį į atmintį
  public void writeWord(int blockIndex, int wordIndex, Word data) {
    if (blockIndex < 0 || blockIndex >= TOTAL_BLOCKS || wordIndex < 0 || wordIndex >= BLOCK_SIZE) {
      throw new IllegalArgumentException("Neteisingas adresas: bloko " + blockIndex + ", žodžio " + wordIndex);
    }
    memory[blockIndex][wordIndex] = data;
  }

  // Parodyti atminties būseną
  public void display() {
    for (int i = 0; i < TOTAL_BLOCKS; i++) {
      System.out.print("Blokas " + i + ": ");
      for (int j = 0; j < BLOCK_SIZE; j++) {
        System.out.print(memory[i][j] + " ");
      }
      System.out.println();
    }
  }

  @Override
  public String toString() {
    return "Memory{" +
        "TOTAL_BLOCKS=" + TOTAL_BLOCKS +
        ", BLOCK_SIZE=" + BLOCK_SIZE +
        ", memory=" + Arrays.deepToString(memory) +
        '}';
  }
}
