package demo.basic_paxos.proposer;

import demo.basic_paxos.ProposalID;

/**
 * Created by: patrick.jiang@activenetwork.com
 * Created on:  16:54 2018/7/6.
 */
public interface BasicProposer {
    void setProposal(Object value);
    
    void prepare();
    
    void receivePromise(String fromUID, ProposalID proposalID, ProposalID prevAcceptedID, Object prevAcceptedValue);
    
}
