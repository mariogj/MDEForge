package org.mdeforge.presentation.rest;

import java.util.List;

import org.mdeforge.business.BusinessException;
import org.mdeforge.business.EcoreMetamodelService;
import org.mdeforge.business.MetricProvider;
import org.mdeforge.business.ProjectService;
import org.mdeforge.business.SimilarityService;
import org.mdeforge.business.ValidateService;
import org.mdeforge.business.model.Artifact;
import org.mdeforge.business.model.EcoreMetamodel;
import org.mdeforge.business.model.Metric;
import org.mdeforge.business.model.User;
import org.mdeforge.business.model.wrapper.json.ArtifactList;
import org.mdeforge.business.model.wrapper.json.MetricList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.customProperties.HyperSchemaFactoryWrapper;

@Controller
@RestController
@RequestMapping("/api/EcoreMetamodel")
public class EcoreMetamodelsRESTController {

	@Autowired
	private EcoreMetamodelService ecoreMetamodelService;
	@Autowired
	private ProjectService projectService;

	@Autowired
	private User user;
	
	
//	@Autowired
//	private ValidateService validateService;
	


	@RequestMapping(value = "/similarity/{id_MM1}/{id_MM2}", method = RequestMethod.GET)
	public @ResponseBody HttpEntity<String> getSimilarity(
			@PathVariable("id_MM1") String id_MM1,
			@PathVariable("id_MM2") String id_MM2) {
		
		EcoreMetamodel mm1 = (EcoreMetamodel) ecoreMetamodelService.findOne(id_MM1);
		EcoreMetamodel mm2 = (EcoreMetamodel) ecoreMetamodelService.findOne(id_MM2);
		SimilarityService si = (SimilarityService) ecoreMetamodelService;
		String simiString = si.calculateSimilarity(mm1, mm2);
		return new ResponseEntity<String>(simiString, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/validate/{id_MM1}", method = RequestMethod.GET)
	public @ResponseBody HttpEntity<String> isValid(
			@PathVariable("id_MM1") String id_MM1) {
		
		ValidateService va = (ValidateService)ecoreMetamodelService;
		
		EcoreMetamodel mm1 = (EcoreMetamodel) ecoreMetamodelService.findOne(id_MM1);
		
		
		boolean v = va.isValid(mm1);
		//boolean v = validationService.isValid(null);
		return new ResponseEntity<String>(((v)?"Is valid":"Not valid"), HttpStatus.OK);
	}
	
	
	@RequestMapping(value="/{id_ecoreMetamodel}/metrics", method = RequestMethod.GET)
	public @ResponseBody HttpEntity<MetricList> getMetrics(@PathVariable("id_ecoreMetamodel") String idEcoreMetamodel)
	{
		EcoreMetamodel emm = (EcoreMetamodel) ecoreMetamodelService.findOne(idEcoreMetamodel);
		MetricProvider mp = (MetricProvider) ecoreMetamodelService;
		List<Metric> lm = mp.calculateMetrics(emm);
		return new ResponseEntity<MetricList>(new MetricList(lm), HttpStatus.OK);
	}
	// Get specified metamodel
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody HttpEntity<ArtifactList> getEcoreMetamodels() {
		ArtifactList result = new ArtifactList(ecoreMetamodelService.findAllWithPublicByUser(user));
		return new ResponseEntity<ArtifactList>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/schema", method = RequestMethod.GET)
	public @ResponseBody HttpEntity<JsonSchema> getJsonSchema() {
		try {
			
			ObjectMapper MAPPER = new ObjectMapper();
			HyperSchemaFactoryWrapper visitor = new HyperSchemaFactoryWrapper();
	        MAPPER.acceptJsonFormatVisitor(MAPPER.constructType(EcoreMetamodel.class), visitor);
	        JsonSchema jsonSchema = visitor.finalSchema();
	        try {
				MAPPER.writeValueAsString(jsonSchema);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new ResponseEntity<JsonSchema>(jsonSchema, HttpStatus.OK);
		} catch (JsonMappingException e) {
			return new ResponseEntity<JsonSchema>(HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

	@RequestMapping(value = "/public", method = RequestMethod.GET)
	public @ResponseBody HttpEntity<ArtifactList> getPublicEcoreMetamodels() {
		ArtifactList list = new ArtifactList(ecoreMetamodelService.findAllPublic());
		return new ResponseEntity<ArtifactList>(list, HttpStatus.OK);
	}

	// get shared metamodel
	@RequestMapping(value = "/shared", method = RequestMethod.GET)
	public @ResponseBody HttpEntity<ArtifactList> getEcoreMetamodelsByUser() {
		ArtifactList list = new ArtifactList(ecoreMetamodelService.findAllWithPublicByUser(user));
		return new ResponseEntity<ArtifactList>(new ArtifactList(list), HttpStatus.OK);
	}

	// Create metamodel
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody HttpEntity<Artifact> createArtifact(@RequestBody EcoreMetamodel ecoreMetamodel) {
		try {
			// SetAuthor
			ecoreMetamodel.setAuthor(user);
			// add author to shared
			// metamodel create
			Artifact s = ecoreMetamodelService.create(ecoreMetamodel);
			// Response success
			return new ResponseEntity<Artifact>(s, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Artifact>(HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

	// update metamodel
	@RequestMapping(method = RequestMethod.PUT, consumes = "application/json")
	public @ResponseBody HttpEntity<String> updateArtifact(@RequestBody EcoreMetamodel ecoreMetamodel) {
		try {
			// SetAuthor
			ecoreMetamodel.setAuthor(user);
			// add author to shared
			ecoreMetamodel.getShared().add(user);
			// metamodel save
			ecoreMetamodelService.update(ecoreMetamodel);
			return new ResponseEntity<String>("EcoreMetamodel updated.", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("Erron: metamodel not updated", HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

	// get single metamodel
	@RequestMapping(value = "/{id_ecoreMetamodel}", method = RequestMethod.GET)
	public @ResponseBody HttpEntity<EcoreMetamodel> getEcoreMetamodel(@PathVariable("id_ecoreMetamodel") String idEcoreMetamodel) {
		try {
			EcoreMetamodel ecoreMetamodel = ecoreMetamodelService.findOneBySharedUserEcore(idEcoreMetamodel, user);
			return new ResponseEntity<EcoreMetamodel>(ecoreMetamodel, HttpStatus.OK);
		} catch (BusinessException e) {
			return new ResponseEntity<EcoreMetamodel>(HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

	@RequestMapping(value = "/{id_ecoreMetamodel}", method = RequestMethod.DELETE)
	public @ResponseBody HttpEntity<String> deleteEcoreMetamodel(@PathVariable("id_ecoreMetamodel") String idEcoreMetamodel) {
		try {
			ecoreMetamodelService.delete(idEcoreMetamodel, user);
			return new ResponseEntity<String>("EcoreMetamodel deleted", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("EcoreMetamodel not deleted", HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

}
