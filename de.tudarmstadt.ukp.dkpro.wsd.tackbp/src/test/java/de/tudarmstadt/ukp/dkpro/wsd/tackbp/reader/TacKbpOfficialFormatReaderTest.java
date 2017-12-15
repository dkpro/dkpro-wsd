/*******************************************************************************
 * Copyright 2017
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package de.tudarmstadt.ukp.dkpro.wsd.tackbp.reader;



import java.io.File;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.wsd.tackbp.reader.TacKbpOfficialFormatReader;

import static junit.framework.Assert.*;

public class TacKbpOfficialFormatReaderTest {

	private static String tacBaseFolder = "src/test/resources/tacKbp/";
	private static String tacDocumentCollection = tacBaseFolder + "document_collection";

	private TacKbpOfficialFormatReader reader;
	
	@Before
	public void setUp(){
		reader = new TacKbpOfficialFormatReader();
		reader.initializeForTests( tacDocumentCollection);

	}
	
	@Test
	@Ignore
	public void testTacEval2009() throws Exception{
		File file = reader.getDocument("LTW_ENG_20081121.0069.LDC2009T13");
		assertNotNull(file);
		assertTrue(file.exists());
		String[] content = reader.getDocumentContent(file);
		assertNotNull(content);
		assertEquals(2, content.length);
		assertEquals("Egypt Calls Meetings to Discuss Stopping Piracy", content[0]);
		assertTrue(content[1].startsWith("The Cairo meeting was called amid concerns that lawlessness was disrupti"));
	}

	@Test
	@Ignore
	public void testTacEval2009b() throws Exception{
		File file = reader.getDocument("LTW_ENG_19960311.0047.LDC2007T07");
		assertNotNull(file);
		assertTrue(file.exists());
		String[] content = reader.getDocumentContent(file);
		assertNotNull(content);
		assertEquals(2, content.length);
		assertEquals("Peres, Arafat Feel the Heat Melman, a journalist for the daily Ha'aretz, specializes in intelligence and terror affairs.", content[0]);
		assertTrue(content[1].startsWith("A few days ago, a crew from Israel's "));
	}

	
	@Test
	@Ignore
	public void testTacEval2010a() throws Exception{
		File file = reader.getDocument("eng-WL-11-174611-12972197");
		assertNotNull(file);
		assertTrue(file.exists());
		String[] content = reader.getDocumentContent(file);
		assertNotNull(content);
		assertEquals(2, content.length);
		assertEquals("NY Court Upholds Marriage Recognition", content[0]);
		assertTrue(content[1].startsWith("Today the anti-gay Alliance Defense Fund lost their challenge to New York"));
	}

	@Test
	@Ignore
	public void testTacEval2010b() throws Exception{
		File file = reader.getDocument("AFP_ENG_20081016.0074.LDC2009T13");
		assertNotNull(file);
		assertTrue(file.exists());
		String[] content = reader.getDocumentContent(file);
		assertNotNull(content);
		assertEquals(2, content.length);
		assertEquals("Venezuela buying Russian tanks, armored vehicles", content[0]);
		assertTrue(content[1].startsWith("Venezuela is buying more Russian weapons,"));
	}

	@Test
	@Ignore
	public void testTacTrain2010() throws Exception{
		File file = reader.getDocument("eng-WL-11-174611-12975322");
		assertNotNull(file);
		assertTrue(file.exists());
		String[] content = reader.getDocumentContent(file);
		assertNotNull(content);
		assertEquals(2, content.length);
		assertEquals("Gay Marriage: Tipping Point?", content[0]);
		assertTrue(content[1].startsWith("You'll have to embiggen this graph from FiveThirtyEight to make sense of it. Jeff Lax and Justin Phillips"));
	}

	@Test
	@Ignore
	public void testTacEval2011() throws Exception{
		File file = reader.getDocument("APW_ENG_20070516.0679.LDC2009T13");
		assertNotNull(file);
		assertTrue(file.exists());
		String[] content = reader.getDocumentContent(file);
		assertNotNull(content);
		assertEquals(2, content.length);
		assertEquals("U.S. Geological Survey reports 6.1 magnitude earthquake in western Laos", content[0]);
		assertTrue(content[1].startsWith("BANGKOK, Thailand 2007-05-16 11:02:22 UTC\nAn"));
	}
	
//	@Test
	@Ignore
	public void testTac0() throws Exception{
		File file = reader.getDocument("eng-NG-31-129395-12101307");
		assertNotNull(file);
		assertTrue(file.exists());
		String[] content = reader.getDocumentContent(file);
		assertNotNull(content);
		assertEquals(2, content.length);
		assertEquals("BHPA Club Coach Course - Aberdeen - 14/15 Feb 2009", content[0]);
		assertTrue(content[1].startsWith("Folks, the BHPA Club Coach is definitely"));
	}
	
	@Test
	@Ignore
	public void testTac1() throws Exception{
		File file = reader.getDocument("juancole.com_juancole_20050926063000_ENG_20050926_063000.LDC2006E32");
		assertNotNull(file);
		assertTrue(file.exists());
		String[] content = reader.getDocumentContent(file);
		assertNotNull(content);
		assertEquals(2, content.length);
		assertEquals("British Spies in Basra Fighting Weapons Smugglers 27 Killed, 62 Wounded in Violence", content[0]);
		assertTrue(content[1].startsWith("Those two SAS special operations troops captured by the Basra p"));
	}

}
