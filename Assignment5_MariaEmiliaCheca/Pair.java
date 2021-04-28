public class Pair implements Comparable<Pair> {
    private char value;
    private double prob;
    // constructors
    public Pair(char value, double prob){
        this.value = value;
        this.prob = prob;
    }
    public Pair(Pair p){
        value = p.getValue();
        prob = p.getProb();
    }
    // getters
    public double getProb(){
        return prob;
    }

    public char getValue() {
        return value;
    }
    // setters

    public void setProb(double prob) {
        this.prob = prob;
    }

    public void setValue(char value) {
        this.value = value;
    }
    // toString
    public String toString(){
        return "(" + value + ", " + prob + ")";
    }

    @Override
    public int compareTo(Pair p) {
        return Double.compare(this.getProb(), p.getProb());
    }
}
