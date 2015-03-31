package edu.kit.pse.mandatsverteilung.model.votedistr;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Any kind of distribution of votes.
 * Known Subclasses: VoteDistrRepublic, VoteDistrState, VoteDistrWard
 * @author Benedict
 * @param <K> the type of Object over which the Votes are distributed
 * @param <V> the type of VoteContainer that is being distributed. This should be specified to a final class in the extending class.
 */
abstract class VoteDistr<K extends Comparable<K>, V extends VoteContainer> implements VoteContainer {

    private final Map<K, V> map;
    
    /**
     * creates a fixed mapping representing the distribution of votes over Ks
     * @param map the data contained in this mapping
     */
    VoteDistr(Map<? extends K, ? extends V> map) {
        this.map = new TreeMap<K, V>();
        map.forEach(this.map::put);
    }

    @Override
    public int getFirst() {
        int sum = 0;
        for (VoteContainer vc : map.values()) {
            sum += vc.getFirst();
        }
        return sum;
    }
    
    @Override
    public int getSecond() {
        int sum = 0;
        for (VoteContainer vc : map.values()) {
            sum += vc.getSecond();
        }
        return sum;
    }
    
    @Override
    public int getFirst(Party party) {
        int sum = 0;
        for (VoteContainer vc : map.values()) {
            sum += vc.getFirst(party);
        }
        return sum;
    }
    
    @Override
    public int getSecond(Party party) {
        int sum = 0;
        for (VoteContainer vc : map.values()) {
            sum += vc.getSecond(party);
        }
        return sum;
    }
    
    public Set<K> getKeySet() {
        return new HashSet<K>(map.keySet());// clone since otherwise remove would affect the map
    }
    
    /**
     * @param key
     * @return the VoteDistr-Object belonging to the specified Key
     */
    public V get(K key) {
        return map.get(key);
    }
    
    @Override
    public String toString() {
        String out = this.getClass().getSimpleName() + ":{";
        for (VoteContainer vc : map.values()) {
            out += vc.toString();
        }
        out += "}";
        return out;
    }
}
