package edu.kit.pse.mandatsverteilung.imExport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

import javafx.scene.chart.PieChart;
import edu.kit.pse.mandatsverteilung.model.candidate.CandidateBuilder;
import edu.kit.pse.mandatsverteilung.model.candidate.CandidateManager;
import edu.kit.pse.mandatsverteilung.model.seatdistr.SeatDistr;
import edu.kit.pse.mandatsverteilung.model.votedistr.VoteDistrRepublic;


public class ImExporter {

    final static int MAX_STRING_LENGTH = 100;

    
    /**
     * Imports with an Importer, which can interpret the data
     * 
     * @param file
     *            Vote distribution file to be imported.
     * @return
     * @throws IOException
     * @throws ImporterException
     */
    public static VoteDistrRepublic importVoteDistribution(File file)
            throws IOException, ImporterException {
        VoteDistrImporter i = new VoteDistrImporterFormat2013(file);
        VoteDistrImporter j = new VoteDistrImporterFormat2002(file);
        LinkedList<VoteDistrImporter> Impl = new LinkedList<VoteDistrImporter>();
        Impl.add(i);
        Impl.add(j);
        for (VoteDistrImporter v : Impl) {
            String header[] = null;
            header = v.buildHeader();
            if (header != null) {
                return v.importVoteDistr();
            }
        }
        throw new ImporterException(
                "The program couldn't recognize the format of the csv, the declarations of the headers may be incorrect!",
                KindOfException.UnknownFormat);
    }

    /**
     * Import with an Importer which could only import the datas of the
     * specified year
     * 
     * @param file
     *            Vote distribution file to be imported.
     * @param year
     *            The release year of the file.
     * @return
     * @throws IOException
     * @throws ImporterException
     */
    public static VoteDistrRepublic importVoteDistribution(File file,
            String year) throws IOException, ImporterException {
        if (year != null && year.equals("2013")) {
            return new VoteDistrImporterFormat2013(file).importVoteDistr();
        } else if (year != null && year.equals("2009")) {
            return new VoteDistrImporterFormat2013(file).importVoteDistr();
        } else if (year != null && year.equals("2002")) {
            return new VoteDistrImporterFormat2002(file).importVoteDistr();
        }

        throw new ImporterException("There is no Importer for this format",
                KindOfException.UnknownFormat);
    }

    /**
     * Imports candidates from a candidate CSV file.
     * 
     * @param file
     *            Candidates file to be imported.
     * @param builder
     *            The builder that builds the candidate object.
     * @return
     * @throws IOException
     * @throws ImporterException
     */
    public static CandidateManager importCandidates(File file,
            CandidateBuilder builder) throws IOException, ImporterException {
        CandImporter i = new CandImporterFormat2013(file, builder);
        CandImporter j = new CandImporterFormat2009(file, builder);
        LinkedList<CandImporter> Impl = new LinkedList<CandImporter>();
        Impl.add(i);
        Impl.add(j);
        for (CandImporter v : Impl) {
            String header[] = v.getHeader(v.getColProp());
            if (header != null) {
                return v.importCand();
            }
        }
        throw new ImporterException(
                "The programm couldn't recognize the format of the csv-data, pherhaps the declarations of the headers are incorrect!",
                KindOfException.UnknownFormat);

    }

    /**
     * Exports the vote distribution in a re-importable CSV format.
     * 
     * @param voteDistrRep
     *            The vote distribution to be exported.
     * @param file
     *            The file path where it is to be saved.
     * @throws IOException
     */
    public static void exportVoteDistr(VoteDistrRepublic voteDistrRep, File file)
            throws IOException {
        VoteDistributionExporter.exportVoteDistr(voteDistrRep, file);
    }

    /**
     * Exports the seat distribution into a CSV file.
     * 
     * @param file
     *            The file path where it is to be saved.
     * @param votes
     *            The associated vote distribution.
     * @param seatDistr
     *            The seat distribution that is to be exported.
     * @param headerInfo
     *            The information about the divisor method.
     * @throws UnsupportedEncodingException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void exportSeatDistr(File file, VoteDistrRepublic votes,
            SeatDistr seatDistr, String headerInfo)
            throws UnsupportedEncodingException, FileNotFoundException,
            IOException {
        SeatDistrExporter.export(file, votes, seatDistr, headerInfo);
    }

    /**
     * Exports the pie chart which is displayed in the GUI.
     * 
     * @param file
     *            The file where it is to be saved.
     * @param chart
     *            The chart that is displayed in the GUI.
     * @param kindOfImage
     *            The file type of the image. if not png or jpg it is png by
     *            default
     * @return
     * @throws IOException
     */
    public static void exportImage(File file, PieChart chart, String kindOfImage)
            throws IOException {
        ImageExporter.export(file, chart, kindOfImage);
    }

}
