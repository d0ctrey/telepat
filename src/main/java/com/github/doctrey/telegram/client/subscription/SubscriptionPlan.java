package com.github.doctrey.telegram.client.subscription;

/**
 * Created by s_tayari on 4/16/2018.
 */
public class SubscriptionPlan {

    private int id;
    private int requiredMember;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRequiredMember() {
        return requiredMember;
    }

    public void setRequiredMember(int requiredMember) {
        this.requiredMember = requiredMember;
    }
}
