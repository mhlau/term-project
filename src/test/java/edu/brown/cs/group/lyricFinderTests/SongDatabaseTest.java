package edu.brown.cs.group.lyricFinderTests;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.brown.cs.group.lyricFinder.SongDatabase;

public class SongDatabaseTest {
  @BeforeClass
  public static void setUpClass() throws Exception {
    // (Optional) Code to run before any tests begin goes here.
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
    // (Optional) Code to run after all tests finish goes here.
  }

  @Before
  public void setUp() {
    // (Optional) Code to run before each test case goes here.
  }

  @After
  public void tearDown() {
    // (Optional) Code to run after each test case goes here.
  }

  @Test
  public void buildDatabaseTest() {
    try {
      @SuppressWarnings("unused")
      SongDatabase sdb = new SongDatabase("testDB.sqlite3", 10);
      File file = new File("testDB.sqlite3");
      assertTrue(file.exists());
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
