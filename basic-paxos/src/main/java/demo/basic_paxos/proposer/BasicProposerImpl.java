package demo.basic_paxos.proposer;

import demo.basic_paxos.EssentialMessenger;
import demo.basic_paxos.ProposalID;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by: patrick.jiang@activenetwork.com
 * Created on:  09:18 2018/7/10.
 */
@Slf4j
@Getter
@Setter
@RequiredArgsConstructor(staticName = "of")
public class BasicProposerImpl implements BasicProposer {
    @NonNull
    protected EssentialMessenger messenger;
    
    protected String proposerUID;
    protected final int quorumSize;
    
    protected ProposalID proposalID;
    protected Object proposedValue = null;
    
    protected ProposalID lastAcceptedID = null;
    protected Set<String> promisesReceived = new HashSet<>();
    
    @Override
    public void setProposal(Object value) {
        if (proposedValue == null) {
            proposedValue = value;
        }
    }
    
    @Override
    public void prepare() {
        promisesReceived.clear();
        proposalID.incrementNumber();
        messenger.sendPrepare(proposalID);
    }
    
    @Override
    public void receivePromise(String fromUID, ProposalID proposalID,
            ProposalID prevAcceptedID, Object prevAcceptedValue) {
        
        if (!proposalID.equals(this.proposalID) || promisesReceived.contains(fromUID))
            return;
        
        promisesReceived.add(fromUID);
        
        if (lastAcceptedID == null || prevAcceptedID.isGreaterThan(lastAcceptedID)) {
            lastAcceptedID = prevAcceptedID;
            
            if (prevAcceptedValue != null)
                proposedValue = prevAcceptedValue;
        }
        
        if (promisesReceived.size() == quorumSize)
            if (proposedValue != null)
                messenger.sendAccept(this.proposalID, proposedValue);
    }
    
}
