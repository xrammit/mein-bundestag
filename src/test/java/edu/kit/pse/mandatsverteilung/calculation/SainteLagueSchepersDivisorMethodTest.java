package edu.kit.pse.mandatsverteilung.calculation;

import edu.kit.pse.mandatsverteilung.PSETestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

public class SainteLagueSchepersDivisorMethodTest {
    
    private AbstractDivisorMethod<String> divisor = DivisorMethod.SainteLagueSchepers();
    private HashMap<String, Integer> entities;
    private HashMap<String, Integer> guaranteedAmount;
    private DivisorResult<String> result;

    @BeforeClass
    public static void setupLoggerConfiguration() {
        PSETestUtils.setupLoggerConfiguration();
    }

    @Before
    public void setUp() throws Exception {
        entities = new HashMap<String, Integer>();
        guaranteedAmount = new HashMap<String, Integer>();
    }

    @After
    public void tearDown() throws Exception {
        divisor = null;
        entities = null;
        guaranteedAmount = null;
        result = null;
    }

    @Test
    public void testDivideEmpty() throws MethodExecutionException {
        // using empty map
        result = divisor.divide(entities, 1);
        assertEquals(Collections.emptyMap(), result.getDividedEntities());
        assertEquals(Collections.emptySet(), result.getDrawEntities());
        assertEquals(0, result.getCountLeftToDivide());
    }
    
    @Test(expected = MethodExecutionException.class)
    public void testDivideLessThanGuaranteed() throws MethodExecutionException {
    	entities.put("A", 0);
    	guaranteedAmount.put("A", 11);
    	divisor.divide(entities, 10, guaranteedAmount);
    }
    
    @Test
    public void testDivideZero() throws MethodExecutionException {
        //using sum 0 to divide
        
        entities.put("A", 0);
        entities.put("B", 0);
        entities.put("C", 0);
        entities.put("D", 0);
        
        HashSet<String> resultSet = new HashSet<>();
        resultSet.addAll(entities.keySet());
        
        HashMap<String,Integer> resultMap = new HashMap<>();
        resultMap.put("A", 2);
        resultMap.put("B", 2);
        resultMap.put("C", 2);
        resultMap.put("D", 2);
        
        result = divisor.divide(entities, 10);
        assertEquals(resultMap, result.getDividedEntities());
        assertEquals(resultSet, result.getDrawEntities());
        assertEquals(2, result.getCountLeftToDivide());
    }
    
    @Test
    public void regrTestDivideFairCountLeft() throws MethodExecutionException {
        entities.put("A", 0);
        entities.put("B", 0);
        entities.put("C", 0);
        
        HashSet<String> resultSet = new HashSet<>();
        resultSet.addAll(entities.keySet());
        
        HashMap<String,Integer> resultMap = new HashMap<>();
        resultMap.put("A", 3);
        resultMap.put("B", 3);
        resultMap.put("C", 3);
        
        result = divisor.divide(entities, 10);
        assertEquals(resultMap, result.getDividedEntities());
        assertEquals(resultSet, result.getDrawEntities());
        assertEquals(1, result.getCountLeftToDivide());
    }

    @Test
    public void testDivideZeroGuaranties() throws MethodExecutionException {
        //using sum 0 to divide and guaranteed amounts
        
        entities.put("A", 0);
        entities.put("B", 0);
        entities.put("C", 0);
        entities.put("D", 0);
        
        guaranteedAmount.put("A", 3);
        guaranteedAmount.put("C", 4);
        guaranteedAmount.put("D", 2);
        guaranteedAmount.put("E", 100000);
        
        HashSet<String> resultSet = new HashSet<>();
        
        HashMap<String,Integer> resultMap = new HashMap<>();
        resultMap.put("A", 3);
        resultMap.put("B", 1);
        resultMap.put("C", 4);
        resultMap.put("D", 2);
        
        result = divisor.divide(entities, 10, guaranteedAmount);
        assertEquals(resultMap, result.getDividedEntities());
        assertEquals(resultSet, result.getDrawEntities());
        assertEquals(0, result.getCountLeftToDivide());
    }

    @Test
    public void testDivideBTW13() throws MethodExecutionException {
        //using data from the election of 2013 to divide
        
        entities.put("CDU", 14921877);
        entities.put("SPD", 11252215);
        entities.put("DIE LINKE", 3755699);
        entities.put("GRÜNE", 3694057);
        entities.put("CSU", 3243569);
        
        result = divisor.divide(entities, 631);
        
        HashMap<String, Integer> resultMap = new HashMap<String, Integer>();
        resultMap.put("CDU", 255);
        resultMap.put("SPD", 193);
        resultMap.put("DIE LINKE", 64);
        resultMap.put("GRÜNE", 63);
        resultMap.put("CSU", 56);
                
        assertEquals(resultMap, result.getDividedEntities());
        assertEquals(Collections.emptySet(), result.getDrawEntities());
        assertEquals(0, result.getCountLeftToDivide());
    }

    @Test
    public void testDivideRoundFlagZero() throws MethodExecutionException {
    	entities.put("A", 3);
    	entities.put("B", 9);
    	
    	result = divisor.divide(entities, 10);
    	
    	HashMap<String, Integer> resultMap = new HashMap<String, Integer>();
        resultMap.put("A", 2);
        resultMap.put("B", 7);
        
        HashSet<String> resultDraw = new HashSet<>();
        resultDraw.add("A");
        resultDraw.add("B");
        
        assertEquals(resultMap, result.getDividedEntities());
        assertEquals(resultDraw, result.getDrawEntities());
        assertEquals(1, result.getCountLeftToDivide());
    }

