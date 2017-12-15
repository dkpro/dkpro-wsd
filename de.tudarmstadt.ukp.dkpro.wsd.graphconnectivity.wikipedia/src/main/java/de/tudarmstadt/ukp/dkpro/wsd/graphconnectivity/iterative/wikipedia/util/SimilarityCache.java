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
import java.util.Random;

/**
 * A cache for similarity values. Increases speed dramatically.
 *
 * @author nico.erbs@gmail.com
 *
 */
public class SimilarityCache extends HashMap<String, Double> {

	private static String CACHE_FILE_PATH = "/srv/experiments/ned/aida/cache.ser";

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static SimilarityCache cache;

	private static final String KEY_IDENTIFIER = "-::-";

	public static void initialize(){
		if(cache == null){
			cache = deSerialize();
			if(cache == null){
				cache = new SimilarityCache();
			}
		}
	}

	@SuppressWarnings("static-access")
	public static void put(String keyPart0, String keyPart1, double value){
		if(!cache.contains(keyPart0, keyPart1)){
			cache.put(keyPart0 + KEY_IDENTIFIER + keyPart1, value);
		}
	}

	public static double get(String keyPart0, String keyPart1){
		if(cache.containsKey(keyPart0 + KEY_IDENTIFIER + keyPart1)){
			return cache.get(keyPart0 + KEY_IDENTIFIER + keyPart1);
		}
		else if(cache.containsKey(keyPart1 + KEY_IDENTIFIER + keyPart0)){
			return cache.get(keyPart1 + KEY_IDENTIFIER + keyPart0);
		}
		return 0d;
	}

	public static boolean contains(String keyPart0, String keyPart1){
		return
				cache.containsKey(keyPart0 + KEY_IDENTIFIER + keyPart1) ||
				cache.containsKey(keyPart1 + KEY_IDENTIFIER + keyPart0);
	}

	public static void serialize() {
		try{

			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(CACHE_FILE_PATH + new Random().nextInt(2))));
			oos.writeObject(cache);
			oos.flush();
			oos.close();
		}
		catch(Exception e) {
			System.out.println("Exception during serialization: " + e);
			e.printStackTrace();
		}

	}

	public static SimilarityCache deSerialize() {
		SimilarityCache cache = null;
		try{
			ObjectInputStream in = new ObjectInputStream(
					new FileInputStream(CACHE_FILE_PATH));
			cache = (SimilarityCache) in.readObject();
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
