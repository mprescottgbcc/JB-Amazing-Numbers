package numbers;

import java.util.HashSet;

class AmazingNumber {
    private final long value;
    private final String valueString;
    private final int length;

    public AmazingNumber(Long num) {
        value = num;
        valueString = String.valueOf(num);
        length = valueString.length();
    }

    private boolean isDivisibleBy7() {
        long first;
        int last;

        if (length > 9) {
            first = Long.parseLong(valueString.substring(0, length - 1));
            last = Integer.parseInt(String.valueOf(valueString.charAt(length - 1)));
            first -= last * 2L;
        } else {
            first = value;
        }

        return first % 7 == 0;
    }

    private int getLastDigit() {
        return Integer.parseInt(String.valueOf(valueString.charAt(length - 1)));
    }

    private int sumOfSquaredDigits(long n) {
        int sum = 0;
        int digit;

        while (n != 0) {
            digit = (int) (n % 10);
            sum += digit * digit;
            n /= 10;
        }

        return sum;
    }

    public boolean isEven() {
        return getLastDigit() % 2 == 0;
    }

    public boolean isBuzz() {
        return isDivisibleBy7() || getLastDigit() == 7;
    }

    public boolean isDuck() {
        for (char c : valueString.toCharArray()) {
            if (c == '0') {
                return true;
            }
        }
        return false;
    }

    public boolean isPalindromic() {
        int i = length - 1;
        char[] sChar = valueString.toCharArray();

        for (char c : sChar) {
            if (c != sChar[i]) {
                return false;
            }
            i--;
        }

        return true;
    }

    public boolean isGapful() {
        if (length < 3) {
            return false;
        }

        int divisor;
        char[] digits = new char[2];

        digits[0] = valueString.charAt(0);
        digits[1] = valueString.charAt(length - 1);
        divisor = Integer.parseInt(String.valueOf(digits));
        return value % divisor == 0;
    }

    public boolean isSpy() {
        long sum = 0;
        long product = 1;

        for (char c : valueString.toCharArray()) {
            int digit = Integer.parseInt(String.valueOf(c));
            sum += digit;
            product *= digit;
        }

        return sum == product;
    }

    public boolean isSquare() {
        long possibleRoot = (long) Math.sqrt(value);
        return possibleRoot * possibleRoot == value;
    }

    public boolean isSunny() {
        return new AmazingNumber(value + 1).isSquare();
    }

    public boolean isJumping() {
        if (valueString.length() == 1) {
            return true;
        }

        for (int i = 1; i < valueString.length(); i++) {
            int previous = Integer.parseInt(String.valueOf(valueString.charAt(i - 1)));
            int current = Integer.parseInt(String.valueOf(valueString.charAt(i)));

            if (Math.abs(previous - current) != 1) {
                return false;
            }
        }

        return true;
    }

    public boolean isHappy() {
        HashSet<Long> set = new HashSet<>();
        long n = value;
        while (true) {
            n = sumOfSquaredDigits(n);
            if (n == 1)
                return true;
            if (set.contains(n))
                return false;
            set.add(n);
        }
    }

    public long getValue() {
        return value;
    }

    @Override
    public String toString() {
        String output = valueString + " is " +
                        (isEven() ? "even, " : "odd, ") +
                        (isBuzz() ? "buzz, " : "") +
                        (isDuck() ? "duck, " : "") +
                        (isPalindromic() ? "palindromic, " : "") +
                        (isGapful() ? "gapful, " : "") +
                        (isSpy() ? "spy, " : "") +
                        (isSquare() ? "square, " : "") +
                        (isSunny() ? "sunny, " : "") +
                        (isJumping() ? "jumping, " : "") +
                        (isHappy() ? "happy, " : "sad, ");

        return output.trim().substring(0, output.length() - 2);
    }
}
