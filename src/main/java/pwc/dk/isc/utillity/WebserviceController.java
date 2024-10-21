package pwc.dk.isc.utillity;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebserviceController {

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "User is not found")
private class onNoUserException extends RuntimeException {}

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();

	private static ArrayList<HashMap<String,Object>> hnkdata = new ArrayList<HashMap<String,Object>>();

	WebserviceController() {
		HashMap<String,Object> d1 = new HashMap<String,Object>();
		ArrayList<String> a1 = new ArrayList<String>();
		a1.add("read");
		d1.put("id","Aaron.Nichols");
		d1.put("fornavn","Aaron");
		d1.put("efternavn","Nichols");
		d1.put("access",a1);
		hnkdata.add(d1);

		HashMap<String,Object> d2 = new HashMap<String,Object>();
		ArrayList<String> a2 = new ArrayList<String>();
		a2.add("write");
		a2.add("read");
		d2.put("id","Adam.Kennedy");
		d2.put("fornavn","Adam");
		d2.put("efternavn","Kennedy");
		d2.put("access",a2);
		hnkdata.add(d2);

	}

	@GetMapping("/extra")
	public String extra(@RequestParam(value = "name", defaultValue = "World") String name) {
		return "Hello " + name;
	}

	HashMap role(String name, String tag) {
		HashMap r = new HashMap();
		r.put("name",name);
		r.put("tag",tag);
		return r;
	}
	@GetMapping("/roles")
	public ArrayList<HashMap> getRoles() {
		ArrayList<HashMap> al = new ArrayList<HashMap>();

		al.add(role("read", "X"));
		al.add(role("write","Y"));
		al.add(role("publish","X"));
		al.add(role("00read","X"));
		al.add(role("00write","Y"));
		al.add(role("00publish","Y"));
		System.out.println("ROLES: " + al);
		return al;
	}
	@GetMapping("/identity")
	public ArrayList<HashMap<String,Object>> getIdentities()  {
		System.out.println("in test");
		return hnkdata;
	}
	@GetMapping("/identity/{id}")
	public HashMap<String,Object> getIdentity(@PathVariable("id") String id)  {
		System.out.println("get Identity " + id);
		HashMap<String,Object> result = null;
		for (HashMap<java.lang.String, java.lang.Object> l : hnkdata) {
			if(id.equals(l.get("id"))) {
				result= (HashMap<String, Object>) l;
			}
		}
		if(result == null) {
			throw new onNoUserException();

		} else {
		}
		return result;
	}
	@PostMapping("identity")

	public HashMap<String,Object> postIdentity(@RequestBody HashMap<String,Object> body) {
		HashMap<String,Object> result = null;
		System.out.println("postIdentity: " + body.toString());
		for (HashMap<java.lang.String, java.lang.Object> l : hnkdata) {
			if(body.get("id").equals(l.get("id"))) {
				result= (HashMap<String, Object>) l;
			}
		}
		if(result == null) {
			result = new HashMap<String,Object>();
			hnkdata.add(result);

		} 
		
		result.put("id",body.get("id"));
		result.put("fornavn",body.get("fornavn"));
		result.put("efternavn",body.get("efternavn"));
		result.put("access",body.get("access"));
		return result;

	}
	@PostMapping("identity/add")

	public HashMap<String, Object> addToIdentity(@RequestBody HashMap<String,Object> body) throws onNoUserException {
		HashMap<String,Object> result = null;
		for (HashMap<java.lang.String, java.lang.Object> l : hnkdata) {
			if(body.get("id").equals(l.get("id"))) {
				result= (HashMap<String, Object>) l;
			}
		}
		if(result == null) {
			throw new onNoUserException();

		} else {
			ArrayList<String> access = (ArrayList<String>)result.get("access");
			access.add((String)body.get("newaccess"));
		}
		System.out.println("AddEntitlement:" + result);
		return result;

	}


	////////////////////// Velocity engine endpoint
	@GetMapping("velocityTest")

	public String velocityTest(@RequestParam String id) throws onNoUserException {

		VelocityEngine velocityEngine = new VelocityEngine();
   /* first, we init the runtime engine.  Defaults are fine. */

        Velocity.init();

        /* lets make a Context and put data into it */

        VelocityContext context = new VelocityContext();

        context.put("name", "Velocity");
        context.put("project", "Jakarta");
		context.put("id",id);

        /* lets render a template */

        StringWriter w = new StringWriter();
		
		String s = "#set($str=\"hej\") #set($localeClass = $str.getClass().forName(\"java.util.UUID\"))\n" + //
						"random $localeClass.randomUUID()\n" + //
						"#set($str1 = \"HEJ${id}\")\n" + //
						"fromBytes $localeClass.nameUUIDFromBytes($str1.getBytes())";
        w = new StringWriter();
        Velocity.evaluate( context, w, "mystring", s );
        System.out.println(" string : " + w );
		
		
		return w.toString();

		

	}
	@PostMapping("velocity")

	public String velocity(@RequestBody HashMap<String,Object> body) throws onNoUserException {

		VelocityEngine velocityEngine = new VelocityEngine();
   /* first, we init the runtime engine.  Defaults are fine. */

        Velocity.init();

        /* lets make a Context and put data into it */

        VelocityContext context = new VelocityContext();

        context.put("name", "Velocity");
        context.put("project", "Jakarta");
		for(Map.Entry<String, Object> entry : ((HashMap<String, Object>) body.get("data")).entrySet()) {
			context.put(entry.getKey(),entry.getValue());

		}

        /* lets render a template */

        StringWriter w = new StringWriter();
		
/* 		String s = "#set($str=\"hej\") #set($localeClass = $str.getClass().forName(\"java.util.UUID\"))\n" + //
						"random $localeClass.randomUUID()\n" + //
						"#set($str1 = \"HEJ${id}\")\n" + //
						"fromBytes $localeClass.nameUUIDFromBytes($str1.getBytes())";
*/
		String s = (String)body.get("script");
        w = new StringWriter();
        Velocity.evaluate( context, w, "mystring", s );
        System.out.println(" string : " + w );
		
		
		return w.toString();

		

	}


}