import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.io.File;

public class Huffman {
    public static void encode() throws IOException {
        // Accept user input
        Scanner in = new Scanner(System.in);
        System.out.print("Enter filename to read from/encode: ");
        String filename = in.next();
        File file = new File(filename);
        Scanner inputFile = new Scanner(file);

        String characters = null;
        ArrayList<String> completeFile = new ArrayList<>();
        while (inputFile.hasNextLine()) {
            characters = inputFile.nextLine();
            completeFile.add(characters);
        }

        int total = 0;
        int[] freq = new int[256];
        char[] chars = new char[0];
        // count number of non-whitespace characters
        for (String s : completeFile) {
            chars = s.replaceAll("\\s", "").toCharArray();
            for (char n : chars) {
                freq[n]++;
            }
            total = total + chars.length;
        }

        // ArrayList of pairs
        ArrayList<Pair> pairs = new ArrayList<>();
        for (int i = 0; i < freq.length; i++) {
            // calculate relative probability of each character
            double prob = Math.round(freq[i] * 10000d / total) / 10000d;
            int valueInt = i;
            char value = (char) i;
            // create a new pair
            if (prob > 0.0) {
                Pair p = new Pair(value, prob);
                pairs.add(p);
            }
        }

        LinkedList<BinaryTree<Pair>> s = new LinkedList<>();
        LinkedList<BinaryTree<Pair>> t = new LinkedList<>();

        // sort pairs and add in ascending order to s queue
        Collections.sort(pairs);
        for (int i = 0; i < pairs.size(); i++) {
            BinaryTree<Pair> bt = new BinaryTree<>();
            bt.makeRoot(pairs.get(i));
            s.add(bt);
        }

        // Huffman algorithm
        // Pick smallest weight trees
        while (!s.isEmpty()) {
            BinaryTree<Pair> a = new BinaryTree<>();
            BinaryTree<Pair> b = new BinaryTree<>();
            // If t is empty
            if (t.isEmpty()) {
                a.setData(s.pop().getData());
                b.setData(s.pop().getData());
            }
            // if t is not empty
            else {
                // find a
                int n = s.peek().getData().compareTo(t.peek().getData());
                if (n >= 0) {
                    a.setData(s.pop().getData());
                } else {
                    a.setData(t.pop().getData());
                }
                // find b
                n = s.peek().getData().compareTo(t.peek().getData());
                if (n >= 0) {
                    b.setData(s.pop().getData());
                } else {
                    b.setData(t.pop().getData());
                }

            }
            // new tree
            BinaryTree<Pair> p = new BinaryTree<>();
            double sum = a.getData().getProb() + b.getData().getProb();
            Pair combine = new Pair('⁂', sum);
            p.makeRoot(combine);
            p.attachLeft(a);
            p.attachRight(b);
            // Enqueue p to t
            t.add(p);
        }

        // if  size in T is > 1
        if (t.size() > 1) {
            while (t.size() != 1) {
                BinaryTree<Pair> a = new BinaryTree<>();
                BinaryTree<Pair> b = new BinaryTree<>();
                a.setData(t.peek().getData());
                a.setLeft(t.peek().getLeft());
                a.setRight(t.peek().getRight());
                t.pop();

                b.setData(t.peek().getData());
                b.setLeft(t.peek().getLeft());
                b.setRight(t.peek().getRight());
                t.pop();
                BinaryTree<Pair> p = new BinaryTree<>();
                double sum = a.getData().getProb() + b.getData().getProb();
                Pair combine = new Pair('⁂', sum);
                p.makeRoot(combine);
                p.attachLeft(a);
                p.attachRight(b);
                t.add(p);
            }
        }
        System.out.println("Printing codes to Huffman.txt");
        // print codes to an output file call Huffman.txt
        PrintWriter output = new PrintWriter("Huffman.txt");

        output.println("Symbol     Prob.     Huffman Code");
        String[] code = findEncoding(t.getFirst());
        ArrayList<String> cfinals = new ArrayList<>();
        ArrayList<Character> sfinals = new ArrayList<>();
        int j = 0;
        for (int i = 0; i < code.length; i++) {
            if (code[i] != null) {
                char v = (char) i;
                output.println(v + "         " + pairs.get(j).getProb() + "     " + code[i]);
                cfinals.add(code[i]);
                sfinals.add(v);
                j++;
            }
        }
        output.flush();

        System.out.println("Printing encoded text to Encoded.txt");

        // encode file into a new file Encoded.txt
        PrintWriter encoded = new PrintWriter("Encoded.txt");
        String temp = null;
        char[] f = new char[0];
        for (int i = 0; i < completeFile.size(); i++) {
            if (i > 0) {
                temp = temp + "⁂" + completeFile.get(i);
            } else {
                temp = completeFile.get(i);
            }

        }
        f = temp.toCharArray();
        for (int i = 0; i < f.length; i++) {
            if (f[i] == '⁂') {
                encoded.println();
            } else if (f[i] == ' ') {
                encoded.print(" ");
            }
            for (int k = 0; k < pairs.size(); k++) {
                if (sfinals.get(k) == f[i]) {
                    encoded.print(cfinals.get(k));
                }
            }
        }
        encoded.flush();
    }


