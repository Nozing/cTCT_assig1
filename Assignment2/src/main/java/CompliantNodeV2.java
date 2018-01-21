import java.util.HashSet;
import java.util.Set;

/* CompliantNode refers to a node that follows the rules (not malicious)*/
public class CompliantNodeV2 implements Node {

    private double p_graph, p_malicious, p_txDistribution;
    private int numRounds;

    private boolean [] followees;
    private Set<Transaction> pendingTransactions;

    public CompliantNodeV2(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
        
        this.p_graph = p_graph;
        this.p_malicious = p_malicious;
        this.p_txDistribution = p_txDistribution;
        this.numRounds = numRounds;

        this.followees = new boolean [0];
        this.pendingTransactions = new HashSet<Transaction>();
    }

    public void setFollowees(boolean[] followees) {
        
        this.followees = followees;
    }

    public void setPendingTransaction(Set<Transaction> pendingTransactions) {
        
        this.pendingTransactions = pendingTransactions;
    }

    public Set<Transaction> sendToFollowers() {
        
        return this.pendingTransactions;
    }

    public void receiveFromFollowees(Set<Candidate> candidates) {
        
        this.pendingTransactions = new HashSet<Transaction>();
        for (Candidate candidate : candidates) {

            this.pendingTransactions.add (candidate.tx);
        }
    }
}
