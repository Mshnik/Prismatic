package test;

import static org.junit.Assert.*;

import models.Hex;

import org.junit.Test;

public class ModelsTest {

  @Test
  public void testContstants() {
    //Good god I hope this never gets changed... just in case
    assertEquals(Hex.SIDES, Hex.NEIGHBOR_COORDINATES.length);
  }

}
