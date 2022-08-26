package numbers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class Main {
    static ArrayList<String> included_properties;
    static ArrayList<String> excluded_properties;

    private static void printWelcome() {
        System.out.println(
            "Welcome to Amazing Numbers!\n" +
            "\n" +
            "Supported requests:\n" +
            "- enter a natural number to know its properties;\n" +
            "- enter two natural numbers to obtain the properties of the list:\n" +
            "  * the first parameter represents a starting number;\n" +
            "  * the second parameter shows how many consecutive numbers are to be printed;\n" +
            "- two natural numbers and properties to search for;\n" +
            "- a property preceded by minus must not be present in numbers;\n" +
            "- separate the parameters with one space;\n" +
            "- enter 0 to exit.\n"
        );
    }

    private static boolean hasProperty(AmazingNumber n, Property p) {
        return switch (p) {
            case EVEN -> n.isEven();
            case ODD -> !n.isEven();
            case BUZZ -> n.isBuzz();
            case DUCK -> n.isDuck();
            case PALINDROMIC -> n.isPalindromic();
            case GAPFUL -> n.isGapful();
            case SPY -> n.isSpy();
            case SQUARE -> n.isSquare();
            case SUNNY -> n.isSunny();
            case JUMPING -> n.isJumping();
            case HAPPY -> n.isHappy();
            case SAD -> !n.isHappy();
        };
    }

    private static boolean hasAllProperties(AmazingNumber number) {
        boolean hasIncluded;
        boolean hasExcluded;
        Property property;

        for (String s : included_properties) {
            property = Property.valueOf(s);
            hasIncluded = hasProperty(number, property);

            if (!hasIncluded) {
                return false;
            }
        }

        for (String s : excluded_properties) {
            property = Property.valueOf(s);
            hasExcluded = hasProperty(number, property);

            if (hasExcluded) {
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Request request;
        AmazingNumber number;
        long value;
        int sampleSize;

        printWelcome();

        while (true) {
            included_properties = new ArrayList<>();
            excluded_properties = new ArrayList<>();

            System.out.println("Enter a request:");
            try {
                request = new Request(scanner.nextLine());
            } catch (Exception e) {
                System.out.println(e.getMessage());
                continue;
            }

            if (request.isDone()) {
                System.out.println("Goodbye!");
                return;
            }

            number = request.number;
            value = number.getValue();
            sampleSize = request.sampleSize;

            //Just one number to print:
            if (sampleSize == 0) {
                boolean isEven = number.isEven();
                boolean isHappy = number.isHappy();

                System.out.println(
                    "Properties of " + value + "\n" +
                    "even: " + isEven + "\n" +
                    "odd: " + !isEven + "\n" +
                    "buzz: " + number.isBuzz() + "\n" +
                    "duck: " + number.isDuck() + "\n" +
                    "palindromic: " + number.isPalindromic() + "\n" +
                    "gapful: " + number.isGapful() + "\n" +
                    "spy: " + number.isSpy() + "\n" +
                    "square: " + number.isSquare() + "\n" +
                    "sunny: " + number.isSunny() + "\n" +
                    "jumping: " + number.isJumping() + "\n" +
                    "happy: " + isHappy + "\n" +
                    "sad: " + !isHappy + "\n"
                );

                continue;
            }

            //A starting number and sample size is provided
            if (included_properties.isEmpty() && excluded_properties.isEmpty()) {
                for (long i = value; i < value + sampleSize; i++) {
                    try {
                        System.out.println(new AmazingNumber(i));
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }

                System.out.println();

                continue;
            }

            //Now let's find the numbers that have the included properties and not the excluded
            int count = 0;
            int i = 0;
            AmazingNumber n;

            while (count < sampleSize) {
                n = new AmazingNumber(value + i);
                if (hasAllProperties(n)) {
                    System.out.println(n);
                    count++;
                }
                i++;
            }

            System.out.println();
        }
    }

    enum Property {
        EVEN, ODD, BUZZ, DUCK, PALINDROMIC, GAPFUL, SPY, SQUARE, SUNNY, JUMPING, HAPPY, SAD
    }

    static class Request {
        private AmazingNumber number = null;
        private int sampleSize = 0;
        private boolean done = false;

        public Request(String values) throws Exception {
            String[] parameters = values.split(" ");

            try {
                long first = Long.parseLong(parameters[0]);
                if (first == 0) {
                    done = true;
                    return;
                }

                if (first < 0) {
                    throw new Exception();
                }

                number = new AmazingNumber(first);
            } catch (Exception e) {
                throw new Exception("The first parameter should be a natural number or zero.\n");
            }

            try {
                if (parameters.length >= 2) {
                    sampleSize = Integer.parseInt(parameters[1]);
                    if (sampleSize < 1) {
                        throw new Exception();
                    }
                }
            } catch (Exception e) {
                throw new Exception("The second parameter should be a natural number.\n");
            }

            //Process property parameters and check for any bad properties
            if (parameters.length >= 3) {
                HashSet<String> badProperties = new HashSet<>();

                for (int i = 2; i < parameters.length; i++) {
                    String property = parameters[i].toUpperCase();
                    try {
                        setProperties(property);
                    } catch (BadPropertyException e) {
                        badProperties.add(e.getMessage());
                    }
                }

                if (badProperties.size() > 0) {
                    throw new Exception(badPropertiesToString(badProperties));
                }


                //Now let's check for mutually exclusive properties ...
                for (String property : included_properties) {
                    if (excluded_properties.contains(property)) {
                        throw new MutuallyExclusivePropertyException(property, "-" + property);
                    }

                    if (property.equals("EVEN") && included_properties.contains("ODD")) {
                        throw new MutuallyExclusivePropertyException("EVEN", "ODD");
                    }

                    if (property.equals("DUCK") && included_properties.contains("SPY")) {
                        throw new MutuallyExclusivePropertyException("DUCK", "SPY");
                    }

                    if (property.equals("SUNNY") && included_properties.contains("SQUARE")) {
                        throw new MutuallyExclusivePropertyException("SUNNY", "SQUARE");
                    }

                    if (property.equals("HAPPY") && included_properties.contains("SAD")) {
                        throw new MutuallyExclusivePropertyException("HAPPY", "SAD");
                    }
                }

                for (String property : excluded_properties) {
                    if (property.equals("EVEN") && excluded_properties.contains("ODD")) {
                        throw new MutuallyExclusivePropertyException("-EVEN", "-ODD");
                    }

                    if (property.equals("DUCK") && excluded_properties.contains("SPY")) {
                        throw new MutuallyExclusivePropertyException("-DUCK", "-SPY");
                    }

                    if (property.equals("SUNNY") && excluded_properties.contains("SQUARE")) {
                        throw new MutuallyExclusivePropertyException("-SUNNY", "-SQUARE");
                    }

                    if (property.equals("HAPPY") && excluded_properties.contains("SAD")) {
                        throw new MutuallyExclusivePropertyException("-HAPPY", "-SAD");
                    }
                }
            }
        }

        private void setProperties(String property) throws BadPropertyException {
            boolean include = property.charAt(0) != '-';

            String temp = (include)
                ? String.copyValueOf(property.toCharArray())
                : String.copyValueOf(property.toCharArray()).substring(1);

            try {
                Property.valueOf(temp);
                if (include) {
                    included_properties.add(temp);
                } else {
                    excluded_properties.add(temp);
                }
            } catch (Exception e) {
                throw new BadPropertyException(temp);
            }
        }

        private String badPropertiesToString(HashSet<String> badProperties) {
            StringBuilder error = new StringBuilder("The propert");
            error
                .append(badProperties.size() > 1 ? "ies" : "y")
                .append(" [");

            int i = 1;
            for (String s : badProperties) {
                error.append(s);
                if (i < badProperties.size()) {
                    error.append(", ");
                }

                i++;
            }

            error.append("] ")
                .append(badProperties.size() > 1 ? "are " : "is ")
                .append("wrong.\nAvailable properties: [");

            for (i = 0; i < Property.values().length; i++) {
                error.append(Property.values()[i]);
                if (i < Property.values().length - 1) {
                    error.append(", ");
                }
            }

            error.append("]\n");
            return error.toString();
        }

        public boolean isDone() {
            return done;
        }

        private static class BadPropertyException extends Exception {
            BadPropertyException(String s) {
                super(s);
            }
        }

        private static class MutuallyExclusivePropertyException extends Exception {
            MutuallyExclusivePropertyException(String p1, String p2) {
                super(
                    "The request contains mutually exclusive properties: [" +
                    p1 + ", " + p2 + "]\n" +
                    "There are no numbers with these properties.\n"
                );
            }
        }
    }
}
