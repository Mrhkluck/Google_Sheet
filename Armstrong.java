import java.util.Scanner;

public class Armstrong {

    // 153 --- 1st find number of digit
    // 1.1 --- find digit
    // 2nd -- find the power
    // 3rd -- sum of digit after power
    // 4th -- check the final sum to given number

    public static void main(String[] args) {
        int num, modenum, countnum = 0,sum = 0;
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter number");
        num = sc.nextInt();
        // Find the digit
        modenum = num; // to keep it safe no work on mode number
        while (modenum > 0) {
            countnum = countnum + 1;
            modenum = modenum / 10;
        }
        System.out.println("Total number of digit are " + countnum); // got the number of digit

        // Find digit
        modenum = num;
        while (modenum > 0) {
            //digit = modenum % 10;
            sum=sum+(int)Math.pow(modenum % 10, countnum);
            modenum /=10;

        }
        System.out.println("Digit is " + sum);
        //check

        if(num == sum){
            System.out.println("its is a armstrong number " +num);
        }else
        System.out.println("its is not a armstrong number " +num);
        sc.close();

    }

}
