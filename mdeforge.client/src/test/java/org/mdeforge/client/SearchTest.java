package org.mdeforge.client;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mdeforge.business.model.EcoreMetamodel;
import org.mdeforge.business.model.GridFileMedia;

public class SearchTest {

	private static MDEForgeClient c;

	@BeforeClass
	public static void setup() throws Exception {
		c = new MDEForgeClient("http://localhost:8080/mdeforge/", "Admin", "test123");
	}
	
	@Test
	public void getEcoreMetamodelsTest() throws Exception {
		System.out.println("START AT: " + new Date());
		EcoreMetamodel emm = new EcoreMetamodel();
		emm.setName("Example.ecore");
		List<String> tags = Arrays.asList("DB, DataBase, Data Base, Relational".split(","));
		emm.setTags(tags);
		emm.setDescription("Describes the basic structure of a general Relational DB");
		emm.setAuthors("Metamodels Authors");
		emm.setOpen(true);	
		GridFileMedia gfm = new GridFileMedia();
		gfm.setFileName("Example.ecore");
		gfm.setContent(MDEForgeClient.readFile("temp/Example.ecore"));
		emm.setFile(gfm);
		List<EcoreMetamodel> reuslt = c.searchEcoreMetamodelByExample(emm);
		for (EcoreMetamodel ecoreMetamodel : reuslt) {
			System.out.println(ecoreMetamodel.getName());
		}
		System.out.println("END AT: " + new Date());
	}
	
}
