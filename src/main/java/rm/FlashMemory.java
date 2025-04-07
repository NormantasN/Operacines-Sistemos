package rm;

public class FlashMemory {
  private static final int BLOCK_SIZE = 16;
  private static final int TOTAL_BLOCKS = 16;
  private final Word[][] flash;  // Flash atmintis saugoma i flash.txt
  // visas perkopijam i harda, jei nebuvo prirasom, jei buvo perrasom, o perrasytas programas executinam,
  // is hardo i supervizorine is supervizorines i virtualia atminti ir executinam
  // saugom programas liste, is kurio zinom ka po ko vykdysim,kol vykdo galim modifikuot rankom, kol i atminti neuzkrove
  // svieziausia info yra harde
  // atskirt interrupto funkcija

  public FlashMemory() {
    flash = new Word[TOTAL_BLOCKS][BLOCK_SIZE];
    for (int i = 0; i < TOTAL_BLOCKS; i++) {
      for (int j = 0; j < BLOCK_SIZE; j++) {
        flash[i][j] = new Word("0000");  // Pradžioje visi žodžiai tušti
      }
    }
  }

  public Word readWord(int block, int offset) {
    return flash[block][offset];
  }

  public void writeWord(int block, int offset, Word word) {
    flash[block][offset] = word;
  }

  public void eraseBlock(int block) {
    for (int i = 0; i < flash[block].length; i++) {
      flash[block][i] = new Word("0000");
    }
  }

  public Word[] getBlock(int block) {
    return flash[block];
  }
}
