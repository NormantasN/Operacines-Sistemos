package rm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class RM {
    public static RM rm;

    // Registrai ir flagai
    public static int AX;
    public static int BX;
    public static short IC;
    public static byte PI;  // Programinių pertraukimų registras
    public static byte SI;  // Supervizorinių pertraukimų registras
    public static byte TI;  // Taimerio pertraukimo registras
    public static byte C;   // Požymių registras

    // RM komponentai
    public static Memory memory;
    public static Printer printer;
    public static HDD hdd;
    public static FlashMemory flash;
    public static Channel channel;

    static {
        try {
            memory = new Memory();
            hdd = new HDD();
            flash = new FlashMemory();
            printer = new Printer();
            channel = new Channel(memory, hdd, flash, printer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RM() {
        System.out.println("Reali mašina inicializuota");
    }

    // Singleton metodas
    public static RM getInstance() {
        if (rm == null) {
            rm = new RM();
        }
        return rm;
    }

    /**
     * loadProgramFromFile() skaito failą (pvz., HDD.txt) ir įrašo kiekvieną eilutę (4 simbolių instrukciją)
     * į RM.memory pradedant nuo bloko 0.
     */
    public void loadProgramFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int currentBlock = 0;
            int currentOffset = 0;
            memory.usedCODEBlocks = 0; // Išvalome kodo blokų skaičių
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                // Užtikriname, kad instrukcija yra būtent 4 simbolių ilgio
                if (line.length() < 4) {
                    line = String.format("%-4s", line);
                } else if (line.length() > 4) {
                    line = line.substring(0, 4);
                }
                Word w = new Word(line);
                memory.write(currentBlock, currentOffset, w);
                currentOffset++;
                if (currentOffset == Memory.BLOCK_SIZE) {
                    currentOffset = 0;
                    currentBlock++;
                }
            }
            memory.usedCODEBlocks = currentBlock;
            System.out.println("Programos instrukcijos įkrautos į atmintį. Naudojami blokai: " + memory.usedCODEBlocks);
        } catch (IOException e) {
            System.err.println("Klaida įkeliant programą iš failo: " + e.getMessage());
        }
    }

    /**
     * processDevices() tikrina flagus ir, priklausomai nuo jų, įvykdo reikiamas funkcijas.
     */
    public void processDevices() {
        if (PI != 0) {
            switch (PI) {
                case 1:
                    System.out.println("RM: PI flag = 1: Aptikta atminties apsaugos klaida!");
                    break;
                case 2:
                    System.out.println("RM: PI flag = 2: Neatpažintas operacijos kodas!");
                    break;
                case 3:
                    System.out.println("RM: PI flag = 3: Bandymas dalyti iš nulio!");
                    break;
                default:
                    System.out.println("RM: PI flag nežinomas: " + PI);
                    break;
            }
            PI = 0;
        }
        if (SI != 0) {
            switch (SI) {
                case 1:
                    System.out.println("RM: SI flag = 1: Pertrauktas nuskaitymo procesas!");
                    break;
                case 2:
                    System.out.println("RM: SI flag = 2: Pertrauktas duomenų išvedimas!");
                    break;
                case 3:
                    System.out.println("RM: SI flag = 3:  Paleidžiama spausdinimo funkcija.");
                    Printer.printBlock(memory, 0);
                    break;
                default:
                    System.out.println("RM: SI flag nežinomas: " + SI);
                    break;
            }
            SI = 0;
        }
        if (TI != 0) {
            System.out.println("RM: TI flag = " + TI + ": Taimerio pertraukimas aktyvus.");
            TI = 0;
        }
        if (C != 0) {
            System.out.println("RM: C registras (požymiai) = " + C);
        }
    }

    // Setter metodai flagams
    public static void setPI(byte state) {
        PI = state;
    }

    public static void setSI(byte state) {
        SI = state;
    }

    public static void setTI(byte state) {
        TI = state;
    }
}
