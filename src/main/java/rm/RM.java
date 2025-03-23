package rm;

import java.io.File;
import java.io.FileNotFoundException;

public class RM {
  public static RM rm;

  // -- REGISTRAI
  public static byte MODE;    // 1 arba 0
  public static int AX;
  public static int BX;
  public static int PTR;
  public static short IC;
  public static byte PI;
  public static byte SI;
  public static byte TI;
  public static byte C; // 1 bitas zero flag (ZF), 2 bitas sign flag (SF), 3 bitas overflow flag(OF)

  // -- RM KOMPONENTES
  public static UserMemory memory;
  public static SupervisorMemory superMemory;
  public static SharedMemory sharedMemory;
  public static Printer printer;
  public static HDD hdd;
//  public static Flash flash;

  static{
    try{
      memory = new UserMemory();
      superMemory = new SupervisorMemory();
      sharedMemory = new SharedMemory();
      hdd = new HDD();
      printer = new Printer();
    }
    catch (Exception e){
      e.printStackTrace();
    }
  }

  public RM(){
    System.out.println("Reali masina inicializuota");
  }

  // Singleton instantiation
  public static RM getInstance(){
    if(rm == null){
      rm = new RM();
    }
    return rm;
  }

  public void load(){

  }


}
