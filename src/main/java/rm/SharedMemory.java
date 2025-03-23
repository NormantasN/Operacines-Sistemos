package rm;

import java.util.concurrent.Semaphore;

public class SharedMemory {
  private static final int BLOCK_SIZE = 16; // Kiekvienas blokas turi 16 žodžių
  private static final int TOTAL_BLOCKS = 2; // Bendroji atmintis užima 2 blokus
  private Word[][] memory;
  private final Semaphore semaphore; // Semaforas sinchronizacijai

  public SharedMemory() {
    memory = new Word[TOTAL_BLOCKS][BLOCK_SIZE];
    semaphore = new Semaphore(1, true); // Leidžiame tik vienam procesui prieiti vienu metu

    // Inicializuojame bendrąją atmintį tuščiais žodžiais
    for (int i = 0; i < TOTAL_BLOCKS; i++) {
      for (int j = 0; j < BLOCK_SIZE; j++) {
        memory[i][j] = new Word("0000");
      }
    }
  }

  // Rašymas į bendrąją atmintį su sinchronizacija
  public void writeWord(int blockIndex, int wordIndex, Word data) {
    try {
      semaphore.acquire(); // Blokuoja kitas operacijas, kol ši baigsis
      if (isValidAddress(blockIndex, wordIndex)) {
        memory[blockIndex][wordIndex] = data;
      } else {
        throw new IllegalArgumentException("Netinkamas adresas.");
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      System.err.println("Rašymo į bendrąją atmintį klaida: " + e.getMessage());
    } finally {
      semaphore.release(); // Atrakina prieigą kitiems procesams
    }
  }

  // Skaitymas iš bendrosios atminties su sinchronizacija
  public Word readWord(int blockIndex, int wordIndex) {
    try {
      semaphore.acquire();
      if (isValidAddress(blockIndex, wordIndex)) {
        return memory[blockIndex][wordIndex];
      }
      throw new IllegalArgumentException("Netinkamas adresas.");
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      System.err.println("Skaitymo iš bendrosios atminties klaida: " + e.getMessage());
      return null;
    } finally {
      semaphore.release();
    }
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
}
