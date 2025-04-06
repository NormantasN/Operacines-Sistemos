import rm.*;
import vm.*;


public class Main {
    public static void main(String[] args) {
        RM.getInstance();

        VM vm = new VM();
        System.out.println("VM inicializuota");
        vm.Execute("src/HDD.txt");
    }
}
