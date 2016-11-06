package com.noor.silly.strats.strategy;

import com.noor.silly.strats.domain.Result;
import com.noor.silly.strats.utils.FileUtils;
import com.noor.silly.strats.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SillyStrat {

	public static final Logger log = LoggerFactory.getLogger(SillyStrat.class);

	public static void main(String[] args) {

		log.warn("\n\n--------------New Entry-------------");
		log.warn("Time : " + Calendar.getInstance().getTime());
		//-----------------------------------
		//Configurations for data generation
		//-----------------------------------

		int groupSize = 3;
		double winsEveryXNumber = 2;
		double startWithXToRisk = 160;
		double doubleOrTriple = 2;

		int numberOfInstruments = 1;	//Gbp/usd, eur/usd etc
		int monthsToSimulate = 12;
		int daysPerMonth = 21;

		//If we want to stop as soon as win X number of times in a row
		boolean stopOnceTargetWinsReached = true;
		int stopAtConsecutiveWins = 3;

		//-----------------------------------
		//Configuraiton finished OR use file below
		//-----------------------------------

		//If we read from file, specify the file, file= real data
		boolean readDataFromFile = true;
		String fileKey = "gbp";
		if(readDataFromFile){
			groupSize = 3;
			startWithXToRisk = 240;
			doubleOrTriple = 2;

			numberOfInstruments = 1;
			monthsToSimulate = 22;
			daysPerMonth = 21;		//This needs to be 2 X if fileKey contains 2 currency
			if(fileKey.length() > 3)
				daysPerMonth = daysPerMonth * 2;

			stopOnceTargetWinsReached = true;
			stopAtConsecutiveWins = 3;
		}else{
			log.warn("GENERATE WITH SIMULATED DATA");
		}

		//-----------------------------------
		//Fiel Configuraiton finished
		//-----------------------------------

		//----------------------------------------------------
		// Calculation total place holders for all instruments
		//----------------------------------------------------
		int consecutiveWins = 0;
		double allWins = 0;
		double allRisks = 0;
		int allNumberOfWinsCount = 0;
		int allNumberOfLooseCount = 0;
		List<List<String>> allData = new ArrayList<List<String>>();
		List<Result> listOfResults = new ArrayList<Result>();

		//Generate win range between x and y
		int winRangeFrom = 4;
		int winRangeTo = 10;


		for (int nic = 0; nic < numberOfInstruments; nic++) {
			
			int numberOfMonthsCounter = monthsToSimulate;

			// Group size, X to risk at beginning of each group, if set to true
			// it will base it on SL and startWithXToRisk amount
			//boolean calculateStartXAgainstSL = false;
			//if (calculateStartXAgainstSL) {
			//	int sl = 25;
			//	startWithXToRisk = Utils.getInitialRiskAmount(groupSize, sl, calculateStartXAgainstSL);
			//}
			int toAdd = (int) (startWithXToRisk / 2);

			// Win rate calculation
			int numberOfDaysPerMonth = daysPerMonth;
			int monthCount = 0;
			//Do for each months
			do {
				monthCount++;
				String key = Utils.generateRandomKey("Key");
				int maxWinCount = Utils.someRandomValueBetween(winRangeFrom, winRangeTo);
				List<String> dataWL = null;
				if(readDataFromFile){
					dataWL = FileUtils.getDataFromFile(monthCount, numberOfDaysPerMonth, fileKey, "dataWL");
				}else{
					dataWL = Utils.getRandomDataWithXPercentOfWins(winsEveryXNumber, numberOfDaysPerMonth, maxWinCount);
				}
				allData.add(dataWL);

				double initialRiskAmount = 0;
				double currentValueOfXToRisk = startWithXToRisk;

				double acRiskAmount = startWithXToRisk;
				double acWinsAmount = 0;

				double sumOfDifference = 0;

				int count = 1;
				int winCount = 0;
				int looseCount = 0;

				Result result = new Result(startWithXToRisk);

				log.info(key);
				log.info("-----");

				//For each of the data set calculate
				for (String x : dataWL) {

					// Calculate doubling or halving
					if (x.equals("W")) {
						// Double it
						double preAmount = currentValueOfXToRisk;
						currentValueOfXToRisk = currentValueOfXToRisk * doubleOrTriple;
						currentValueOfXToRisk = Utils.getPercentToXDecimal(currentValueOfXToRisk, 2);
						log.info(x + ", " + preAmount + " = " + currentValueOfXToRisk);
						winCount++;
						consecutiveWins++;
					} else {
						// Half it
						double preAmount = currentValueOfXToRisk;
						currentValueOfXToRisk = currentValueOfXToRisk / 2;
						currentValueOfXToRisk = Utils.getPercentToXDecimal(currentValueOfXToRisk, 2);
						log.info(x + ", " + preAmount + " = " + currentValueOfXToRisk);
						looseCount++;
						if(consecutiveWins < stopAtConsecutiveWins)
						consecutiveWins = 0;
					}
					// log.info(total);

					// End of the group sum up results, reset
					if (count != 1 && count % groupSize == 0) {
						double diff = currentValueOfXToRisk - initialRiskAmount;
						if (initialRiskAmount == 0) {
							diff = diff - startWithXToRisk;
						}
						diff = Utils.getPercentToXDecimal(diff, 2);

						//
						if (currentValueOfXToRisk >= startWithXToRisk / 2) {
							sumOfDifference = sumOfDifference + diff;
							log.info("Difference : " + diff);
							acWinsAmount = acWinsAmount + currentValueOfXToRisk;
							acRiskAmount = acRiskAmount + initialRiskAmount;
							log.info("Win so far : " + Utils.getPercentToXDecimal(acWinsAmount - acRiskAmount, 2));

							currentValueOfXToRisk = startWithXToRisk + toAdd;
							initialRiskAmount = currentValueOfXToRisk;
						} else {
							sumOfDifference = sumOfDifference + diff;
							log.info("Difference : " + diff);
							acWinsAmount = acWinsAmount + currentValueOfXToRisk;
							acRiskAmount = acRiskAmount + initialRiskAmount;
							log.info("Win so far : " + Utils.getPercentToXDecimal(acWinsAmount - acRiskAmount, 2));

							currentValueOfXToRisk = startWithXToRisk;
							initialRiskAmount = currentValueOfXToRisk;
						}

						log.info("----------------------");

						//If we want to stop as soon as we win X number of times in a row
						if(stopOnceTargetWinsReached && consecutiveWins>=stopAtConsecutiveWins){
							consecutiveWins = 0;
							break;
						}

					}


					count++;

				} // Finished looping a single data set

				printResults(key, dataWL, startWithXToRisk, winCount+looseCount, winCount, acWinsAmount, acRiskAmount, sumOfDifference, "Data set summary");

				numberOfMonthsCounter--;

				allWins = allWins + acWinsAmount;
				allRisks = allRisks + acRiskAmount;
				allNumberOfWinsCount = allNumberOfWinsCount + winCount;
				allNumberOfLooseCount = allNumberOfLooseCount + looseCount;

				//Store results for reporting
				result.setWinCount(winCount);
				result.setLooseCount(looseCount);
				result.setTotalAmountWin(allWins);
				result.setTotalAmountLoose(allRisks);
				result.setWinThisDataSet(acWinsAmount);
				result.setLooseThisDataset(acRiskAmount);
				result.addDataSet(dataWL);

				listOfResults.add(result);

			} while (numberOfMonthsCounter > 0); // Finished all data sets

			//printAllWinsResults(null, allWins, allRisks, daysPerMonth, monthsToSimulate, allNumberOfWinsCount, "Sum Of All", nic);

		} // Finished FOR Loop

		printAllWinsResults(allData, allWins, allRisks, daysPerMonth, monthsToSimulate, allNumberOfWinsCount, "Sum Of All",
				numberOfInstruments - 1, allNumberOfLooseCount + allNumberOfWinsCount);

		printDataSet(listOfResults, allData);

		printAllWinsResults(allData, allWins, allRisks, daysPerMonth, monthsToSimulate, allNumberOfWinsCount, "Sum Of All",
				numberOfInstruments - 1, allNumberOfLooseCount + allNumberOfWinsCount);

		log.warn("--------------Entry Completed-------------");
	}

	private static void printDataSet(List<Result> listOfResults, List<List<String>> allData) {
		log.warn("\nData Sets : ");
		int totalWC = 0;
		if (allData != null) {
			for (List<String> x : allData) {
				int wc = 0;
				for(String w: x){
					if(w.equals("W")){
						wc++;
					}
				}
				log.warn("" + x + ", W=" + wc);
				totalWC = totalWC + wc;
				wc = 0;
			}

		}

		System.out.println();
		for(Result r: listOfResults){
			System.out.println(r);
		}

		int elements = allData.get(0).size() * allData.size();
		double percent = Utils.getPercentToXDecimal((totalWC * 1.0) / elements, 2)*100;
		log.warn("\nNumber of data sets : " + allData.size() + "");
		log.warn("Total number of W counts : " + (totalWC));
		log.warn("Total number of elements : " + (elements));
		log.warn("Actual win % : " + percent + "%\n");
	}

	private static void printAllWinsResults(List<List<String>> allData, double allWins, double allRisks,
			int numberOfDaysPerMonth, int months, int allNumberOfWinsCount, String message, int nic, int totalCount) {
		log.warn("\n-------" + message + "------- : ");
		double totalNDays = totalCount;
		double percent = Utils.getPercentToXDecimal( allNumberOfWinsCount / totalNDays, 2)*100;
		log.warn("ALL win  : " + Utils.getPercentToXDecimal(allWins,2));
		log.warn("ALL risk : " + Utils.getPercentToXDecimal(allRisks,2));
		log.warn("Difference : " + Utils.getPercentToXDecimal(allWins - allRisks, 2));
		log.warn("ALL win % : " + (allNumberOfWinsCount) + "/" + (totalNDays) + " = " + percent + "%");
		log.warn("-------" + message + "------- : ");

	}

	public static void printResults(String key, List<String> dataWL, double startWithXToRisk, int numberOfItems,
			int winCount, double acWinsAmount, double acRiskAmount, double sumOfDiffernce, String message) {

		log.warn("\n-------" + message + "------- : ");
		log.warn("Data : " + dataWL);
		log.warn("Key : " + key);
		log.warn("Win count : " + winCount);
		log.warn("Loss count : " + (numberOfItems - winCount));
		if(numberOfItems > 0)
		log.warn("W Percentage : " + ((winCount * 100 / numberOfItems)) + "%");
		log.warn("Starting Risk : " + startWithXToRisk);
		log.warn("Account W Amount : " + Utils.getPercentToXDecimal(acWinsAmount, 2));
		log.warn("Risk Amount : " + acRiskAmount);
		if (sumOfDiffernce < 0) {
			log.warn("Loss : " + Utils.getPercentToXDecimal(sumOfDiffernce, 2));
		} else {
			log.warn("Wins : " + Utils.getPercentToXDecimal(sumOfDiffernce, 2));
		}
		log.warn("-------" + message + "------- : ");
	}

}