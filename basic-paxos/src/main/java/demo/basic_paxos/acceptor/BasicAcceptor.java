package demo.basic_paxos.acceptor;

import demo.basic_paxos.ProposalID;

/**
 * Created by: patrick.jiang@activenetwork.com
 * Created on:  16:52 2018/7/6.
 */
public interface BasicAcceptor {
    void receivePrepare(String from, ProposalID proposalID);
    
    void receiveAcceptRequest(String from, ProposalID proposalID, Object value);
}
