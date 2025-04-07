package rm;

public class Channel {
  private int SB, DB, SO, DO, ST, DT; // Registrai
  private UserMemory userMemory;
  private SupervisorMemory supervisorMemory;
  private SharedMemory sharedMemory;
  private HDD hdd;

  public Channel(UserMemory userMemory, SupervisorMemory supervisorMemory, SharedMemory sharedMemory, HDD hdd) {
    this.userMemory = userMemory;
    this.supervisorMemory = supervisorMemory;
    this.sharedMemory = sharedMemory;
    this.hdd = hdd;
  }

  public void setRegisters(int sb, int db, int so, int dO, int st, int dt) {
    this.SB = sb;
    this.DB = db;
    this.SO = so;
    this.DO = dO;
    this.ST = st;
    this.DT = dt;
  }

  public void executeXCHG() {
    if (!isSupervisorMode()) {
      System.out.println("Klaida: Kanalų įrenginį galima naudoti tik supervizoriaus režime.");
      return;
    }

    // Bloko kopijavimas
    Word[] sourceBlock = readBlock(ST, SB);
    if (sourceBlock != null) {
      writeBlock(DT, DB, sourceBlock);
      System.out.println("Blokas sėkmingai nukopijuotas.");
    }

    // Vieno žodžio kopijavimas
    Word singleWord = readWord(ST, SO);
    if (singleWord != null) {
      writeWord(DT, DO, singleWord);
      System.out.println("Žodis sėkmingai nukopijuotas.");
    }
  }

  private boolean isSupervisorMode() {
    return true; // Laikinai grąžina true, bet čia turėtų būti tikrinama reali būsena
  }

  private Word[] readBlock(int storageType, int blockIndex) {
    return switch (storageType) {
      case 1 -> userMemory.getBlock(blockIndex);
      case 2 -> supervisorMemory.getBlock(blockIndex);
      case 3 -> hdd.getBlock(blockIndex);
      case 5 -> sharedMemory.getBlock(blockIndex);
      default -> {
        System.out.println("Netinkamas šaltinio blokas.");
        yield null;
      }
    };
  }

  private Word readWord(int storageType, int blockIndex) {
    return switch (storageType) {
      case 1 -> userMemory.getWord(blockIndex, 0);
      case 2 -> supervisorMemory.readWord(blockIndex, 0);
      case 3 -> hdd.readWord(blockIndex, 0);
      case 5 -> sharedMemory.readWord(blockIndex, 0);
      default -> {
        System.out.println("Netinkamas šaltinio žodis.");
        yield null;
      }
    };
  }

  private void writeBlock(int storageType, int blockIndex, Word[] data) {
    switch (storageType) {
      case 1 -> {
        for (int i = 0; i < data.length; i++) {
          userMemory.writeWord(blockIndex, i, data[i]);
        }
      }
      case 2 -> {
        for (int i = 0; i < data.length; i++) {
          supervisorMemory.writeWord(blockIndex, i, data[i]);
        }
      }
      case 3 -> {
        for (int i = 0; i < data.length; i++) {
          hdd.writeWord(blockIndex, i, data[i]);
        }
      }
      case 5 -> {
        for (int i = 0; i < data.length; i++) {
          sharedMemory.writeWord(blockIndex, i, data[i]);
        }
      }
      default -> System.out.println("Netinkama paskirties vieta blokui.");
    }
  }

  // prideti printeri, patikrint if source atmintis
  // galima tiesiai cia printint
  // cikla is VM perkelt i RM

  private void writeWord(int storageType, int blockIndex, Word data) {
    switch (storageType) {
      case 1 -> userMemory.writeWord(blockIndex, 0, data);
      case 2 -> supervisorMemory.writeWord(blockIndex, 0, data);
      case 3 -> hdd.writeWord(blockIndex, 0, data);
      case 5 -> sharedMemory.writeWord(blockIndex, 0, data);
      default -> System.out.println("Netinkama paskirties vieta žodžiui.");
    }
  }

}
