package vm;
import rm.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * VM klasė įkelia ir vykdo programos segmentus iš HDD failo.
 * palaiko šias komandas:
 *
 * Aritmetinės komandos:
 *   ADD_  – sudeda AX ir BX, įrašo rezultatą į AX (jei reikia, nustato perteklio (OF) ir ženklų (SF) flagus).
 *   MUL_  – sudaugina AX ir BX, įrašo rezultatą į AX (jei reikia, nustato perteklio (OF) ir ženklų (SF) flagus).
 *
 * Darbo su duomenimis komandos:
 *   LWxx  – įkelia žodį iš atminties adresu (16*x1+x2) į AX.
 *   LSxx  – įrašo žodį iš AX į atmintį adresu (16*x1+x2).
 *   SWAP  – sukeičia registrų AX ir BX reikšmes.
 *   PRNx  – išveda x bloką iš vartotojo atminties.
 *
 * Valdymo komandos:
 *   JMxx  – besąlyginis peršokimas: nustato komandos skaitliuką į adresą (16*x1+x2).
 *   HALT  – nutraukia einamojo programos segmento vykdymą.
 */
public class VM {

    /**
     * HDD.txt Failo formatas:
     *   ----                      // Failo pradzia
     *   <PAV1>                // 4 simboliu pavadinimas
     *   DATA
     *   <Data segmentas>
     *   CODE
     *   <Kodo segmentas>
     *   HALT
     */
    // VM dirba su virtualia atmintim
    public void Execute(String filename) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filename));
            List<List<String>> segments = new ArrayList<>();
            List<String> currentSegment = new ArrayList<>();

            for (String line : lines) {
                line = line.trim();
                if (line.equals("----")) {
                    if (!currentSegment.isEmpty()) {
                        segments.add(new ArrayList<>(currentSegment));
                        currentSegment.clear();
                    }
                } else {
                    if (!line.isEmpty()) {
                        currentSegment.add(line);
                    }
                }
            }
            if (!currentSegment.isEmpty()) {
                segments.add(currentSegment);
            }

            int programNumber = 1;
            for (List<String> segment : segments) {
                if (segment.size() < 2) continue;

                System.out.println("=== Executing Program " + programNumber + " ===");

                String fileName = segment.get(0);
                System.out.println("Program name: " + fileName);

                List<String> instructions = new ArrayList<>();
                boolean inDataSection = false;
                boolean inCodeSection = false;

                // Process all lines from index 1 onward.
                for (int i = 1; i < segment.size(); i++) {
                    String line = segment.get(i);
                    if (line.equalsIgnoreCase("DATA")) {
                        inDataSection = true;
                        inCodeSection = false;
                        continue;
                    }
                    if (line.equalsIgnoreCase("CODE")) {
                        inDataSection = false;
                        inCodeSection = true;
                        continue;
                    }
                    if (inDataSection) {
                        String[] parts = line.split("\\s+");
                        if (parts.length == 2) {
                            String addrHex = parts[0];   // pvz "04"
                            String valueHex = parts[1];   // pvz "0001"
                            int address = Integer.parseInt(addrHex, 16);
                            int block = address / 16;
                            int offset = address % 16;
                            Word word = new Word(valueHex);
                            RM.memory.writeWord(block, offset, word);
                            System.out.println("DATA: Loaded " + valueHex
                                    + " into block " + block + ", offset " + offset);
                        }
                    }
                    if (inCodeSection) {
                        instructions.add(line);
                    }
                }
                Commands(instructions);
                programNumber++;
                System.out.println("==========================================");
            }
        } catch (IOException e) {
            System.err.println("Error reading HDD file: " + e.getMessage());
        }
    }

    /**
     * Komandu sarasas
     */
    private void Commands(List<String> instructions) {
        int pc = 0; // Program counteris
        while (pc < instructions.size()) { // Negali but WHILe vm turi but realioj masinoj, RM ivykdo viena komanda ir patikrina ar reikia executint, nustatom timeri, suziurim si ti pi, vm gali nustatyt jiem reiksmes, bet daugiau nieko
            String instr = instructions.get(pc);
            System.out.println("Executing: " + instr);
            if (instr.equalsIgnoreCase("HALT")) {
                System.out.println("HALT encountered. Ending current program execution.");
                // NUSTATYT SI, pagal dokumentacija
                break;
            } else if (instr.startsWith("LW")) {
                // LWxx: įkelia žodį iš atminties adresu (16*x1+x2) į AX
                String addrStr = instr.substring(2);
                int address = Integer.parseInt(addrStr, 16);
                int block = address / 16;
                int offset = address % 16;
                Word w = RM.memory.getWord(block, offset);
                RM.AX = wordToInt(w);
                System.out.println("LW: AX <- " + RM.AX);
            } else if (instr.startsWith("LS")) {
                // LSxx: įrašo žodį iš AX į atmintį adresu (16*x1+x2).
                String addrStr = instr.substring(2);
                int address = Integer.parseInt(addrStr, 16);
                int block = address / 16;
                int offset = address % 16;
                String hexVal = intToHex(RM.AX);
                Word w = new Word(hexVal);
                RM.memory.writeWord(block, offset, w);
                System.out.println("LS: Memory[" + block + "][" + offset + "] <- " + hexVal);
            } else if (instr.equalsIgnoreCase("SWAP")) {
                // SWAP: sukeičia registrų AX ir BX reikšmes.
                int temp = RM.AX;
                RM.AX = RM.BX;
                RM.BX = temp;
                System.out.println("SWAP: AX=" + RM.AX + ", BX=" + RM.BX);
            } else if (instr.equalsIgnoreCase("ADD_")) {
                int result = RM.AX + RM.BX;
                RM.C = 0;
                if (result > 32767 || result < -32768) {
                    RM.C |= 0x04; // (OF)
                }
                if (result < 0) {
                    RM.C |= 0x02; //  (SF)
                }
                RM.AX = result & 0xFFFF;
                System.out.println("ADD_: AX=" + RM.AX);
            } else if (instr.equalsIgnoreCase("MUL_")) {
                int result = RM.AX * RM.BX;
                RM.C = 0;
                if (result > 32767 || result < -32768) {
                    RM.C |= 0x04; //  (OF)
                }
                if (result < 0) {
                    RM.C |= 0x02; //  (SF)
                }
                RM.AX = result & 0xFFFF;
                System.out.println("MUL_: AX=" + RM.AX);
            } else if (instr.startsWith("JM")) {
                // JMxx: nustato komandos skaitliuką į adresą (16*x1+x2).
                String addrStr = instr.substring(2);
                short jumpAddress = Short.parseShort(addrStr, 16);
                if (jumpAddress < instructions.size()) {
                    System.out.println("JM: Jumping to instruction index " + jumpAddress);
                    RM.IC = jumpAddress;   // REALIOJ MASINOJ turi but
                    continue;
                } else {
                    System.out.println("JM: Jump address out of range. Ignoring jump.");
                }
            } else if (instr.startsWith("PRN")) {
                // PRNx: išveda x bloką iš vartotojo atminties.
                String param = instr.substring(3);
                int block = Integer.parseInt(param, 16);
                Word[] blockWords = RM.memory.getBlock(block);
                StringBuilder sb = new StringBuilder();
                for (Word w : blockWords) {
                    sb.append(w.toString()).append(" ");
                }
                RM.printer.print(sb.toString()); // VIrtuali masina tiktai SI nustato ir reali masina pamato kad SI = 3 ir isspausdina per printeri, iskvietus kanalo irengini, kuris pradeada spausdinima, reikia paduot pradzia, apie printeri ir flashiuka zino RM bet NE VM
            } else {
                System.out.println("Unknown instruction: " + instr);
            }
            pc++;
        }
    }

    /**
     * 4 simboliu hex zodi pavercia i int
     */
    private int wordToInt(Word w) {
        String s = w.toString();
        s = s.replaceAll("[\\[\\]\\,\\s]", "");
        return Integer.parseInt(s, 16);
    }

    /**
     * int pavercia i 4 simboliu hex zodi
     */
    private String intToHex(int value) {
        return String.format("%04X", value & 0xFFFF);
    }
}
