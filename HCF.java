import java.util.Scanner;

public class HCF {
public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    System.out.println("enter a");
    int a = sc.nextInt();
    System.out.println("enter b:");
    int b = sc.nextInt();
    System.out.println("Input values: a = " + a + ", b = " + b);
    if (b > a) {
        System.out.println("Swapping values: a = " + b + ", b = " + a);
        int temp = b;
        a = b;
        b = temp;
    }System.out.println("Calculating HCF...");
System.out.println("Input values: a = " + a + ", b = " + b);
if (b > a) {
    System.out.println("Swapping values: a = " + b + ", b = " + a);
    int temp = b;
    a = b;
    b = temp;
}
System.out.println("After swapping: a = " + a + ", b = " + b);
while (a != 0 || b != 0) {
    int c = a % b;
    a = b;
    b = c;
    System.out.println("Iteration: a = " + a + ", b = " + b);
}
if (a == 0)
    System.out.println("HCF is " + b);
else
    System.out.println("HCF is " + a);
System.out.println("HCF calculation completed.");
         int temp = b;
         a = b;
         b = temp;
         }
         while (a != 0 || b != 0) {
         int c = a % b;
         a = b;
         b = c;
         }
         if (a == 0)
         System.out.println("HCF is " + b);
         else
         System.out.println("HCF is " + a);
        
        /*int a = 27;
        int b = 9;

        while ( b != 0) {
            int c = b;
            b = a % b;
            a = c;
        }
            System.out.println("HCF is " + a);*/

    }
}
