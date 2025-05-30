import java.util.Scanner;

public class factorial {

    //Check if a number is a strong number (sum of factorial of digits equals the number).
     public static void main(String[] args) {
        int num;
        System.out.println("Enter the Number");
        try (Scanner r = new Scanner(System.in)) {
            num=r.nextInt();
        }
        // 5! = 5*4*3*2*1
        int fact = 1;
        for(int i = 1; i <= num; i++){
            fact = fact * i;
        }
        System.out.println("Factorial of " + num + " is " + fact);
     }
}
