package org.mdeforge.presentation.rest;

import java.util.List;

import org.eclipse.m2m.atl.common.ATLExecutionException;
import org.eclipse.m2m.atl.core.ATLCoreException;
import org.mdeforge.business.ATLTransformationService;
import org.mdeforge.business.BusinessException;
import org.mdeforge.business.ProjectService;
import org.mdeforge.business.model.ATLTransformation;
import org.mdeforge.business.model.ATLTransformationError;
import org.mdeforge.business.model.ATLTransformationTestServiceError;
import org.mdeforge.business.model.GridFileMedia;
import org.mdeforge.business.model.Metric;
import org.mdeforge.business.model.Model;
import org.mdeforge.business.model.Project;
import org.mdeforge.business.model.Transformation;
import org.mdeforge.business.model.User;
import org.mdeforge.business.model.Workspace;
import org.mdeforge.business.model.wrapper.json.ArtifactList;
import org.mdeforge.business.model.wrapper.json.MetricList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;

;

@Controller
@RestController
@RequestMapping("/api/ATLTransformation")
public class ATLTransformationRESTController {

	@Autowired
	private ATLTransformationService ATLtransformationService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private User user;

	@RequestMapping(value="/{id_artifact}/metrics", method = RequestMethod.GET)
	public @ResponseBody HttpEntity<MetricList> getMetrics(@PathVariable("id_artifact") String idArtifact)
	{
		ATLTransformation emm = ATLtransformationService.findOne(idArtifact);
		List<Metric> lm = ATLtransformationService.calculateMetrics(emm);
		return new ResponseEntity<MetricList>(new MetricList(lm), HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody HttpEntity<ArtifactList> getTransformations() {
		//http://localhost:8080/mdeforge/api/metamodel/?access_token=40846e42-fc43-46df-ad09-982d466b8955
		ArtifactList result = new ArtifactList(ATLtransformationService
				.findAllWithPublicByUser(user));
		return new ResponseEntity<ArtifactList>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/public", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody HttpEntity<ArtifactList> getPublicTransformations() {
		List<ATLTransformation> list = ATLtransformationService.findAllPublic();
		return new ResponseEntity<ArtifactList>(new ArtifactList(list), HttpStatus.OK);
	}
	
	@RequestMapping(value = "execute/{id}", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody HttpEntity<ArtifactList> execute(@PathVariable("id") String id, 
			@RequestBody List<Model> models) {
		try {
			ATLTransformation transformation = ATLtransformationService.findOne(id);
			ArtifactList result = new ArtifactList(ATLtransformationService.execute(transformation, models, user));
			return new ResponseEntity<ArtifactList>(result, HttpStatus.OK);
		} catch (BusinessException e) {
			return new ResponseEntity<ArtifactList>(HttpStatus.UNPROCESSABLE_ENTITY);
		} catch (ATLExecutionException e) {
			return new ResponseEntity<ArtifactList>(HttpStatus.UNPROCESSABLE_ENTITY);
		} catch (ATLCoreException e) {
			return new ResponseEntity<ArtifactList>(HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}
		
	@RequestMapping(value = "/schema", method = RequestMethod.GET)
	public @ResponseBody HttpEntity<JsonSchema> getJsonSchema() {
		ObjectMapper MAPPER = new ObjectMapper();
		JsonSchemaGenerator generator = new JsonSchemaGenerator(MAPPER);
        try {
			JsonSchema jsonSchema = generator.generateSchema(Transformation.class);
			return new ResponseEntity<JsonSchema>(jsonSchema, HttpStatus.OK);
		} catch (JsonMappingException e) {
			return new ResponseEntity<JsonSchema>(HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

	@RequestMapping(value = "/shared", method = RequestMethod.GET)
	public @ResponseBody HttpEntity<ArtifactList> getTransformationsByUser() {
		ArtifactList list = new ArtifactList(ATLtransformationService
				.findAllWithPublicByUser(user));
		return new ResponseEntity<ArtifactList>(list, HttpStatus.OK);

	}

	@RequestMapping(value = "/workspace/{id_workspace}", method = RequestMethod.POST)
	public @ResponseBody HttpEntity<String> createArtifactInWorkspace(
			@ModelAttribute ATLTransformation transformation,
			@PathVariable("id_workspace") String idWorkspace,
			@RequestParam("_file") MultipartFile file) {
		try {
			Workspace workspace = new Workspace();
			workspace.setId(idWorkspace);
			transformation.setAuthor(user);
			transformation.getShared().add(user);

			GridFileMedia fileMedia = new GridFileMedia();
			fileMedia.setFileName(file.getName());
			fileMedia.setByteArray(file.getBytes());
			transformation.setFile(fileMedia);

			ATLtransformationService.create(transformation);
			return new ResponseEntity<String>("Transformation inserted.",
					HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("Erron: Project not inserted.",
					HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/project/{id_project}", method = RequestMethod.POST)
	public @ResponseBody HttpEntity<String> createArtifactInPoject(
			@ModelAttribute ATLTransformation ATLtransformation,
			@PathVariable("id_project") String idProject,
			@RequestParam("_file") MultipartFile file) {
		try {
			Project project = projectService.findById(idProject, user);
			ATLtransformation.getProjects().add(project);
			ATLtransformation.setAuthor(user);
			ATLtransformation.getShared().add(user);

			GridFileMedia fileMedia = new GridFileMedia();
			fileMedia.setFileName(file.getName());
			fileMedia.setByteArray(file.getBytes());
			ATLtransformation.setFile(fileMedia);

			ATLtransformationService.create(ATLtransformation);
			return new ResponseEntity<String>("Transformation inserted.",
					HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<String>(
					"Erron: Transformation not inserted.",
					HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

	// create transformation
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody HttpEntity<String> createArtifact(
			@RequestBody ATLTransformation transformation) {
		try {
			// SetAuthor
			transformation.setAuthor(user);
			// transformation save
			ATLtransformationService.create(transformation);
			return new ResponseEntity<String>("Transformation inserted.",
					HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>(
					"Erron: Transformation not inserted.",
					HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

	@RequestMapping(method = RequestMethod.PUT, consumes = "application/json")
	public @ResponseBody HttpEntity<String> updateArtifact(
			@RequestBody ATLTransformation transformation,
			@PathVariable("id_user") String idUser) {
		try {
			// GetUser
			// SetAuthor
			transformation.setAuthor(user);
			// add author to shared
			transformation.getShared().add(user);
			// transformation update
			ATLtransformationService.update(transformation);
			return new ResponseEntity<String>("Transformation updated.",
					HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>(
					"Erron: transformation not updated",
					HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

	@RequestMapping(value = "/{id_transformation}", method = RequestMethod.DELETE)
	public @ResponseBody HttpEntity<String> deleteTranformation(
			@PathVariable("id_metamodel") String idTranformation) {
		try {
			ATLTransformation art = ATLtransformationService.findOneById(idTranformation, user);
			ATLtransformationService.delete(art, user);
			return new ResponseEntity<String>("Transformation deleted",
					HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("Transformation deleted",
					HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}
	
	@RequestMapping(value = "/{id_artifact}", method = RequestMethod.GET)
	public @ResponseBody HttpEntity<ATLTransformation> getATLTransformation(@PathVariable("id_artifact") String idArtifact) {
		try {
			ATLTransformation artifact = ATLtransformationService.findOneById(idArtifact, user);
			return new ResponseEntity<ATLTransformation>(artifact, HttpStatus.OK);
		} catch (BusinessException e) {
			return new ResponseEntity<ATLTransformation>(HttpStatus.UNPROCESSABLE_ENTITY);
		}

	}
	@RequestMapping(value = "/anatlyzer/{id_artifact}", method = RequestMethod.GET)
	public @ResponseBody HttpEntity<List<ATLTransformationError>> anATLyzer(@PathVariable("id_artifact") String id) {
		try {
			ATLTransformation transformation = ATLtransformationService.findOneById(id, user);
			List<ATLTransformationError> errors = ATLtransformationService.anATLyzer(transformation);
			return new ResponseEntity<List<ATLTransformationError>>(errors, HttpStatus.OK);
		} catch (BusinessException e) {
			return new ResponseEntity<List<ATLTransformationError>>(HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}
	@RequestMapping(value = "/testerService/{id_artifact}", method = RequestMethod.GET)
	public @ResponseBody HttpEntity<List<ATLTransformationTestServiceError>> testerService(@PathVariable("id_artifact") String id
			) {
		try {
			ATLTransformation transformation = ATLtransformationService.findOneById(id, user);
			List<ATLTransformationTestServiceError> errors = ATLtransformationService.testServices(transformation);
			return new ResponseEntity<List<ATLTransformationTestServiceError>>(errors, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<ATLTransformationTestServiceError>>(HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}
	@RequestMapping(value = "/byname/{name}", method = RequestMethod.GET)
	public @ResponseBody HttpEntity<ATLTransformation> getATLTransformationByName(@PathVariable("name") String idArtifact) {
		try {
			ATLTransformation artifact = ATLtransformationService.findOneByName(idArtifact, user);
			return new ResponseEntity<ATLTransformation>(artifact, HttpStatus.OK);
		} catch (BusinessException e) {
			return new ResponseEntity<ATLTransformation>(HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}
}
