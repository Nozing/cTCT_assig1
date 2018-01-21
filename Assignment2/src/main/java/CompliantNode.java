import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/* CompliantNode refers to a node that follows the rules (not malicious)*/
public class CompliantNode implements Node {

    private double p_graph, p_malicious, p_txDistribution;
    private int numRounds;

    private boolean [] followees;
    private Set<Transaction> pendingTransactions;
    private Map<Integer, ProcessedTransaction> processedTransaction;

    public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
        
        this.p_graph = p_graph;
        this.p_malicious = p_malicious;
        this.p_txDistribution = p_txDistribution;
        this.numRounds = numRounds;

        this.followees = new boolean [0];
        this.pendingTransactions = new HashSet<Transaction>();
        this.processedTransaction = new HashMap<Integer,CompliantNode.ProcessedTransaction>();
    }

    public void setFollowees(boolean[] followees) {
        
        this.followees = followees;
    }

    public void setPendingTransaction(Set<Transaction> pendingTransactions) {
        
        this.pendingTransactions = pendingTransactions;
    }

    public Set<Transaction> sendToFollowers() {
        
        if (this.processedTransaction.isEmpty()) {

            return this.pendingTransactions;
        } else {

            Set<Transaction> transactionsToSend = 
                    new HashSet<Transaction>();
            
            for (Entry<Integer, ProcessedTransaction> entry : this.processedTransaction.entrySet()) {

                transactionsToSend.add(entry.getValue().tx);
            }

            return transactionsToSend;
        }
    }

    public void receiveFromFollowees(Set<Candidate> candidates) {
        
        candidates = validateCandidatesReception(candidates);

        for (Candidate candidate : candidates) {

            if (this.processedTransaction.containsKey(candidate.tx.id)) {

                this.processedTransaction.get(
                    candidate.tx.id).incrementNumberOfRepetitions();
            } else {

                this.processedTransaction.put(
                    candidate.tx.id, new ProcessedTransaction(candidate.tx));
            }
        }
    }

	private Set<Candidate> validateCandidatesReception(Set<Candidate> candidates) {
        
        Set<Candidate> candidatesValidated = new HashSet<Candidate>();
        for (Candidate candidate : candidates) {

            if (candidate.sender > 0 
                    && candidate.sender < this.followees.length 
                    && this.followees[candidate.sender]) {

                candidatesValidated.add(candidate);
            }
        }

        return candidatesValidated;
    }
    
    private class ProcessedTransaction {

        Transaction tx;
        int numRepetitions;

        ProcessedTransaction(Transaction tx) {

            this.tx = tx;
            this.numRepetitions = 1;
        }

        void incrementNumberOfRepetitions() {

            numRepetitions++;
        }
    }
}
