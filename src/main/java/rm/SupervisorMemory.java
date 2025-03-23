package rm;

public class SupervisorMemory {
  private static final int BLOCK_SIZE = 16;
  private static final int TOTAL_BLOCKS = 16;
  private Word[][] memory;

  public SupervisorMemory() {
    memory = new Word[TOTAL_BLOCKS][BLOCK_SIZE];
    for (int i = 0; i < TOTAL_BLOCKS; i++) {
      for (int j = 0; j < BLOCK_SIZE; j++) {
        memory[i][j] = new Word("0000"); // Inicializuojame nulinėmis reikšmėmis
      }
    }
  }

  // Tik OS turėtų galėti rašyti duomenis į supervizorinę atmintį
  public void writeWord(int blockIndex, int wordIndex, Word data) {
    if (!isSupervisorMode()) {
      throw new SecurityException("Prieiga draudžiama: tik OS gali rašyti į supervizorinę atmintį.");
    }
    if (isValidAddress(blockIndex, wordIndex)) {
      memory[blockIndex][wordIndex] = data;
    } else {
      throw new IllegalArgumentException("Netinkamas adresas.");
    }
  }

  // Skaityti leidžiama tik OS
  public Word readWord(int blockIndex, int wordIndex) {
    if (!isSupervisorMode()) {
      throw new SecurityException("Prieiga draudžiama: tik OS gali skaityti iš supervizorinės atminties.");
    }
    if (isValidAddress(blockIndex, wordIndex)) {
      return memory[blockIndex][wordIndex];
    }
    throw new IllegalArgumentException("Netinkamas adresas.");
  }

  public Word[] getBlock(int blockIndex) {
    if (blockIndex < 0 || blockIndex >= TOTAL_BLOCKS) {
      throw new IllegalArgumentException("Neteisingas bloko indeksas: " + blockIndex);
    }
    return memory[blockIndex];
  }

  private boolean isValidAddress(int blockIndex, int wordIndex) {
    return blockIndex >= 0 && blockIndex < TOTAL_BLOCKS &&
        wordIndex >= 0 && wordIndex < BLOCK_SIZE;
  }

  private boolean isSupervisorMode() {
    // Čia reikėtų patikrinti, ar esame supervizorės režime
    // Pvz., naudojant kokį nors OS būsenos kintamąjį
    return true; // Laikinai grąžina true, bet čia turėtų būti tikrinama reali būsena
  }
}
