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

package de.tudarmstadt.ukp.dkpro.wsd.graphconnectivity.iterative.wikipedia.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;

/**
 * A cache for incoming links. Increases speed dramatically.
 * 
 * @author nico.erbs@gmail.com
 *
 */
public class IncomingLinksCache extends HashMap<String, List<String>> {

	private static String CACHE_FILE_PATH = "/srv/experiments/ned/aida/incomingLinksCache.ser";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static IncomingLinksCache cache;


	public static void initialize(){
		if(cache == null){
			//try de-serializing first
			cache = deSerialize();
			if(cache == null){
				cache = new IncomingLinksCache();
			}
		}
	}

	public static void add(String article, List<String> sources){
		cache.put(article, sources);
	}

	public static boolean contains(String article){
		return cache.containsKey(article);
	}

	public static List<String> getSources(String article){
		return cache.get(article);
	}

	public static void serialize() {
		try{

			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(CACHE_FILE_PATH)));
			oos.writeObject(cache);
			oos.flush();
			oos.close();
		}
		catch(Exception e) {
			System.out.println("Exception during serialization: " + e);
			e.printStackTrace();
		} 

	}

	public static IncomingLinksCache deSerialize() {
		IncomingLinksCache cache = null;
		try{
			ObjectInputStream in = new ObjectInputStream( 
					new FileInputStream(CACHE_FILE_PATH));
			cache = (IncomingLinksCache) in.readObject();
			in.close();
		} catch(Exception e){
			System.out.println("Exception during serialization: " + e);
		}
		return cache;

	}

	public static String getCACHE_FILE_PATH() {
		return CACHE_FILE_PATH;
	}

	public static void setCACHE_FILE_PATH(String cACHE_FILE_PATH) {
		CACHE_FILE_PATH = cACHE_FILE_PATH;
	}

}
