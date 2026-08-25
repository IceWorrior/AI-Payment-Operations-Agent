package com.paymentagent.model;

import java.util.List;

public class RiskAnalysis{

    private int riskScore;
    private String riskLevel;
    private List<String> reasons;

    public RiskAnalysis(
        int riskScore,
        String riskLevel,
        List<String> reasons
    ){

        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.reasons = reasons;

    }

    public int getRiskScore(){
        return riskScore;
    }

    public String getRiskLevel(){
        return riskLevel;
    }

    public List<String> getReasons(){
        return reasons;
    }
}