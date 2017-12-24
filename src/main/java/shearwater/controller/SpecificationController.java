package shearwater.controller;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@RestController
public class SpecificationController {
	/*
	 * Specification Controller for handling request from Front end
	 * Different Controllers defined: create, update, delete, run etc.
	 * Specifications are stored in the form of:
	 * HashMap <name (String), specification (JSONObject)>
	 */
	
	// specsMap HashMap for storing Specifications in the below form
	// Ex: {name: "dog", spec: ["string", "boof"]}
	// Ex: {name: "dogNew", spec: ["string", "before", "spec", "dog", "string", "after"]}
	static Map<String, JSONArray> specsMap = new HashMap<String, JSONArray>();
	
	/*
	 * Create Spec Controller
	 */
	
	// Through Crossorigin mapping to Front end 
	@CrossOrigin(origins = "http://localhost:8000")
	@RequestMapping(value = "/specs/controller/create", method = RequestMethod.POST)
	public boolean createSpecification(@RequestBody String payload) {
		try {
			System.out.println("Create Request");
			JSONObject jsonObj = new JSONObject(payload);
			jsonObj = parseInJsonArray(jsonObj); // Parsing JSON array in the appropriate format
			return createSpecificationDefinition(jsonObj);
		} catch (Exception e) {
			System.out.println("createSpec error catched");
			return false;
		}
	}

	/*
	 * Read Spec Controller
	 */
	
	@CrossOrigin(origins = "http://localhost:8000")
	@RequestMapping(value = "/specs/controller/read", method = RequestMethod.POST, produces = "application/json")
	public Map<String, String> readSpecification(@RequestBody String payload) {
		String specName = new String();
		try {
			System.out.println("Read Request");
			specName = payload;
			return readSpecificationDefinition(specName);
		} catch (JSONException je) {
			System.out.println("Read Spec error catched");
			return new HashMap<String, String>();
		}
	}
	
	/*
	 * Delete Spec Controller
	 */

	@CrossOrigin(origins = "http://localhost:8000")
	@RequestMapping(value = "/specs/controller/delete", method = RequestMethod.POST)
	public boolean deleteSpecification(@RequestBody String payload) {
		try {
			System.out.println("Delete Request");
			String specName = payload;
			return deleteSpecificationDefinition(specName);
		} catch (Exception e) {
			System.out.println("DeleteSpec error catched");
			return false;
		}
	}

	/*
	 * Run Spec Controller
	 */
	
	@CrossOrigin(origins = "http://localhost:8000")
	@RequestMapping(value = "/specs/controller/run", method = RequestMethod.POST, produces = "application/json")
	public Map<String, String> runSpecification(@RequestBody String payload) {
		String specName = new String();
		Map<String, String> hmapResponse = new HashMap<String, String>();
		try {
			System.out.println("Run Request");
			specName = payload;
			String response = runSpecificationDefinition(specName);
			hmapResponse.put("Response", response);
			return hmapResponse;
		} catch (JSONException je) {
			System.out.println("RunSpec error catched");
			return hmapResponse;
		}
	}
	
	@JsonSerialize
	public class EmptyJsonResponse {
	}

	/*
	 * Parsing JSON Array
	 */
	
	/*
	 * Helper function for
	 * Parsing front end JSON Object into appropriate format as follows:
	 * 
	 * {
	 * 	"name":"dogNew",
	 * 	"values":["before","dog","after"],
	 * 	"type":["string","spec","string"]
	 * }
	 * 
	 * to 
	 * {
	 * 	"name":"dogNew",
	 * 	"type":["string","before","spec","dog","string","after"]
	 * }
	 * 
	 */
	
	public JSONObject parseInJsonArray(JSONObject jsonObj){
		JSONObject newJsonObj = new JSONObject();
		newJsonObj.put("name", (String) jsonObj.get("name"));
		JSONArray jsonArr = new JSONArray();
		JSONArray specsType = jsonObj.getJSONArray("type");
		JSONArray specsValues = jsonObj.getJSONArray("values");
		for(int i = 0; i < specsValues.length(); i++){
			String type = (String)  specsType.get(i);
			jsonArr.put(type);
			String value = (String) specsValues.get(i);
			jsonArr.put(value);
		}
		newJsonObj.put("type", jsonArr);
		return newJsonObj;
	}
	
	
	/*
	 * Create Specification Definition
	 */
	
	public boolean createSpecificationDefinition(JSONObject jsonObject) {
		try {
			String name = (String) jsonObject.get("name");
			JSONArray specification = jsonObject.getJSONArray("type");
			// Creating HashMap Specification in the form of <name (String), specification (JSONObject)>
			specsMap.put(name, specification);
			return true;
		} catch (JSONException je) {
			System.out.println("Json exception occured");
			je.printStackTrace();
			return false;
		}
	}

	
	/*
	 * Delete Specification Definition
	 */
	
	public boolean deleteSpecificationDefinition(String name) {
		try {
			// if contains specification delete from hashmap
			if (specsMap.containsKey(name)) { 
				specsMap.remove(name);
				return true;
			}
			// else return false
			else {	
				return false;
			}
		} catch (JSONException je) {
			System.out.println("Delete JSON exception");
			je.printStackTrace();
			return false;
		}
	}

	/*
	 * Read Specification Definition
	 */
	public Map<String, String> readSpecificationDefinition(String name) {
		Map<String, String> hmapRead = new HashMap<String, String>();
		try {
			JSONArray specification = specsMap.get(name);
			// Creating Map of Specification in the form of ["dog" : "boof"]
			hmapRead.put(name, specification.toString()); 
		} catch (JSONException je) {
			System.out.println("Json exception occured");
			je.printStackTrace();
		}
		// returning the map of specification
		return hmapRead; 
	}

	/*
	 * Run Specification Definition
	 */
	public String runSpecificationDefinition(String name) {
		String specValueAll = new String();
		try {
			if(specsMap.containsKey(name)){
				JSONArray specification = specsMap.get(name);
				for (int i = 0; i < specification.length(); i = i + 2) {
					String specType = (String) specification.get(i);
					String specValue = (String) specification.get(i + 1);
					if (specType.equals("string"))
						specValueAll = specValueAll + " " + specValue;
					else if (specType.equals("spec")){
						// Resolving specifications recursively
						specValueAll = specValueAll + " " + 
								runSpecificationDefinition(specValue);
					}
				}
			}else
				return "Specification not found!";
		} catch (JSONException je) {
			System.out.println("Json exception occured");
			return "Specification not found!";
		}
		return specValueAll.trim();
	}
	
}