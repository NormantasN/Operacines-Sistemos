package rm;

public class Printer {
  public static void print(Object data){
    System.out.println("Printer: ");
    if(data == null){
      System.out.println("null");
    }
    else if(data instanceof char[]){
      System.out.println(new String((char[]) data));
    }
    else{
      System.out.println(data);
    }
  }
}
