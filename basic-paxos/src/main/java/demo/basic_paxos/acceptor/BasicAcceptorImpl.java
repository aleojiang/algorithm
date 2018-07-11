package demo.basic_paxos.acceptor;

import demo.basic_paxos.EssentialMessenger;
import demo.basic_paxos.ProposalID;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by: patrick.jiang@activenetwork.com
 * Created on:  16:58 2018/7/6.
 */
@Getter
@Setter
@Slf4j
@RequiredArgsConstructor(staticName = "of")
public class BasicAcceptorImpl implements BasicAcceptor {
    @NonNull
    private EssentialMessenger messenger;
    
    private ProposalID promisedID;
    private ProposalID acceptedID;
    private Object acceptedValue;
    
    @Override
    public void receivePrepare(String fromUID, ProposalID proposalID) {
        if (proposalID.equals(promisedID)) { // duplicate message
            messenger.sendPromise(fromUID, proposalID, acceptedID, acceptedValue);
        } else if (this.promisedID == null || proposalID.isGreaterThan(promisedID)) {
            promisedID = proposalID;
            messenger.sendPromise(fromUID, proposalID, acceptedID, acceptedValue);
        }
    }
    
    @Override
    public void receiveAcceptRequest(String from, ProposalID proposalID, Object value) {
        if (promisedID == null || proposalID.isGreaterThan(promisedID) || proposalID.equals(promisedID)) {
            promisedID = proposalID;
            acceptedID = proposalID;
            acceptedValue = value;
            messenger.sendAccepted(acceptedID, acceptedValue);
        }
    }
    
}
