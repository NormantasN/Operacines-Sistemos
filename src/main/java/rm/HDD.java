package rm;

public class HDD {
  private static final int BLOCK_SIZE = 16; // Kiekvienas blokas turi 16 žodžių
  private static final int TOTAL_BLOCKS = 34; // Išorinė atmintis užima 34 blokus
  private Word[][] storage;

  public HDD() {
    storage = new Word[TOTAL_BLOCKS][BLOCK_SIZE];

    // Inicializuojame visus blokus tuščiais žodžiais
    for (int i = 0; i < TOTAL_BLOCKS; i++) {
      for (int j = 0; j < BLOCK_SIZE; j++) {
        storage[i][j] = new Word("0000");
      }
    }
  }

  // Rašymas į diską
  public void writeWord(int blockIndex, int wordIndex, Word data) {
    if (isValidAddress(blockIndex, wordIndex)) {
      storage[blockIndex][wordIndex] = data;
    } else {
      throw new IllegalArgumentException("Netinkamas HDD adresas.");
    }
  }

  // Skaitymas iš disko
  public Word readWord(int blockIndex, int wordIndex) {
    if (isValidAddress(blockIndex, wordIndex)) {
      return storage[blockIndex][wordIndex];
    }
    throw new IllegalArgumentException("Netinkamas HDD adresas.");
  }

  // Gauti visą bloką
  public Word[] getBlock(int blockIndex) {
    if (blockIndex < 0 || blockIndex >= TOTAL_BLOCKS) {
      throw new IllegalArgumentException("Netinkamas HDD bloko indeksas.");
    }
    return storage[blockIndex];
  }

  private boolean isValidAddress(int blockIndex, int wordIndex) {
    return blockIndex >= 0 && blockIndex < TOTAL_BLOCKS &&
        wordIndex >= 0 && wordIndex < BLOCK_SIZE;
  }
}
