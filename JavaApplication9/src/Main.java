
import java.util.Scanner;

public class Main{
    
    public static void main(String[] args){
        Scanner leer = new Scanner(System.in);
        System.out.print("Ingrese numero 1:");
        int numero1 = leer.nextInt();
        System.out.println("Ingrese numero 2:");
        int numero2 = leer.nextInt();
        int numero3 = numero1 + numero2;
        System.out.println("La suma de los dos numeros es:"+numero3);
        
    }

}