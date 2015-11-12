package org.mdeforge.presentation.frontend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mdeforge.business.ATLTransformationService;
import org.mdeforge.business.CRUDArtifactService;
import org.mdeforge.business.ContainmentRelationService;
import org.mdeforge.business.CosineSimilarityRelationService;
import org.mdeforge.business.DiceSimilarityRelationService;
import org.mdeforge.business.EcoreMetamodelService;
import org.mdeforge.business.GridFileMediaService;
import org.mdeforge.business.ModelService;
import org.mdeforge.business.SimilarityRelationService;
import org.mdeforge.business.UserService;
import org.mdeforge.business.model.Artifact;
import org.mdeforge.business.model.EcoreMetamodel;
import org.mdeforge.business.model.GridFileMedia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/public")
public class SearchPublicController {
	
	
	@Autowired
	private EcoreMetamodelService ecoreMetamodelService;
	@Autowired
	private SimilarityRelationService similarityRelationService;
	@Autowired
	private ContainmentRelationService containmentRelationService;
	@Autowired
	private CosineSimilarityRelationService cosineSimilarityRelationService;
	@Autowired
	private DiceSimilarityRelationService diceSimilarityRelationService;
	@Autowired
	private GridFileMediaService gridFileMediaService;
	@Autowired
	private UserService userService;
	@Autowired
	private CRUDArtifactService<Artifact> artifactService;
	@Autowired
	private ModelService modelService;
	@Autowired
	private ATLTransformationService aTLTransformationService;

//	@Autowired
//	private ConnectionFactoryRegistry facebook;
	
	
	@RequestMapping(value = "/social", method = { RequestMethod.GET })
	public @ResponseBody String getFracebookFriends(HttpServletRequest request, HttpServletResponse response) {
//		ConnectionFactory<Facebook> cf = facebook.getConnectionFactory(Facebook.class);
		//TODO 
//		OAuth2Operations oa2o = cf.getOAuthOperations();
//		Connection<Facebook> ccf = cf.createConnection(null);
//		ccf.createData().;
//		System.out.println(ccf.getImageUrl());
		return null;
	}
	
	private static final String[] ARTIFACT_TYPE = {"models", "transformations", "metamodels"};

	
	@RequestMapping(value = "/search", method = { RequestMethod.GET })
	public String search(Model model, @RequestParam(value = "search_string", required = false) String searchString, 
			@RequestParam(value = "artifactType", required = false) String artifactType) {
		List<Artifact> al = new ArrayList<Artifact>();
		String [] artifactTypes;
		if(searchString != null && searchString != ""){
			
			if (!(artifactType == null || artifactType.equals("")))
				artifactTypes = artifactType.split(",");
			else
				artifactTypes = ARTIFACT_TYPE;
			for (String string : artifactTypes) {
				if (string.equals("models"))
					al.addAll(modelService.orederedSearch(searchString));
				if (string.equals("transformations"))
					al.addAll(aTLTransformationService
							.orederedSearch(searchString));
				if (string.equals("metamodels"))
					al.addAll(ecoreMetamodelService
							.orederedSearch(searchString));
			}
		}
		Collections.sort(al, new Comparator<Artifact>(){
			@Override
			public int compare(Artifact o1, Artifact o2) {
				return o1.getScore().compareTo(o2.getScore());
			}
		  });
		Collections.reverse(al);
		
		if(searchString != null && searchString != "")
			model.addAttribute("artifactList", al);
		return "public.search";
	}
	
	
	@RequestMapping(value = "/search_metamodel_by_example/result", method = { RequestMethod.POST })
	public @ResponseBody List<EcoreMetamodel> searchEcoreMetamodelResultByExample(
			Model model,
			@RequestParam("metamodelfile") MultipartFile file) throws IOException {
		EcoreMetamodel m = new EcoreMetamodel();
		byte[] bytes = file.getBytes();
		GridFileMedia gfm = new GridFileMedia();
		gfm.setByteArray(bytes);
		gfm.setFileName("searchFragment.ecore");
		m.setFile(gfm);
		m.setName("searchFragment");        
		List<EcoreMetamodel> el = ecoreMetamodelService.searchByExample(m, 0.5);
		for (EcoreMetamodel ecoreMetamodel : el) {
			ecoreMetamodel.setFile(null);
		}
		return el;
	}
	
	
//	@RequestMapping(value = "/search_metamodel_by_example/result", method = { RequestMethod.POST })
	public String searchEcoreMetamodelResultByExampleTop5(
			Model model,
			@RequestParam("metamodelfile") MultipartFile file) throws IOException {
		EcoreMetamodel m = new EcoreMetamodel();
		byte[] bytes = file.getBytes();
		GridFileMedia gfm = new GridFileMedia();
		gfm.setByteArray(bytes);
		gfm.setFileName("searchFragment.ecore");
		m.setFile(gfm);
		m.setName("searchFragment");        
		List<EcoreMetamodel> el = ecoreMetamodelService.searchByExample(m, 0.5);
		for (EcoreMetamodel ecoreMetamodel : el) {
			ecoreMetamodel.setFile(null);
		}
		model.addAttribute("artifactListByExample", el);
		return "public.search";

	}
	

}
