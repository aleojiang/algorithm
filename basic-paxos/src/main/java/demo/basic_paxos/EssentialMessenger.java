package demo.basic_paxos;

/**
 * Created by: patrick.jiang@activenetwork.com
 * Created on:  09:14 2018/7/10.
 */
public interface EssentialMessenger {
    void sendPrepare(ProposalID proposalID);
    
    void sendPromise(String proposerUID, ProposalID proposalID, ProposalID acceptedID, Object acceptedValue);
    
    void sendAccept(ProposalID proposalID, Object proposalValue);
    
    void sendAccepted(ProposalID proposalID, Object acceptedValue);
    
    void onResolution(ProposalID proposalID, Object value);
}
