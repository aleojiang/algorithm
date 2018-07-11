/**
 * Created by: patrick.jiang@activenetwork.com
 * Created on:  10:47 2018/7/10.
 */
package demo.basic_paxos;

/**
 * 基本概念:
 *
 * Proposer
 * - 提议发起者，处理客户端请求，将客户端的请求发送到集群中，以便决定这个值是否可以被批准。
 * Acceptor
 * - 提议批准者，负责处理接收到的提议，他们的回复就是一次投票，会存储一些状态来决定是否接收一个值。
 * Replica
 * - 节点或者副本，分布式系统中的一个server，一般是一台单独的物理机或者虚拟机，同时承担paxos中的提议者和接收者角色。
 * ProposalId：
 * - 每个提议都有一个编号，编号高的提议优先级高。
 * Paxos Instance
 * - Paxos中用来在多个节点之间对同一个值达成一致的过程，比如同一个日志序列号：logIndex，不同的logIndex属于不同的Paxos Instance。
 * AcceptedProposal
 * - 在一个Paxos Instance内，已经接收过的提议
 * AcceptedValue
 * - 在一个Paxos Instance内，已经接收过的提议对应的值。
 * MinProposal
 * - 在一个Paxos Instance内，当前接收的最小提议值，会不断更新
 */

