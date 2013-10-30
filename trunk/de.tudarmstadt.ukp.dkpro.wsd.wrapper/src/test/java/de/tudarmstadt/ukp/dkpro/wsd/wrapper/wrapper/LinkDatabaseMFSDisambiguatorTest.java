package de.tudarmstadt.ukp.dkpro.wsd.wrapper.wrapper;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

public class LinkDatabaseMFSDisambiguatorTest {

	@Test
	@Ignore
	public void disambiguateTest() throws IOException {
		String input = "Please disambiguate this text.";
		
		LinkDatabaseMFSDisambiguator disambiguator = new LinkDatabaseMFSDisambiguator();
		List<String> senses = disambiguator.disambiguate(input);
		System.out.println(senses);

		List<String> goldSenses = new ArrayList<String>();
		goldSenses.add("word_sense_disambiguation");
		goldSenses.add("text_user_interface");
		
		assertEquals(goldSenses.size(), senses.size());
		for(String sense : senses){
			assertTrue(goldSenses.contains(sense));
		}
	}
}
