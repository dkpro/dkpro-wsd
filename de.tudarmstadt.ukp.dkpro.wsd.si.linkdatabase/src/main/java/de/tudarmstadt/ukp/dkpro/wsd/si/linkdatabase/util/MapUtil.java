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

package de.tudarmstadt.ukp.dkpro.wsd.si.linkdatabase.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A utility class for operations on Maps
 * 
 * @author nico.erbs@gmail.com
 *
 */
public class  MapUtil{
	
	public static int countSumOfValues(Map<? extends Object,Long> map){
		int counter = 0;
		Iterator<Long> values = map.values().iterator();
		while (values.hasNext()){
			counter += values.next();
		}
		return counter;		
	}

	public static long getMaxValue(Map<? extends Object,Long> map){
		long max = 0;
		for(long value : map.values()){
			if(value>max){
				max = value;
			}
		}
		return max;		
	}

	public static List<Integer> getIdsAsInt(HashMap<Long, Long> linkTargets) {
		List<Integer> ids = new ArrayList<Integer>();
		for(long id : linkTargets.keySet()){
			ids.add((int) id);
		}
		return ids;
	}

}
