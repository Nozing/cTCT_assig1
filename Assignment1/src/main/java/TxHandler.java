import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TxHandler {

    private UTXOPool pool;

    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
        
        this.pool = new UTXOPool(utxoPool);
    }

    /**
     * @return true if:
     * (1) all outputs claimed (as inputs) by {@code tx} are in the current UTXO pool, 
     * (2) the signatures on each input of {@code tx} are valid, 
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
        
        Set<UTXO> utxoClaimed = new HashSet<UTXO>();

        Double totalInputValue = 0D;
        Double totalOuputValue = 0D;

        for (int inputIndex = 0; inputIndex < tx.numInputs(); inputIndex++) {

            boolean valid = false;

            Transaction.Input input = tx.getInput(inputIndex);
    
            UTXO temporal = new UTXO(input.prevTxHash, input.outputIndex);
            if (!this.pool.contains(temporal)) {
                /* Validation (1). If temporal is not in the pool the transaction is not valid */
                return false;
            } else {
    
                if (utxoClaimed.contains(temporal)) {
                    /* Validation (3) */
                    return false;
                } else {

                    utxoClaimed.add(temporal);
                }

                Transaction.Output previousOutput = this.pool.getTxOutput(temporal);
                
                if (!Crypto.verifySignature(previousOutput.address, tx.getRawDataToSign(inputIndex), input.signature)) {
                    /* Validation (2) */
                    valid = false;
                } else {
                   totalInputValue += previousOutput.value;
                   valid = true;
                }
            }

            if (!valid) {

                return false;
            }
        }

        boolean valid = true;
        for (Transaction.Output output : tx.getOutputs()) {

            if (output.value < 0) {

                valid = false;
            } else {

                totalOuputValue += output.value;
            }
        }

        if (!valid) return false;

        if (totalInputValue >= totalOuputValue) {
            /* Validation (5) */
            return true;
        }
        return false;
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        
        List<Transaction> validTransactions = new LinkedList<Transaction>();
        for (Transaction tx : possibleTxs) {

            if (this.isValidTx(tx)) {

                for (int index = 0; index < tx.numOutputs(); index++) {

                    this.pool.addUTXO(new UTXO(tx.getHash(), index), tx.getOutput(index));
                }

                validTransactions.add(tx);
            }
        }

        Transaction[] validTxAsArray = new Transaction[validTransactions.size()];
        for (int i = 0; i < validTransactions.size(); i++) {

            validTxAsArray[i] = validTransactions.get(i);
        }
        return validTxAsArray;
    }
}