    @Test
    public void testDivideMergeDraws() throws MethodExecutionException {
    	entities.put("A", 2);
    	entities.put("B", 1);
    	entities.put("C", 1);

        result = divisor.divide(entities, 1);
        
        HashMap<String, Integer> resultMap = new HashMap<String, Integer>();
        resultMap.put("A", 1);
        resultMap.put("B", 0);
        resultMap.put("C", 0);
        
        assertEquals(resultMap, result.getDividedEntities());
        assertEquals(Collections.emptySet(), result.getDrawEntities());
        assertEquals(0, result.getCountLeftToDivide());
    }

    @Test
    public void testDivideAddSeatsDraw() throws MethodExecutionException {
    	entities.put("A", 4);
    	entities.put("B", 3);
    	entities.put("C", 2);
    	entities.put("D", 2);
        
        result = divisor.divide(entities, 30);
        
        HashMap<String, Integer> resultMap = new HashMap<String, Integer>();
        resultMap.put("A", 11);
        resultMap.put("B", 8);
        resultMap.put("C", 5);
        resultMap.put("D", 5);
        
        HashSet<String> resultDraw = new HashSet<>();
        resultDraw.add("C");
        resultDraw.add("D");
        
        assertEquals(resultMap, result.getDividedEntities());
        assertEquals(resultDraw, result.getDrawEntities());
        assertEquals(1, result.getCountLeftToDivide());
    }
    
    @Test
    public void testDivideAddSeatsIncDraw() throws MethodExecutionException {
        entities.put("A", 6);
        entities.put("B", 6);
        entities.put("C", 6);
        entities.put("D", 1);
        entities.put("E", 1);
        entities.put("F", 1);
        entities.put("G", 1);
        entities.put("H", 1);
        entities.put("I", 1);
        
        result = divisor.divide(entities, 10);
        
        HashMap<String, Integer> resultMap = new HashMap<String, Integer>();
        resultMap.put("A", 3);
        resultMap.put("B", 3);
        resultMap.put("C", 3);
        resultMap.put("D", 0);
        resultMap.put("E", 0);
        resultMap.put("F", 0);
        resultMap.put("G", 0);
        resultMap.put("H", 0);
        resultMap.put("I", 0);
        
        HashSet<String> resultDraw = new HashSet<>();
        resultDraw.add("D");
        resultDraw.add("E");
        resultDraw.add("F");
        resultDraw.add("G");
        resultDraw.add("H");
        resultDraw.add("I");
        
        assertEquals(resultMap, result.getDividedEntities());
        assertEquals(resultDraw, result.getDrawEntities());
        assertEquals(1, result.getCountLeftToDivide());
    }
    
    @Test
    public void regrTestDivideTakeSeatsCountLeft() throws MethodExecutionException {
        entities.put("A", 4);
        entities.put("B", 4);
        entities.put("C", 4);
        entities.put("D", 4);
        entities.put("E", 4);
        entities.put("F", 4);
        
        result = divisor.divide(entities, 10);
        
        HashMap<String, Integer> resultMap = new HashMap<String, Integer>();
        resultMap.put("A", 1);
        resultMap.put("B", 1);
        resultMap.put("C", 1);
        resultMap.put("D", 1);
        resultMap.put("E", 1);
        resultMap.put("F", 1);
        
        HashSet<String> resultDraw = new HashSet<>();
        resultDraw.addAll(entities.keySet());
        
        assertEquals(resultMap, result.getDividedEntities());
        assertEquals(resultDraw, result.getDrawEntities());
        assertEquals(4, result.getCountLeftToDivide());
    }

    @Test
    public void testDivideAddSeatsMult() throws MethodExecutionException {
    	entities.put("A", 4);
    	entities.put("B", 3);
    	entities.put("C", 2);
        
        result = divisor.divide(entities, 10);
        
        HashMap<String, Integer> resultMap = new HashMap<String, Integer>();
        resultMap.put("A", 5);
        resultMap.put("B", 3);
        resultMap.put("C", 2);
        
        assertEquals(resultMap, result.getDividedEntities());
        assertEquals(Collections.emptySet(), result.getDrawEntities());
        assertEquals(0, result.getCountLeftToDivide());
    }

    @Test
    public void testDivideTakeSeatsDraw() throws MethodExecutionException {
    	entities.put("A", 4000);
    	entities.put("B", 4000);
    	entities.put("C", 2000);
    	entities.put("D", 1);
        
        result = divisor.divide(entities, 19);
        
        HashMap<String, Integer> resultMap = new HashMap<String, Integer>();
        resultMap.put("A", 7);
        resultMap.put("B", 7);
        resultMap.put("C", 4);
        resultMap.put("D", 0);
        
        HashSet<String> resultDraw = new HashSet<>();
        resultDraw.add("A");
        resultDraw.add("B");
        
        assertEquals(resultMap, result.getDividedEntities());
        assertEquals(resultDraw, result.getDrawEntities());
        assertEquals(1, result.getCountLeftToDivide());
    }

    @Test
    public void testDivideTakeSeatsMult() throws MethodExecutionException {
    	entities.put("A", 10); 
    	entities.put("B", 8);
    	entities.put("C", 16);
    	entities.put("D", 8);
    	
    	guaranteedAmount.put("A", 5);
    	guaranteedAmount.put("B", 3);
    	guaranteedAmount.put("C", 4);
    	guaranteedAmount.put("D", 0);
    	
    	HashMap<String, Integer> resultMap = new HashMap<String, Integer>();
    	resultMap.putAll(guaranteedAmount);
        guaranteedAmount.put("E", 100000);
    	
    	result = divisor.divide(entities, 12, guaranteedAmount);

        assertEquals(resultMap, result.getDividedEntities());
        assertEquals(Collections.emptySet(), result.getDrawEntities());
        assertEquals(0, result.getCountLeftToDivide()); 
    }
}