    private static String[] findEncoding(BinaryTree<Pair> bt) {
        String[] result = new String[256];
        findEncoding(bt, result, "");
        return result;
    }

    private static void findEncoding(BinaryTree<Pair> bt, String[] a, String prefix) {
        // test is node/tree is a leaf
        if (bt.getLeft() == null && bt.getRight() == null) {
            a[bt.getData().getValue()] = prefix;
        }

        // recursive calls
        else {
            findEncoding(bt.getLeft(), a, prefix + "0");
            findEncoding(bt.getRight(), a, prefix + "1");
        }
    }

    public static void decode() throws IOException {
        // accept user input
        Scanner in = new Scanner(System.in);
        System.out.print("Enter filename to read from/decode: ");
        String decode = in.next();
        File f1 = new File(decode);
        System.out.print("Enter filename of document containing Huffman codes: ");
        String codes = in.next();
        File f2 = new File(codes);
        Scanner ls = new Scanner(f2);
        ls.nextLine();

        ArrayList<Character> characters = new ArrayList<>();
        ArrayList<String> code = new ArrayList<>();
        // Store codes and characters into arrays
        while (ls.hasNext()) {
            char c = ls.next().charAt(0);
            ls.next();
            String s = ls.next();
            characters.add(c);
            code.add(s);
        }

        // decode file
        // read f1 into char[]
        Scanner inFile = new Scanner(f1);
        String charac = null;
        ArrayList<String> entireFile = new ArrayList<>();
        while (inFile.hasNextLine()) {
            charac = inFile.nextLine();
            entireFile.add(charac);
        }

        // transform char[]
        String tmp = null;
        char[] f = new char[0];
        for (int i = 0; i < entireFile.size(); i++) {
            if (i > 0) {
                tmp = tmp + "⁂" + entireFile.get(i);
            } else {
                tmp = entireFile.get(i);
            }

        }
        f = tmp.toCharArray();

        System.out.println("Printing decoded text to Decoded.txt");
        // print decoded string to output file
        PrintWriter output = new PrintWriter("Decoded.txt");

        // compare first chars to codes to see if we can find a match
        int i = 0;
        while (i < f.length) {
            if (f[i] == ' ') {
                output.print(" ");
                i++;
            } else if (f[i] == '⁂') {
                output.println();
                i++;
            }
            else {
                String compare = String.valueOf(f[i]) + String.valueOf(f[i + 1]) + String.valueOf(f[i + 2]) + String.valueOf(f[i + 3]);
                i = i + 3;
                boolean found = false;
                for (int j = 0; j < code.size(); j++) {
                    if (compare.equals(code.get(j))) {
                        output.print(characters.get(j));
                        found = true;
                    }
                }

                if (found == false) {
                    if (i < f.length) {
                        i++;
                        compare = compare + String.valueOf(f[i]);
                    }
                    for (int j = 0; j < code.size(); j++) {
                        if (compare.equals(code.get(j))) {
                            output.print(characters.get(j));
                            found = true;
                            break;
                        }
                    }
                }
                i++;
            }
        }
        output.flush();
    }
}