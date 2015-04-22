package org.mdeforge.business;

import java.util.List;

import org.mdeforge.business.model.Artifact;
import org.mdeforge.business.model.Metric;
import org.mdeforge.business.model.User;


public interface CRUDArtifactService <T extends Artifact> {
	T create(T art) throws BusinessException;
	void delete(String idArtifact, User idUser) throws BusinessException;
	void update(T artifact) throws BusinessException;
	List<T> findAll() throws BusinessException;
	T findOne(String id) throws BusinessException;	
	List<T> findAllWithPublicByUser(User user) throws BusinessException;
	List<T> findAllPublic() throws BusinessException;
	List<T> findArtifactInProject(String idProject, User user);
	List<T> findArtifactInWorkspace(String id, User user);
	boolean isArtifactInWorkspace(String idWorkspace, String idArtfact)	throws BusinessException;
	boolean isArtifactInProject(String idProject, String idArtfact) throws BusinessException;
	boolean isArtifactInUser(User idUser, String idArtfact)	throws BusinessException;
	boolean existRelation(String idTo, String idFrom) throws BusinessException;
	void delete(T artifact, User user) throws BusinessException;
	T findOneByOwner(String idArtifact, User idUser);
	T findOneById(String idArtifact, User idUser)
			throws BusinessException;
	List<Metric> findMetricForArtifact(Artifact a);
}