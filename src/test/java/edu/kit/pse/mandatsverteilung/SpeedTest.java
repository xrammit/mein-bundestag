package edu.kit.pse.mandatsverteilung;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import edu.kit.pse.mandatsverteilung.calculation.MethodExecutionException;
import edu.kit.pse.mandatsverteilung.calculation.MethodExecutionResult;
import edu.kit.pse.mandatsverteilung.calculation.MethodExecutor;
import edu.kit.pse.mandatsverteilung.calculation.MethodExecutorFactory;
import edu.kit.pse.mandatsverteilung.imExport.ImExporter;
import edu.kit.pse.mandatsverteilung.imExport.ImporterException;
import edu.kit.pse.mandatsverteilung.model.candidate.CandidateBuilder;
import edu.kit.pse.mandatsverteilung.model.candidate.CandidateManager;
import edu.kit.pse.mandatsverteilung.model.seatdistr.SeatDistr;
import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrRepublic;

public class SpeedTest {

    public static void main(String[] args) throws IOException, ImporterException, MethodExecutionException {
        PSETestUtils.setupLoggerConfiguration();
        long start = 0;
        long end = 0;
        VoteDistrRepublic vd = null;
        CandidateManager cm = null;
        MethodExecutionResult result = null;
        SeatDistr sd = null;
        final int times = 100;
        
        
        //vd = ImExporter.importVoteDistribution(new File("src/test/resources/Stimmen2009.csv"), "2009");
        //WTF???
        start = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            vd = ImExporter.importVoteDistribution(new File("src/test/resources/Stimmen2009.csv"), "2009");
        }
        end = System.currentTimeMillis();
        System.out.println("Import Stimmverteilung: " + ((double)(end - start)) / times + "ms");
        
        start = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            cm = ImExporter.importCandidates(new File("src/test/resources/Wahlbewerber2009.csv"), new CandidateBuilder(vd));
        }
        end = System.currentTimeMillis();
        System.out.println("Import Kanidaten: " + ((double)(end - start)) / times + "ms");
        
        start = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            result = MethodExecutorFactory.createElection2009MethodExecutor(vd, cm).executeMethod();
        }
        end = System.currentTimeMillis();
        System.out.println("Berrechnung: " + ((double)(end - start)) / times + "ms");
        
        sd = result.getSeatDistr();
        start = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            ImExporter.exportSeatDistr(new File("test.csv"), vd, sd, i + "");
        }
        end = System.currentTimeMillis();
        System.out.println("Export: " + ((double)(end - start)) / times + "ms");
        
        
        start = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            vd = ImExporter.importVoteDistribution(new File("src/test/resources/Stimmen2013.csv"), "2013");
        }
        end = System.currentTimeMillis();
        System.out.println("Import Stimmverteilung: " + ((double)(end - start)) / times + "ms");
        
        vd = PSETestUtils.get2013();
        start = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            cm = ImExporter.importCandidates(new File("src/test/resources/Wahlbewerber2013.csv"), new CandidateBuilder(vd));
        }
        end = System.currentTimeMillis();
        System.out.println("Import Kanidaten: " + ((double)(end - start)) / times + "ms");
        
        start = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            result = MethodExecutorFactory.createElection2013MethodExecutor(vd, cm).executeMethod();
        }
        end = System.currentTimeMillis();
        System.out.println("Berrechnung: " + ((double)(end - start)) / times + "ms");
        
        sd = result.getSeatDistr();
        start = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            ImExporter.exportSeatDistr(new File("test.csv"), vd, sd, i + "");
        }
        end = System.currentTimeMillis();
        System.out.println("Export: " + ((double)(end - start)) / times + "ms");
        
    }
    
}
