package edu.kit.pse.mandatsverteilung.imExport;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrRepublic;

public class VoteDistrImporterTests2002 {


    @Test(expected = ImporterException.class)
    public void FalseFormatTest() throws IOException, ImporterException {
            ImExporter.importVoteDistribution(new File("src/test/resources/Stimmen2009.csv"), "1900");
    }

    @Test
    public void test2002NumberofFirst1() throws IOException, ImporterException {
        VoteDistrRepublic vote = ImExporter.importVoteDistribution(new File("src/test/resources/btw2002wkr.csv"), "2002");
        assertEquals(47798608, vote.getFirst());
    }
    
    @Test(expected = ImporterException.class)
    public void testFirstVoteNoNumber() throws IOException, ImporterException {
            new VoteDistrImporterFormat2002(new File("src/test/resources/btw2002wkrFirstVoteNoNumber.csv")).importVoteDistr();

    }
    
    @Test(expected = ImporterException.class)
    public void testWardIDNoNumber() throws IOException, ImporterException {
            new VoteDistrImporterFormat2002(new File("src/test/resources/btw2002wkrWardIDNoNumber.csv")).importVoteDistr();

    }
    
    @Test(expected = ImporterException.class)
    public void testSecondVoteNoNumber() throws IOException, ImporterException {
            new VoteDistrImporterFormat2002(new File("src/test/resources/btw2002SecondVoteNoNumber.csv")).importVoteDistr();

    }
    

    
    @Test(expected = ImporterException.class)
    public void testWardIDNotUnique() throws IOException, ImporterException {
            new VoteDistrImporterFormat2002(new File("src/test/resources/btw2002wkrWardIDNotUnique.csv")).importVoteDistr();

    }
}
