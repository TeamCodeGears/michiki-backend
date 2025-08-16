package com.michiki.michiki.plan.socket;

import com.michiki.michiki.plan.dto.OnlineMemberDto;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OnlineUserTracker {

    private final Map<Long, Set<OnlineMemberDto>> onlineUsersPerPlan = new ConcurrentHashMap<>();

    public void addUser(Long planId, OnlineMemberDto user) {
        onlineUsersPerPlan.computeIfAbsent(planId, k -> ConcurrentHashMap.newKeySet()).add(user);
    }

    public void removeUser(Long planId, OnlineMemberDto user) {
        Set<OnlineMemberDto> users = onlineUsersPerPlan.get(planId);
        if (users != null) {
            users.remove(user);
            if (users.isEmpty()) {
                onlineUsersPerPlan.remove(planId);
            }
        }
    }

    public Set<OnlineMemberDto> getUsers(Long planId) {
        return onlineUsersPerPlan.getOrDefault(planId, Collections.emptySet());
    }
}
