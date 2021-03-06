package org.mdeforge.presentation.rest;

import java.util.List;

import org.mdeforge.business.BusinessException;
import org.mdeforge.business.WorkspaceService;
import org.mdeforge.business.model.Project;
import org.mdeforge.business.model.User;
import org.mdeforge.business.model.Workspace;
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

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;

@Controller
@RestController
@RequestMapping("/api/workspace")
public class WorkspaceRESTController {

	@Autowired
	private User user;
	@Autowired
	private WorkspaceService workspaceService;

	
	@RequestMapping(value="temp", method = RequestMethod.GET)
	public @ResponseBody HttpEntity<List<Workspace>> findAll() {
		List<Workspace> lists = workspaceService.findAll();
		return new ResponseEntity<List<Workspace>>(lists, HttpStatus.OK);
	}

	@RequestMapping(value = "/schema", method = RequestMethod.GET)
	public @ResponseBody HttpEntity<JsonSchema> getJsonSchema() {
		ObjectMapper MAPPER = new ObjectMapper();
		JsonSchemaGenerator generator = new JsonSchemaGenerator(MAPPER);
        try {
			JsonSchema jsonSchema = generator.generateSchema(Workspace.class);
			return new ResponseEntity<JsonSchema>(jsonSchema, HttpStatus.OK);
		} catch (JsonMappingException e) {
			return new ResponseEntity<JsonSchema>(HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody HttpEntity<List<Workspace>> findAllByUsername() {
		List<Workspace> lists = workspaceService.findByUser(user);
		return new ResponseEntity<List<Workspace>>(lists, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody HttpEntity<Workspace> findOne(
			@PathVariable("id") String id) {
		try {
			Workspace workspace = workspaceService.findById(id, user);
			return new ResponseEntity<Workspace>(workspace, HttpStatus.OK);
		} catch (BusinessException e) {
			return new ResponseEntity<Workspace>(
					HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}
	
	@RequestMapping(value = "/{id}/Project/", method = RequestMethod.GET)
	public @ResponseBody HttpEntity<List<Project>> findProjectInWorkspace(
			@PathVariable("id") String id) {
		try {
			List<Project> project = workspaceService.findProjectInWorkspace(id, user);
			return new ResponseEntity<List<Project>>(project, HttpStatus.OK);
		} catch (BusinessException e) {
			return new ResponseEntity<List<Project>>(
					HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}
	
	@RequestMapping(method = RequestMethod.POST, consumes="application/json")
	public @ResponseBody HttpEntity<String> create(
			@RequestBody Workspace workspace) {
		try
		{
			workspace.setOwner(user);
			workspaceService.create(workspace);
			return new ResponseEntity<String>("Workspace created.", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("Workspace not created.", HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

	@RequestMapping(method = RequestMethod.PUT, consumes="application/json")
	public @ResponseBody HttpEntity<String> update(
			@RequestBody Workspace workspace) {
		try
		{
			workspace.setOwner(user);
			workspaceService.update(workspace);
			return new ResponseEntity<String>("Workspace updated.", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("Workspace not updated.", HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

	
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
	public @ResponseBody HttpEntity<String> delete(
			@PathVariable("id") String id) {
		try {
			workspaceService.delete(id, user);
			return new ResponseEntity<String>("Workspaces deleted.",
					HttpStatus.OK);
		} catch (BusinessException e) {
			return new ResponseEntity<String>(HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}
}
