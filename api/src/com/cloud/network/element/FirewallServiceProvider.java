package com.cloud.network.element;

import java.util.List;

import com.cloud.exception.ResourceUnavailableException;
import com.cloud.network.Network;
import com.cloud.network.rules.FirewallRule;

public interface FirewallServiceProvider extends NetworkElement {
    /**
     * Apply rules
     * @param network
     * @param rules
     * @return
     * @throws ResourceUnavailableException
     */
    boolean applyFWRules(Network network, List<? extends FirewallRule> rules) throws ResourceUnavailableException;
}
