import rm.*;
import vm.*;


public class Main {
    public static void main(String[] args) {
        RM.getInstance();
        // padaryt menu su WHILE/switch case, kad butu ivedimas, imountint flashiuka, switchint zingsnini rezima, exit padaryt
        VM vm = new VM();
        System.out.println("VM inicializuota");
        vm.Execute("HDD.txt");
    }
}
