package org.mdeforge.presentation.rest;

import java.util.List;

import org.mdeforge.business.BusinessException;
import org.mdeforge.business.CRUDArtifactService;
import org.mdeforge.business.ProjectService;
import org.mdeforge.business.SearchProvider;
import org.mdeforge.business.model.Artifact;
import org.mdeforge.business.model.EcoreMetamodel;
import org.mdeforge.business.model.User;
import org.mdeforge.business.model.wrapper.json.ArtifactList;
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
@RequestMapping("/api/Artifact")
public class ArtifactRESTController {

	@Autowired
	private ProjectService projectService;
	@Autowired
	private CRUDArtifactService<Artifact> artifactService;

	@Autowired
	private User user;

	// Get specified artifact
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody HttpEntity<List<Artifact>> getArtifacts() {
		List<Artifact> result = artifactService.findAllWithPublicByUser(user);
		return new ResponseEntity<List<Artifact>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/schema", method = RequestMethod.GET)
	public @ResponseBody HttpEntity<JsonSchema> getJsonSchema() {
		try {
			
			ObjectMapper MAPPER = new ObjectMapper();
			HyperSchemaFactoryWrapper visitor = new HyperSchemaFactoryWrapper();
	        MAPPER.acceptJsonFormatVisitor(MAPPER.constructType(Artifact.class), visitor);
	        JsonSchema jsonSchema = visitor.finalSchema();
	        try {
				MAPPER.writeValueAsString(jsonSchema);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return new ResponseEntity<JsonSchema>(jsonSchema, HttpStatus.OK);
		} catch (JsonMappingException e) {
			return new ResponseEntity<JsonSchema>(HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

	@RequestMapping(value = "/public", method = RequestMethod.GET)
	public @ResponseBody HttpEntity<List<Artifact>> getPublicArtifacts() {
		List<Artifact> list = artifactService.findAllPublic();
		return new ResponseEntity<List<Artifact>>(list, HttpStatus.OK);
	}

	// get shared artifact
	@RequestMapping(value = "/shared", method = RequestMethod.GET)
	public @ResponseBody HttpEntity<List<Artifact>> getArtifactsByUser() {
		List<Artifact> list = artifactService.findAllWithPublicByUser(user);
		return new ResponseEntity<List<Artifact>>(list, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id_artifact}", method = RequestMethod.GET)
	public @ResponseBody HttpEntity<Artifact> getArtifact(@PathVariable("id_artifact") String idArtifact) {
		try {
			Artifact artifact = artifactService.findOneById(idArtifact, user);
			return new ResponseEntity<Artifact>(artifact, HttpStatus.OK);
		} catch (BusinessException e) {
			return new ResponseEntity<Artifact>(HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}


	//create artifact index
	@RequestMapping(value = "/createIndex", method = RequestMethod.GET)
	public @ResponseBody HttpEntity<String> createIndex() {
		try {
			artifactService.createIndex();
			
			return new ResponseEntity<String>("OK", HttpStatus.OK);
		} catch (BusinessException e) {
			return new ResponseEntity<String>(HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

	// search artifacts ordered by score
	@RequestMapping(value = "/orederedSearch/{text}", method = RequestMethod.GET)
	public @ResponseBody HttpEntity<ArtifactList> orderedSearch(@PathVariable("text") String text) {
		try {
			SearchProvider searchProvider = (SearchProvider) artifactService;
			ArtifactList list = new ArtifactList(searchProvider.orederedSearch(text));
			
			return new ResponseEntity<ArtifactList>(list, HttpStatus.OK);
		} catch (BusinessException e) {
			return new ResponseEntity<ArtifactList>(HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}
	
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public @ResponseBody HttpEntity<String> delete(
			@PathVariable("id") String idArtifact) {
		try {
			artifactService.delete(idArtifact, user);
			return new ResponseEntity<String>("true",
					HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("false",
					HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}
	
	@RequestMapping(value = "/search/{search_string}", method = { RequestMethod.GET })
	public HttpEntity<ArtifactList> searchResult(
			@PathVariable(value = "search_string") String searchString) {
		ArtifactList artifacts = new ArtifactList(artifactService.search(searchString));
		return new ResponseEntity<ArtifactList> (artifacts,
				HttpStatus.OK);
	}
}
