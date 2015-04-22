package org.mdeforge.client;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mdeforge.business.model.ETLTransformation;
import org.mdeforge.business.model.EcoreMetamodel;
import org.mdeforge.business.model.Project;
import org.mdeforge.business.model.Workspace;

public class CreateTest {

	private static MDEForgeClient c;

	@BeforeClass
	public static void setup() throws Exception {
		c = new MDEForgeClient("http://localhost:8080/mdeforge/", "test123", "test123");
	}
	
	@Ignore
	@Test
	public void addWorkspace() throws Exception {
		Workspace w = new Workspace();
		w.setName("Francesco WorkSpace");
		w.setDescription("Test Project");
		c.addWorkspace(w);
	}
	
//	@Ignore
//	@Test
//	public void addProject() throws Exception {
//		Project p = new Project();
//		p.setName("Progetto di prova");
//		p.setWorkspaces(new ArrayList<Workspace>());
//		List<Workspace> ws = c.getWorkspaces();
//		for (Workspace workspace : ws) {
//			p.getWorkspaces().add(workspace);
//		}
//		c.addProject(p);
//	}
	@Ignore
	@Test
	public void addEcoreMetamodel() throws Exception {
		EcoreMetamodel emm = new EcoreMetamodel();
		emm.setName("AndroidAppMM.ecore");
		emm.setOpen(false);	
		List<Workspace> worspaces = c.getWorkspaces();
		for (Workspace project : worspaces) {
			emm.getWorkspaces().add(project);
		}
		
		EcoreMetamodel emm2 = new EcoreMetamodel();
		emm2.setName("WebAppMM.ecore");
		emm2.setOpen(false);	
		for (Workspace project : worspaces) {
			emm2.getWorkspaces().add(project);
		}
		c.addEcoreMetamodel(emm,"temp/AndroidAppMM.ecore");
		c.addEcoreMetamodel(emm2,"temp/WebAppMM.ecore");	
//		ETLTransformation etl = new ETLTransformation();
//		etl.setName("Android2Web.etl");
//		etl.setOpen(false);
//		DomainConformToRelation dct = new DomainConformToRelation();
//		dct.setName("Source");
//		dct.setFromArtifact(etl);
//		dct.setToArtifact(emm);
//		CoDomainConformToRelation cdct = new CoDomainConformToRelation();
//		cdct.setName("Target");
//		cdct.setToArtifact(emm2);
//		cdct.setFromArtifact(etl);
//		etl.getRelations().add(dct);
//		etl.getRelations().add(cdct);
//		c.addETLTransformation(etl, "temp/Android2Web.etl");
		
	}
	@Ignore
	@Test
	public void testAddProjectToWorkspace() {
		try {
			Workspace w = c.getWorkspace("5514aa53d4c67eee3e2c1b12");
			Project p = new Project();
			p.setName("Test bascio");
			p.getWorkspaces().add(w);
			c.addProject(p);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void testArtifactToWorkspace() {
		try {
			Workspace w = c.getWorkspace("5514aa53d4c67eee3e2c1b12");
			ETLTransformation p = new ETLTransformation();
			p.setName("Android2Web");
			p.getWorkspaces().add(w);
			c.addETLTransformation(p, "temp/Android2Web.etl");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}