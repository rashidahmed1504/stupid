package com.noor.silly.strats.domain;

import com.noor.silly.strats.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by tayyibah on 05/11/2016.
 */
public class Result {

    public static final Logger log = LoggerFactory.getLogger(Utils.class);

    private final double startWithXToRisk;
    private double totalAmountWin;
    private double totalAmountLoose;

    private int winCount;
    private int looseCount;
    private List<String> data;
    private double winForThisDataSet;
    private double looseForThisDataSet;
    private String key;

    public Result(double startWithXToRisk){
        this.startWithXToRisk = startWithXToRisk;
    }

    public int getWinCount() {
        return winCount;
    }

    public void setWinCount(int winCount) {
        this.winCount = winCount;
    }



    public void setLooseCount(int looseCount) {
        this.looseCount = looseCount;
    }

    public int getLooseCount() {
        return looseCount;
    }

    public double getTotalAmountWin() {
        return totalAmountWin;
    }

    public void setTotalAmountWin(double totalAmountWin) {
        this.totalAmountWin = totalAmountWin;
    }

    public double getTotalAmountLoose() {
        return totalAmountLoose;
    }

    public void setTotalAmountLoose(double totalAmountLoose) {
        this.totalAmountLoose = totalAmountLoose;
    }

    public void addDataSet(List<String> dataWL) {
        this.data = dataWL;
    }

    public void setWinThisDataSet(double acWinsAmount) {
        this.winForThisDataSet = acWinsAmount;
    }

    public void setLooseThisDataset(double acRiskAmount) {
        this.looseForThisDataSet = acRiskAmount;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        winForThisDataSet = Utils.getPercentToXDecimal(winForThisDataSet, 2);
        looseForThisDataSet = Utils.getPercentToXDecimal(looseForThisDataSet, 2);
        totalAmountWin = Utils.getPercentToXDecimal(totalAmountWin, 2);
        totalAmountLoose = Utils.getPercentToXDecimal(totalAmountLoose, 2);

        String xx = //"data : " + data +
                "Initial Amount £= " + startWithXToRisk + "\twinCount=" + winCount +"\tlooseCount=" + looseCount +"\ttotalPlayed=" + (winCount + looseCount)
                + "\twinAmount=" + winForThisDataSet + "\tlooseAmount=" + looseForThisDataSet + "\tDIFFERENCE=" + Utils.getPercentToXDecimal((winForThisDataSet - looseForThisDataSet), 2)
                + "\twinTotal=" + totalAmountWin+ "\tlooseTotal=" + totalAmountLoose + "\tDIFFERENCE_TOTAL=" + Utils.getPercentToXDecimal((totalAmountWin - totalAmountLoose), 2)
                + "  \tData=" + data + "\tW=" + calculateWinCount(data) + ", Key=" + key
                + "";

        return xx;
    }

    private int calculateWinCount(List<String> data) {int totalWC = 0;
        int wc = 0;
        for(String w: data){
            if(w.equals("W")){
                wc++;
            }
        }
        return wc;
    }

}
