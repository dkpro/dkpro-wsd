package de.tudarmstadt.ukp.dkpro.wsd.wrapper;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

public class LinkDatabaseLinkMeasureDisambiguatorTest {

	@Test
	public void disambiguateTest() throws IOException {
		String input = "Please disambiguate this text.";
		
		Disambiguator disambiguator = new LinkDatabaseLinkMeasureDisambiguator();
		List<String> senses = disambiguator.disambiguate(input);
		System.out.println(senses);

		List<String> goldSenses = new ArrayList<String>();
		goldSenses.add("word_sense_disambiguation");
		
		assertEquals(goldSenses.size(), senses.size());
		for(String sense : senses){
			assertTrue(goldSenses.contains(sense));
		}
	}
}
