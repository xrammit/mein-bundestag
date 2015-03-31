package edu.kit.pse.mandatsverteilung.imExport;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.kit.pse.mandatsverteilung.model.candidate.CandidateBuilder;
import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrRepublic;

public class CandidateFormat2013Test {
        CandidateBuilder builder2;
        VoteDistrRepublic votes2;

        @Before
        public void setUp() throws IOException, ImporterException {
            votes2 = ImExporter.importVoteDistribution(new File("src/test/resources/Stimmen2013.csv"));
            builder2 = new CandidateBuilder(votes2);
        }

        
        @Test
        public void emptyColumnsTest() throws IOException, ImporterException {
            ImExporter.importCandidates(new File(
                    "src/test/resources/candidateImporter/wahlbewerber2013EmptyColumns.csv"), builder2);

        }
        
        @Test(expected = IllegalArgumentException.class)
        public void FileIsNullTest() throws IOException, ImporterException {
            ImExporter.importCandidates(null, builder2);

        }
        
        @Test(expected = ImporterException.class)
        public void wardNotANumberTest() throws IOException, ImporterException {
            ImExporter.importCandidates(new File(
                    "src/test/resources/candidateImporter/wahlbewerber2013WardNotANumber.csv"), builder2);

        }
        
        @Test(expected = ImporterException.class)
        public void posNotANumber() throws IOException, ImporterException {
            ImExporter.importCandidates(new File(
                    "src/test/resources/candidateImporter/wahlbewerber2013PosNotANumber.csv"), builder2);

        }
        @Test(expected = ImporterException.class)
        public void NoDirectNorListTest() throws IOException, ImporterException {
            ImExporter.importCandidates(new File(
                    "src/test/resources/candidateImporter/wahlbewerber2013NoDirectNorList.csv"), builder2);

        }
        @Test(expected = ImporterException.class)
        public void DoubleColumnsTest() throws IOException, ImporterException {
            ImExporter.importCandidates(new File(
                    "src/test/resources/candidateImporter/wahlbewerber2013DoubleColumns.csv"), builder2);

        }
        @Test(expected = ImporterException.class)
        public void ListAndDirectWithoutCorrectPartyTest() throws IOException, ImporterException {
            ImExporter.importCandidates(new File(
                    "src/test/resources/candidateImporter/wahlbewerber2013BothWithoutCorrectParty.csv"), builder2);

        }
        @Test(expected = ImporterException.class)
        public void ListWithoutCorrectPartyTest() throws IOException, ImporterException {
            ImExporter.importCandidates(new File(
                    "src/test/resources/candidateImporter/wahlbewerber2013ListWithoutCorrectParty.csv"), builder2);

        }
        
        @Test(expected = ImporterException.class)
        public void DirectWithoutCorrectPartyTest() throws IOException, ImporterException {
            ImExporter.importCandidates(new File(
                    "src/test/resources/candidateImporter/wahlbewerber2013DirectWithoutCorrectParty.csv"), builder2);
        }
        
        @After
        public void tearDown() {
            builder2 = null;
        }
}
